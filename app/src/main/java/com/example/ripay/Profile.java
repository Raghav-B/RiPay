package com.example.ripay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Profile extends AppCompatActivity {
    Context context;
    //GetUser
    //TextView username = (TextView) findViewById(R.id.username);
    //username.setText(user.name);

    //TextView amt = (TextView) findViewById(R.id.creditamt);
    //amt.setText(String.ValueOf(user.getAmt()))

    double amtcurrent = 0.00;

    private String account_id;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_profile);
        Button button = (Button) findViewById(R.id.but);
        context = this;

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        account_id = intent.getStringExtra("account_id");

        final TextView amtinv = (TextView) findViewById(R.id.creditamt);
        TextView usernameText = findViewById(R.id.username);
        usernameText.setText(userName);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://razerhackathon.sandbox.mambu.com/api/savings/" + account_id + "/") //Client id comes here...
                        .method("GET", null)
                        //.addHeader("", "")
                        .addHeader("Authorization", "Basic VGVhbTY1OnBhc3MxNDIxRjY4QUQ0")
                        .addHeader("Cookie", "AWSALB=XKt4B3ffdOVIRe9T7eWmRs16jlnPlKpKtWJcpTrkZN1IhiFGaiA6CcF8ds7Bw+vbtTNg/rvEDl7OkuQ7Pq2hxMnQ+A4X9qqZy3Dh+uTaBFvsnUXuqhrAcLZVYaXi; AWSALBCORS=XKt4B3ffdOVIRe9T7eWmRs16jlnPlKpKtWJcpTrkZN1IhiFGaiA6CcF8ds7Bw+vbtTNg/rvEDl7OkuQ7Pq2hxMnQ+A4X9qqZy3Dh+uTaBFvsnUXuqhrAcLZVYaXi")
                        .build();
                try {
                    String testString = client.newCall(request).execute().body().string();
                    Log.d("debug", testString);

                    JSONObject jsonObject = new JSONObject(testString);
                    String temp = jsonObject.getString("balance");
                    amtinv.setText("SGD " + jsonObject.getString("balance"));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openDeposit();
            }
        });
    }

    public void openDeposit() {
        Intent intent = new Intent(this, Deposit.class);
        intent.putExtra("account_id", account_id);
        startActivity(intent);
    }


}
