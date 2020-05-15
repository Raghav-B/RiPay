package com.example.ripay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private int loginView;

    public void onLoginButtonPress(View view) {
        EditText emailText = findViewById(R.id.edtLoginEmail);
        EditText passwordText = findViewById(R.id.edtLoginPassword);

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(findViewById(R.id.loginScreen), R.string.login_details_not_entered,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) { // User is not signed in.
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(findViewById(R.id.loginScreen), R.string.user_login_success,
                                        Snackbar.LENGTH_LONG).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                // TODO ADD SIGNIN ACTIVITY
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    // User does not exist
                                    Snackbar.make(findViewById(R.id.loginScreen), R.string.user_not_found_error,
                                            Snackbar.LENGTH_LONG).show();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    // Incorrect credentials
                                    Snackbar.make(findViewById(R.id.loginScreen), R.string.user_login_password_fail,
                                            Snackbar.LENGTH_LONG).show();
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

    public void onRegisterButtonPress(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginView = R.layout.activity_main;
        setContentView(loginView);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) { // If user has previously signed in.
            Snackbar.make(findViewById(R.id.loginScreen), R.string.user_login_success,
                    Snackbar.LENGTH_LONG).show();
            Log.d("debug", "User name: " + currentUser.getEmail());
            mAuth.signOut(); // TODO REMOVE IN FINAL BUILD
        }
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
