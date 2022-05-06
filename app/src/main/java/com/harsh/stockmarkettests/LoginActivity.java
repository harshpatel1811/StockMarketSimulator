package com.harsh.stockmarkettests;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextView signup, tv_welcome, tv_signin_line, tv_account_line, login;
    EditText email, password;
    Button signin_button, signup_button;
    FirebaseAuth mAuth;
    ProgressDialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signup = findViewById(R.id.tv_signup);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        signin_button = findViewById(R.id.signin_button);
        signup_button = findViewById(R.id.signup_button);
        tv_welcome = findViewById(R.id.tv_welcome);
        tv_signin_line = findViewById(R.id.tv_signin_line);
        tv_account_line = findViewById(R.id.tv_account_line);
        login = findViewById(R.id.tv_login);
        Activity activity = this;
      //  mdialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseHandler firebaseHandler = new FirebaseHandler(this,mAuth, activity);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        //Check if user is logged in or not


        if(firebaseUser!= null)
            startActivity(new Intent(LoginActivity.this, MainActivity2.class));


        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(LoginActivity.this, MainActivity2.class));
                /*
                mdialog.show();
                mdialog.setTitle("Logging");
                mdialog.setMessage("Please wait while we are logging");

                 */
                if(TextUtils.isEmpty(email.getText().toString()))
                {
                    email.setError("Email can not be empty");
                    email.requestFocus();

                }
                else if(TextUtils.isEmpty(password.getText().toString()))
                {
                    password.setError("Password can not be empty");
                    password.requestFocus();

                }
                else
                {
                    firebaseHandler.login(email.getText().toString(), password.getText().toString());
                }
            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(email.getText().toString()))
                {
                    email.setError("Email can not be empty");
                    email.requestFocus();

                }
                else if(TextUtils.isEmpty(password.getText().toString()))
                {
                    password.setError("Password can not be empty");
                    password.requestFocus();

                }
                else
                {
                    firebaseHandler.signup(email.getText().toString(), password.getText().toString());
                }
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_welcome.setText("Sign Up");
                tv_signin_line.setText("Sign Up to continue");
               // signin_button.setText("Sign Up");
                signin_button.setVisibility(View.GONE);
                signup_button.setVisibility(View.VISIBLE);
                signup_button.setText("Sign Up");
                signup.setText("Sign Up");
                signup.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
                login.setText("Login");
                tv_account_line.setText("Already have a account?");
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_welcome.setText("Welcome Back!");
                tv_signin_line.setText("Login to your account");
                signup_button.setVisibility(View.GONE);
                signin_button.setVisibility(View.VISIBLE);
                signin_button.setText("Sign in");
                //signin_button.setText("Login");
                signup.setVisibility(View.VISIBLE);
                login.setVisibility(View.GONE);
                tv_account_line.setText("Don't have a account?");
            }
        });


    }
}