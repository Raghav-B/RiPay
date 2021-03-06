package com.example.ripay;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

public class PrimaryActivity extends AppCompatActivity {

    private ArrayList<Business> al;
    private ArrayAdapter<Business> arrayAdapter;
    private User user;

    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary);

        Intent prevIntent = getIntent();
        user = new User(prevIntent.getStringExtra("uid"));

        al = new ArrayList<>();
        Business u1 = new Business("Barberrer", 1000, 0,
                30, 100, 500, R.drawable.barberrer);
        Business u2 =  new Business("Barby", 1000, 500,
                30, 100, 500, R.drawable.barby);
        Business u3 =  new Business("Caffee", 1000, 750,
                30, 100, 500, R.drawable.cafee);
        Business u4 =  new Business("Laundrix", 1000, 300,
                30, 100, 500, R.drawable.laundrix);
        Business u5 =  new Business("Primart", 1000, 0,
                30, 100, 500, R.drawable.primart);

        al.add(u1);
        al.add(u2);
        al.add(u3);
        al.add(u4);
        al.add(u5);

        arrayAdapter = new BusinessCardAdapter(this, al);

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
                //Business u = (Business) dataObject;
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                //Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                al.add(new Business("Done", 1000, 0,
                        30, 100, 500, R.drawable.ic_menu_send));
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
                if(u.getCompanyName() != "Done") {
                    openActivity2((Business) dataObject);
                }
            }
        });
    }

    public void openActivity2(Business business) {
        Intent intent = new Intent(this, Details.class);
        intent.putExtra("id", business.getID());
        intent.putExtra("name", business.getCompanyName());
        intent.putExtra("cur", business.getCurVal());
        intent.putExtra("tot", business.getTotalVal());
        intent.putExtra("bro", business.getBronzeVal());
        intent.putExtra("sil", business.getSilverVal());
        intent.putExtra("gold", business.getGoldVal());
        intent.putExtra("invname", user.getUserName());

        startActivity(intent);
    }

    public void onProfileButtonPress(View view) {
        Intent intent = new Intent(this, Profile.class);
        intent.putExtra("userName", user.getUserName());
        intent.putExtra("account_id", user.getAccount_id());
        startActivity(intent);
    }

    public void updateCard(Business business) {

    }

}
