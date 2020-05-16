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

    private Context curContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        curContext = this;
    }

    public void onRegisterContinueButtonPress(View view) {
        EditText edtFirstName = findViewById(R.id.edtFirstName);
        EditText edtLastName = findViewById(R.id.edtLastName);
        EditText edtNewEmail = findViewById(R.id.edtNewEmail);
        EditText edtNewPassword = findViewById(R.id.edtNewPassword);
        EditText edtNewPasswordConfirm = findViewById(R.id.edtNewPasswordConfirm);

        String firstNameString = edtFirstName.getText().toString();
        String lastNameString = edtLastName.getText().toString();
        String newEmailString = edtNewEmail.getText().toString();
        String newPasswordString = edtNewPassword.getText().toString();
        String newPasswordConfirmString = edtNewPasswordConfirm.getText().toString();

        /*if (newEmailString.isEmpty() || newPasswordString.isEmpty() || newPasswordConfirmString.isEmpty() ||
                firstNameString.isEmpty() || lastNameString.isEmpty()) {
            FocusSnackbar.show(curContext, R.string.registration_missing_fields, findViewById(R.id.registrationScreen));
            return;
        }
        if (!newPasswordString.equals(newPasswordConfirmString)) {
            FocusSnackbar.show(curContext, R.string.registration_password_diff, findViewById(R.id.registrationScreen));
            return;
        }*/

        Intent continueLoginIntent = new Intent(this, NRICActivity.class);
        continueLoginIntent.putExtra("firstName", firstNameString);
        continueLoginIntent.putExtra("lastName", lastNameString);
        continueLoginIntent.putExtra("email", newEmailString);
        continueLoginIntent.putExtra("password", newPasswordConfirmString);
        startActivity(continueLoginIntent);
    }
}
