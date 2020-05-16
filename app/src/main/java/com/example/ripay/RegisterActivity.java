package com.example.ripay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    private Context curContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        curContext = this;

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
            FocusSnackbar.show(curContext, R.string.login_details_not_entered, findViewById(R.id.registrationScreen));
            return;
        }
        if (!newPasswordString.equals(newPasswordConfirmString)) {
            FocusSnackbar.show(curContext, R.string.registration_password_diff, findViewById(R.id.registrationScreen));
            return;
        }

        mAuth.createUserWithEmailAndPassword(newEmailString, newPasswordConfirmString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            FocusSnackbar.show(curContext, R.string.registration_successful, findViewById(R.id.registrationScreen));
                            completeRegistration(user);
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                // Weak password
                                FocusSnackbar.show(curContext, R.string.registration_weak_password, findViewById(R.id.registrationScreen));
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                // Wrong email
                                FocusSnackbar.show(curContext, R.string.registration_invalid_email, findViewById(R.id.registrationScreen));
                            } catch (FirebaseAuthUserCollisionException e) {
                                // Email already registered
                                FocusSnackbar.show(curContext, R.string.registration_email_exists, findViewById(R.id.registrationScreen));
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

        Intent finishLoginActivityIntent = new Intent("finish_activity");
        sendBroadcast(finishLoginActivityIntent);

        startActivity(completeLoginIntent);
        finish();
    }
}
