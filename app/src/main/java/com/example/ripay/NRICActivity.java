package com.example.ripay;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import android.content.Context;
import android.content.Intent;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NRICActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Context curContext;
    private CameraView camera;
    private TextView scanStatus;
    private Button finishRegistrationButton;

    private String firstNameString;
    private String lastNameString;
    private String newEmailString;
    private String newPasswordConfirmString;
    private String nricString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_r_i_c);
        curContext = this;

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        Intent prevRegistrationStep = getIntent();
        firstNameString = prevRegistrationStep.getStringExtra("firstName");
        lastNameString = prevRegistrationStep.getStringExtra("lastName");
        newEmailString = prevRegistrationStep.getStringExtra("email");
        newPasswordConfirmString = prevRegistrationStep.getStringExtra("password");

        scanStatus = findViewById(R.id.scanStatus);
        finishRegistrationButton = findViewById(R.id.registerCompleteButton);
        finishRegistrationButton.setVisibility(View.INVISIBLE);

        camera = findViewById(R.id.cameraView);
        camera.setLifecycleOwner(this);
        camera.addFrameProcessor(new FrameProcessor() {
            @Override
            @WorkerThread
            public void process(@NonNull Frame frame) {
                if (frame.getDataClass() == byte[].class) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    YuvImage yuvImage = new YuvImage(frame.getData(), ImageFormat.NV21, frame.getSize().getWidth(), frame.getSize().getHeight(), null);
                    yuvImage.compressToJpeg(new Rect(0, 0, frame.getSize().getWidth(), frame.getSize().getHeight()), 90, out);
                    byte[] imageBytes = out.toByteArray();
                    String imageString = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("application/json,text/plain");
                    RequestBody body = RequestBody.create(mediaType, "{\r\n\t\"base64image\":\"" + imageString + "\"\r\n}");
                    Request request = new Request.Builder()
                            .url("https://niw1itg937.execute-api.ap-southeast-1.amazonaws.com/Prod/verify")
                            .method("POST", body)
                            .addHeader("x-api-key", "sWenMNn3MoFuUAW04AdG")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Content-Type", "text/plain")
                            .build();
                    try {
                        String resultString = client.newCall(request).execute().body().string();
                        //Log.d("debug", resultString);
                        checkFrame(resultString);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void checkFrame(String resultString) throws JSONException {
        JSONObject jsonObject = new JSONObject(resultString);

        JSONObject prediction = jsonObject.getJSONObject("prediction");
        JSONObject vision = jsonObject.getJSONObject("vision");
        JSONObject qualityCheck = jsonObject.getJSONObject("qualityCheck");

        if (prediction.getString("type").equals("sg_id_front")) {
            if (qualityCheck.getBoolean("finalDecision")) {
                // Read NRIC number for now
                nricString = vision.getJSONObject("extract").getString("idNum");

                findViewById(R.id.nricScanner).post(new Runnable() {
                    @Override
                    public void run() {
                        camera.clearFrameProcessors();
                        scanStatus.setText(R.string.nric_success);
                        finishRegistrationButton.setVisibility(View.VISIBLE);
                    }
                });

            } else {
                findViewById(R.id.nricScanner).post(new Runnable() {
                    @Override
                    public void run() {
                        scanStatus.setText(R.string.nric_unclear);
                    }
                });
            }
        } else { // Not the front
            findViewById(R.id.nricScanner).post(new Runnable() {
                @Override
                public void run() {
                    scanStatus.setText(R.string.nric_place_face_up);
                }
            });
        }
    }

    private void onRegistrationCompletePress(View view) {


        mAuth.createUserWithEmailAndPassword(newEmailString, newPasswordConfirmString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            FocusSnackbar.show(curContext, R.string.registration_successful, findViewById(R.id.registrationScreen));
                            completeRegistration(user);
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                // Weak password
                                FocusSnackbar.show(curContext, R.string.registration_weak_password, findViewById(R.id.registrationScreen));
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                // Wrong email
                                FocusSnackbar.show(curContext, R.string.registration_invalid_email, findViewById(R.id.registrationScreen));
                            } catch (FirebaseAuthUserCollisionException e) {
                                // Email already registered
                                FocusSnackbar.show(curContext, R.string.registration_email_exists, findViewById(R.id.registrationScreen));
                            } catch (Exception e) {
                                // Unknown exception
                                Log.d("exception", e.getMessage());
                            }
                        }
                    }
                });
    }

    private void completeRegistration(FirebaseUser currentUser) {
        Intent completeLoginIntent = new Intent(this, PrimaryActivity.class);
        completeLoginIntent.putExtra("uid", currentUser.getUid());

        Intent finishLoginActivityIntent = new Intent("finish_activity");
        sendBroadcast(finishLoginActivityIntent);

        startActivity(completeLoginIntent);
        finish();
    }
}
