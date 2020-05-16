package com.example.ripay;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

public class PrimaryActivity extends AppCompatActivity {

    private ArrayList<Business> al;
    private ArrayAdapter<Business> arrayAdapter;
    private int i;
    private ProgressBar mpb;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary);
        mpb = (ProgressBar) findViewById(R.id.pb);

        User x = new User("Vibhu", 1000);
        this.user = x;

        al = new ArrayList<>();
        Business u1 = new Business("Jay Gould", 1000, 0,
                30, 100, 500);
        Business u2 =  new Business("John Depp", 1000, 500,
                30, 100, 500);
        Business u3 =  new Business("Tom Jackson", 1000, 750,
                30, 100, 500);
        Business u4 =  new Business("Jon Stevenson", 1000, 300,
                30, 100, 500);
        Business u5 =  new Business("George Lucas", 1000, 0,
                30, 100, 500);

        al.add(u1);
        al.add(u2);
        al.add(u3);
        al.add(u4);
        al.add(u5);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.business_card, R.id.name, al);

        SwipeFlingAdapterView flingContainer = findViewById(R.id.swipeFrame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                //Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show(); //Some kind of display
                Business u = (Business) dataObject;
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                //Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                al.add(new Business("Done", 1000, 0,
                        30, 100, 500));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                //Doesn't do anything for now
                //FIX THIS!!!
            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                //Toast.makeText(MainActivity.this, "click", Toast.LENGTH_LONG).show();
                //oast.makeText(MainActivity.this, "Clicked!", Toast.LENGTH_LONG).show();
                Business u = (Business) dataObject;
                if(u.getCompanyname() != "Done") {
                    openActivity2((Business) dataObject);
                }
            }
        });
    }

    public void openActivity2(Business business) {
        Intent intent = new Intent(this, Details.class);
        intent.putExtra("id", business.getID());
        intent.putExtra("name", business.getCompanyname());
        intent.putExtra("cur", business.getcur());
        intent.putExtra("tot", business.gettot());
        intent.putExtra("bro", business.getBronzeval());
        intent.putExtra("sil", business.getSilverval());
        intent.putExtra("gold", business.getGoldval());
        intent.putExtra("invname", user.getName());
        intent.putExtra("invbal", user.getBalance());

        startActivity(intent);
    }

}
