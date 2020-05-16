package com.example.ripay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut(); // Signing out just in case.
    }

    public void onRegistrationCompleteButtonPress(View view) {
        EditText edtNewEmail = findViewById(R.id.edtNewEmail);
        EditText edtNewPassword = findViewById(R.id.edtNewPassword);
        EditText edtNewPasswordConfirm = findViewById(R.id.edtNewPasswordConfirm);

        String newEmailString = edtNewEmail.getText().toString();
        String newPasswordString = edtNewPassword.getText().toString();
        String newPasswordConfirmString = edtNewPasswordConfirm.getText().toString();

        if (newEmailString.isEmpty() || newPasswordString.isEmpty() ||
                newPasswordConfirmString.isEmpty()) {
            Snackbar.make(findViewById(R.id.registrationScreen), R.string.login_details_not_entered,
                    Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!newPasswordString.equals(newPasswordConfirmString)) {
            Snackbar.make(findViewById(R.id.registrationScreen), R.string.registration_password_diff,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(newEmailString, newPasswordConfirmString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Snackbar.make(findViewById(R.id.registrationScreen), R.string.registration_successful,
                                    Snackbar.LENGTH_LONG).show();
                            completeRegistration(user);
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                // Weak password
                                Snackbar.make(findViewById(R.id.registrationScreen), R.string.registration_weak_password,
                                        Snackbar.LENGTH_LONG).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                // Wrong email
                                Snackbar.make(findViewById(R.id.registrationScreen), R.string.registration_invalid_email,
                                        Snackbar.LENGTH_LONG).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                // Email already registered
                                Snackbar.make(findViewById(R.id.registrationScreen), R.string.registration_email_exists,
                                        Snackbar.LENGTH_LONG).show();
                            } catch (Exception e) {
                                // Unknown exception
                                Log.d("exception", e.getMessage());
                            }
                        }
                    }
                });
    }

    public void completeRegistration(FirebaseUser currentUser) {
        Intent completeLoginIntent = new Intent(this, PrimaryActivity.class);
        completeLoginIntent.putExtra("uid", currentUser.getUid());
        startActivity(completeLoginIntent);
        finish();
    }

}
