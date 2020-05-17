package com.example.ripay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Deposit extends AppCompatActivity {
    int val = 0;

    private String account_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_deposit);

        Intent intent = getIntent();
        account_id = intent.getStringExtra("account_id");

        SeekBar sb = (SeekBar) findViewById(R.id.sb2);
        //int value = sb.getProgress();
        TextView num = (TextView) findViewById(R.id.textView5);
        num.setText("-");

        int bal = 100;  // Account balance


        sb.setMax(bal);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                TextView num = (TextView) findViewById(R.id.textView5);
                num.setText(value + "/" + seekBar.getMax() + " Credits To Be Added");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                TextView num = (TextView) findViewById(R.id.textView5);
                num.setText(value + "/" + seekBar.getMax() + " Credits Available");
                val = value;
            }
        });
        Button depo = (Button) findViewById(R.id.deposit);
        depo.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                OkHttpClient client = new OkHttpClient().newBuilder()
                                                        .build();
                                                MediaType mediaType = MediaType.parse("application/json,application/json");
                                                RequestBody body = RequestBody.create(mediaType, "{\n    \"amount\": " + val + ",\n    \"notes\": \"Deposit into savings account\",\n    \"type\": \"DEPOSIT\",\n    \"method\": \"bank\",\n    \"customInformation\": [\n        {\n            \"value\": \"unique identifier for receipt\",\n            \"customFieldID\": \"IDENTIFIER_TRANSACTION_CHANNEL_I\"\n        }\n    ]\n}");
                                                Request request = new Request.Builder()
                                                        .url("https://razerhackathon.sandbox.mambu.com/api/savings/" + account_id + "//transactions")
                                                        .method("POST", body)
                                                        .addHeader("Content-Type", "application/json")
                                                        .addHeader("Authorization", "Basic VGVhbTY1OnBhc3MxNDIxRjY4QUQ0")
                                                        .addHeader("Content-Type", "application/json")
                                                        .addHeader("Cookie", "AWSALB=XKt4B3ffdOVIRe9T7eWmRs16jlnPlKpKtWJcpTrkZN1IhiFGaiA6CcF8ds7Bw+vbtTNg/rvEDl7OkuQ7Pq2hxMnQ+A4X9qqZy3Dh+uTaBFvsnUXuqhrAcLZVYaXi; AWSALBCORS=XKt4B3ffdOVIRe9T7eWmRs16jlnPlKpKtWJcpTrkZN1IhiFGaiA6CcF8ds7Bw+vbtTNg/rvEDl7OkuQ7Pq2hxMnQ+A4X9qqZy3Dh+uTaBFvsnUXuqhrAcLZVYaXi")
                                                        .build();
                                                try {
                                                    String testString = client.newCall(request).execute().body().string();
                                                    Log.d("debug", testString);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        backtoMain();
                                    }

                                }
        );
    }

    public void backtoMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


}
