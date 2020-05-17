package com.example.ripay;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Context curContext;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String userNameString;
    private String nricString;
    private String newEmailString;
    private String newPasswordConfirmString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        curContext = this;

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        Intent prevRegistrationStep = getIntent();
        userNameString = prevRegistrationStep.getStringExtra("userName");
        nricString = prevRegistrationStep.getStringExtra("NRIC");
    }

    public void onRegisterCompletePress(View view) {
        EditText edtNewEmail = findViewById(R.id.edtNewEmail);
        EditText edtNewPassword = findViewById(R.id.edtNewPassword);
        EditText edtNewPasswordConfirm = findViewById(R.id.edtNewPasswordConfirm);

        newEmailString = edtNewEmail.getText().toString();
        String newPasswordString = edtNewPassword.getText().toString();
        newPasswordConfirmString = edtNewPasswordConfirm.getText().toString();

        if (newEmailString.isEmpty() || newPasswordString.isEmpty() || newPasswordConfirmString.isEmpty()) {
            FocusSnackbar.show(curContext, R.string.registration_missing_fields, findViewById(R.id.registrationScreen));
            return;
        }
        if (!newPasswordString.equals(newPasswordConfirmString)) {
            FocusSnackbar.show(curContext, R.string.registration_password_diff, findViewById(R.id.registrationScreen));
            return;
        }

        firebaseAccountCreation();
    }

    private void firebaseAccountCreation() {
        mAuth.createUserWithEmailAndPassword(newEmailString, newPasswordConfirmString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            try {
                                mambuClientAccountCreation(user);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    private void mambuClientAccountCreation(FirebaseUser currentUser) throws JSONException {
        String userUid = currentUser.getUid();

        JSONObject newClientTemplate = Constants.createNewClientTemplate();
        String[] userNameStringArr = userNameString.split(" ", 2);
        newClientTemplate.getJSONObject("client").put("firstName", userNameStringArr[0]);
        newClientTemplate.getJSONObject("client").put("lastName", userNameStringArr[1]);
        newClientTemplate.getJSONArray("idDocuments").getJSONObject(0).put("documentId", nricString);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json,application/json");
                RequestBody body = RequestBody.create(mediaType, newClientTemplate.toString());
                Request request = new Request.Builder()
                        .url("https://razerhackathon.sandbox.mambu.com/api/clients")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Basic VGVhbTY1OnBhc3MxNDIxRjY4QUQ0")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Cookie", "AWSALB=IbV8Q61zNMWqZjJ4P3QhMeD81MMHHtB2Eux9wozui2yE7+E/Us48leE4Kn4qjQSwRcb+U9wNOeMWcqKTFKO/Ut6OY2fvbpL8TkG3Lcz+76FErUMoRCAbT3/tEJww; AWSALBCORS=IbV8Q61zNMWqZjJ4P3QhMeD81MMHHtB2Eux9wozui2yE7+E/Us48leE4Kn4qjQSwRcb+U9wNOeMWcqKTFKO/Ut6OY2fvbpL8TkG3Lcz+76FErUMoRCAbT3/tEJww")
                        .build();

                try {
                    String resultString = client.newCall(request).execute().body().string();
                    mambuCurrentAccountCreation(resultString, userUid);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void mambuCurrentAccountCreation(String resultString, String userUid) throws JSONException {
        JSONObject createdClient = new JSONObject(resultString);
        Log.d("debug", resultString);

        // Store client ID and encoded key on firebase such that it is mapped to the client's login unique identifier
        String user_id = createdClient.getJSONObject("client").getString("id");
        String encodedKey = createdClient.getJSONObject("client").getString("encodedKey");
        String account_id = null;

        // Create mambu current account using encodedKey
        JSONObject newCurrentAccountTemplate = Constants.createNewCurrentAccountTemplate();
        newCurrentAccountTemplate.getJSONObject("savingsAccount").put("accountHolderKey", encodedKey);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json,application/json");
        RequestBody body = RequestBody.create(mediaType, newCurrentAccountTemplate.toString());
        Request request = new Request.Builder()
                .url("https://razerhackathon.sandbox.mambu.com/api/savings")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Basic VGVhbTY1OnBhc3MxNDIxRjY4QUQ0")
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", "AWSALB=XKt4B3ffdOVIRe9T7eWmRs16jlnPlKpKtWJcpTrkZN1IhiFGaiA6CcF8ds7Bw+vbtTNg/rvEDl7OkuQ7Pq2hxMnQ+A4X9qqZy3Dh+uTaBFvsnUXuqhrAcLZVYaXi; AWSALBCORS=XKt4B3ffdOVIRe9T7eWmRs16jlnPlKpKtWJcpTrkZN1IhiFGaiA6CcF8ds7Bw+vbtTNg/rvEDl7OkuQ7Pq2hxMnQ+A4X9qqZy3Dh+uTaBFvsnUXuqhrAcLZVYaXi")
                .build();
        try {
            String resultString2 = client.newCall(request).execute().body().string();
            Log.d("debug", resultString2);
            JSONObject jsonObject = new JSONObject(resultString2);
            account_id = jsonObject.getJSONObject("savingsAccount").getString("id");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> userLoginData = new HashMap<>();
        userLoginData.put(Constants.KEY_USER_ID, user_id);
        userLoginData.put(Constants.KEY_ACCOUNT_ID, account_id);
        db.collection(Constants.USER_LIST_COL).document(userUid).set(userLoginData);

        FocusSnackbar.show(curContext, R.string.registration_successful, findViewById(R.id.registrationScreen));

        Intent completeRegistrationIntent = new Intent(this, PrimaryActivity.class);
        completeRegistrationIntent.putExtra("uid", userUid);

        Intent finishLoginActivityIntent = new Intent("finish_registration");
        sendBroadcast(finishLoginActivityIntent);

        startActivity(completeRegistrationIntent);
        finish();
    }
}
