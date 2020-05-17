package com.example.ripay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Details extends AppCompatActivity {

    static int val = 0;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_card_details);
        Intent intent = getIntent();

        SeekBar sb = (SeekBar) findViewById(R.id.seekbar);
        //int value = sb.getProgress();
        TextView num = (TextView) findViewById(R.id.edt);
        num.setText("-");

        //String invname = intent.getStringExtra("invname");
        double invbal = intent.getDoubleExtra("invbal", 0);
        int bal = 100;

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
                //sendPayment(String userid, "ISBC756", val); //Add User and Biz details...
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


