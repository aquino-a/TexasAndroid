package com.aquino.texasandroid.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.aquino.texasandroid.R;
import com.aquino.texasandroid.TexasLoginManager;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private TexasLoginManager loginManager;
    private Button mSignIn, mRegister;
    private EditText mPassword, mUsername;

    public static final int REGISTER_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginManager = TexasLoginManager.getInstance(this);
        setupView();
    }

    private void setupView() {
        mSignIn = findViewById(R.id.sign_in_button);
        mRegister = findViewById(R.id.register_button);
        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                try {
                    loginManager.createValidToken(username,password);
                    if(loginManager.validToken()) {
                        Log.i(getClass().getName(),"Token is ready.");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegisterActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }
}
