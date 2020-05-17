package com.example.ripay;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class User {
    private String uid;
    private String userName;
    private String user_id;
    private String account_id;

    public User(String uid) {
        this.uid = uid;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.USER_LIST_COL).document(this.uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user_id = documentSnapshot.getString(Constants.KEY_USER_ID);
                        account_id = documentSnapshot.getString(Constants.KEY_ACCOUNT_ID);
                    }
                });

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://razerhackathon.sandbox.mambu.com/api/clients/" + getUid())
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Basic VGVhbTY1OnBhc3MxNDIxRjY4QUQ0")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Cookie", "AWSALB=pdumk9i67AaJlH0kJn40FhYkxIs1DE34cU29fl7AaZownTl27lQrS2E+BWESAdwTY0OZF1kDkEOW7Q8RVNRT3an+O7stVqOa3kcg+GDQr9MuvrKtpN8wTAP3jiev; AWSALBCORS=pdumk9i67AaJlH0kJn40FhYkxIs1DE34cU29fl7AaZownTl27lQrS2E+BWESAdwTY0OZF1kDkEOW7Q8RVNRT3an+O7stVqOa3kcg+GDQr9MuvrKtpN8wTAP3jiev")
                        .build();
                try {
                    String resultString = client.newCall(request).execute().body().string();
                    JSONObject jsonObject = new JSONObject(resultString);
                    userName = jsonObject.getString("firstName") + " " + jsonObject.getString("lastName");
                    Log.d("debug", userName);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getUid() {
        return this.uid;
    }

    public String getAccount_id() {
        return this.account_id;
    }

    public String getUserName() {
        return userName;
    }
}
