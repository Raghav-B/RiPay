package com.example.ripay;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NRICActivity extends AppCompatActivity {

    private Context curContext;
    private CameraView camera;
    private TextView scanStatus;
    private Button continueRegistrationButton;

    private String userNameString;
    private String nricString;

    private EditText edtFullName;
    private EditText edtNRIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_r_i_c);
        curContext = this;

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_registration")) {
                    unregisterReceiver(this);
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_registration"));

        scanStatus = findViewById(R.id.scanStatus);
        edtFullName = findViewById(R.id.edtFullName);
        edtNRIC = findViewById(R.id.edtNRIC);
        continueRegistrationButton = findViewById(R.id.registerContinueButton);

        continueRegistrationButton.setVisibility(View.INVISIBLE);
        edtFullName.setVisibility(View.GONE);
        edtNRIC.setVisibility(View.GONE);

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
                        Log.d("debug", resultString);
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

        JSONObject prediction;
        JSONObject vision;
        JSONObject qualityCheck;

        try {
            prediction = jsonObject.getJSONObject("prediction");
            vision = jsonObject.getJSONObject("vision");
            qualityCheck = jsonObject.getJSONObject("qualityCheck");
        } catch (JSONException e) {
            findViewById(R.id.nricScanner).post(new Runnable() {
                @Override
                public void run() {
                    camera.clearFrameProcessors();
                    scanStatus.setText("API Limit Exceeded, please enter your details manually.");
                    edtFullName.setVisibility(View.VISIBLE);
                    edtNRIC.setVisibility(View.VISIBLE);
                    continueRegistrationButton.setVisibility(View.VISIBLE);
                }
            });
            return;
        }

        if (prediction.getString("type").equals("sg_id_front")) {
            if (qualityCheck.getBoolean("finalDecision")) {
                // Read NRIC number for now
                nricString = vision.getJSONObject("extract").getString("idNum");
                userNameString = vision.getJSONObject("extract").getString("name");

                Log.d("debug", "Details: " + nricString + ", " + userNameString);

                // TODO Add detected details focus snackbar

                findViewById(R.id.nricScanner).post(new Runnable() {
                    @Override
                    public void run() {
                        camera.clearFrameProcessors();
                        scanStatus.setText(R.string.nric_success);
                        continueRegistrationButton.setVisibility(View.VISIBLE);
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

    public void onRegistrationContinuePress(View view) {
        userNameString = edtFullName.getText().toString();
        nricString = edtNRIC.getText().toString();

        Intent continueRegistrationIntent = new Intent(this, RegisterActivity.class);
        continueRegistrationIntent.putExtra("userName", userNameString);
        continueRegistrationIntent.putExtra("NRIC", nricString);
        startActivity(continueRegistrationIntent);
    }
}
