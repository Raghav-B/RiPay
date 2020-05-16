package com.example.ripay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import android.util.Log;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private int loginView;
    private Context curContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginView = R.layout.activity_main;
        setContentView(loginView);
        curContext = this;

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) { // If user has previously signed in.
            FocusSnackbar.show(curContext, R.string.user_login_success, findViewById(R.id.loginScreen));
            Log.d("debug", "User name: " + currentUser.getEmail());
            mAuth.signOut(); // TODO REMOVE IN FINAL BUILD
            // TODO Add login function here
        }
    }

    public void onLoginButtonPress(View view) {
        EditText emailText = findViewById(R.id.edtLoginEmail);
        EditText passwordText = findViewById(R.id.edtLoginPassword);

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            FocusSnackbar.show(curContext, R.string.login_details_not_entered, findViewById(R.id.loginScreen));
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) { // User is not signed in.
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FocusSnackbar.show(curContext, R.string.user_login_success, findViewById(R.id.loginScreen));
                                FirebaseUser user = mAuth.getCurrentUser();
                                completeLogin(user);
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    // User does not exist
                                    FocusSnackbar.show(curContext, R.string.user_not_found_error, findViewById(R.id.loginScreen));
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    // Incorrect credentials
                                    FocusSnackbar.show(curContext, R.string.user_login_password_fail, findViewById(R.id.loginScreen));
                                } catch (Exception e) {
                                    Log.d("exception", e.getMessage());
                                }
                            }
                        }
                    });

        } else { // User is signed in (control shouldn't reach this part of code)
            Log.d("exception", "Unknown error");
        }
    }

    public void completeLogin(FirebaseUser currentUser) {
        Intent completeLoginIntent = new Intent(this, PrimaryActivity.class);
        Log.d("debug", "uid pre login: " + currentUser.getUid());
        completeLoginIntent.putExtra("uid", currentUser.getUid());
        startActivity(completeLoginIntent);
        finish();
    }

    public void onRegisterButtonPress(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        /*int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;*/
        return true;
    }
}
