package com.example.ripay;

import androidx.appcompat.app.AppCompatActivity;
import java.lang.String;

import android.annotation.SuppressLint;
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

public class TransferComplete extends AppCompatActivity {
    static String frombankacc;
    static String tobankacc;
    static int value = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_complete);

        TextView amt = (TextView) findViewById(R.id.amount);
        //amt.setText(String.format("SGD %.2d", value));

        Button backtoswipe = (Button) findViewById(R.id.backtoswipe);
        backtoswipe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               openSwipe();
            }
        });

        //Intent intent = getIntent();
        //String userID = intent.getStringExtra("userid");
        //String bizID = intent.getStringExtra("bizid");
        //value = intent.getIntExtra("value", 0);

        //Step 1: Access the Current Account of the Investor
        //Take our userid and find the clientID for Mambu
        tobankacc = "\"KMIJ140\"";
        frombankacc = "\"YQFM001\"";
        value = 100;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json,application/json");
                RequestBody body = RequestBody.create(mediaType, "{\n\t\"type\": \"TRANSFER\",\n    \"amount\": \"100\",\n    \"notes\": \"Transfer to Expenses Account\",\n    \"toSavingsAccount\": \"KMIJ140\",\n    \"method\":\"bank\"\n}");
                Request request = new Request.Builder()
                        .url("https://razerhackathon.sandbox.mambu.com/api/savings/YQFM001/transactions")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Basic VGVhbTY1OnBhc3MxNDIxRjY4QUQ0")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Cookie", "AWSALB=phyVJNlQlyPcOFKeX7iE3wS3rQCctRcKznJiQdNunEd53pASHpk0KMs+lVVm0WHhkiuaSJzS5p+vkhl3nSc8SgGsnXrwxhCW67oAXAT2UZ70Qjb8yaMc7QbE1jXS; AWSALBCORS=phyVJNlQlyPcOFKeX7iE3wS3rQCctRcKznJiQdNunEd53pASHpk0KMs+lVVm0WHhkiuaSJzS5p+vkhl3nSc8SgGsnXrwxhCW67oAXAT2UZ70Qjb8yaMc7QbE1jXS")
                        .build();
                try {
                    String testString = client.newCall(request).execute().body().string();
                    Log.d("debug", testString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void openSwipe() {
        Intent i = new Intent(this, PrimaryActivity.class);
        startActivity(i);
    }

    public static class Details extends AppCompatActivity {

        static int val = 0;

        @SuppressLint("DefaultLocale")
        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.business_card_details);
            Intent intent = getIntent();




            //SEEKBAR

            SeekBar sb = (SeekBar) findViewById(R.id.seekbar);
            //int value = sb.getProgress();
            TextView num = (TextView) findViewById(R.id.edt);
            num.setText("-");




            String invname = intent.getStringExtra("invname");
            double invbal = intent.getDoubleExtra("invbal", 0);
            int bal = (int) invbal;



            sb.setMax(bal);
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int value = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    value = progress;
                    TextView num = (TextView) findViewById(R.id.edt);
                    num.setText(value + "/" + seekBar.getMax() + " Credits Available");
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //write custom code to on start progress
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    TextView num = (TextView) findViewById(R.id.edt);
                    num.setText(value + "/" + seekBar.getMax() + " Credits Available");
                    val = value;
                }



            });

            Button button = (Button) findViewById(R.id.sendMoneyButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ///
                    //sendPayment(String userid, String bizid, val); //Add User and Biz details...
                    ///
                    sendPayment(null, null, 1);
                }
            });

            //Text
            int id = intent.getIntExtra("id", 0);
            String name = intent.getStringExtra("name");
            TextView coyname = (TextView) findViewById(R.id.title);
            coyname.setText(name);
            TextView desc = (TextView) findViewById(R.id.desc);
            double curr = intent.getDoubleExtra("cur", 0);
            double tot = intent.getDoubleExtra("tot", 0);
            double rem = tot - curr;
            double bro = intent.getDoubleExtra("bro", 0);
            double sil = intent.getDoubleExtra("sil", 0);
            double gold = intent.getDoubleExtra("gold", 0);

            String str = "";
            str = String.format("%s requires %.2f and currently has %.2f. " +
                    "They require %.2f more credits to achieve their goal. The Bronze Tier requires" +
                    " %.2f credits, the Silver Tier requires %.2f credits and the Gold Tier requires %.2f" +
                    " credits. Help support %s today!", name, tot, curr, rem, bro, sil, gold, name );

                desc.setText(String.valueOf(str));


            }

            public void sendPayment(String userid, String bizid, int value) {
                Intent payment = new Intent(this, TransferComplete.class);
                payment.putExtra("userid", userid);
                payment.putExtra("bizid", bizid);
                payment.putExtra("value", value);
                startActivity(payment);
                finish();
            }


    }
}
