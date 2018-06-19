package com.aquino.texasandroid.activities;

import android.app.Activity;
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
    private boolean mLastAttemptSuccess;

    public static final int REGISTER_CODE = 0;
    public static final String SUCCESS_EXTRA = "com.aquino.texasandroid.success_extra";
    public static final String EXIT_EXTRA = "com.aquino.texasandroid.exit_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginManager = TexasLoginManager.getInstance(this);
        setupView();
        if(savedInstanceState !=null){
            mLastAttemptSuccess = savedInstanceState.getBoolean("success");
            setupResult(mLastAttemptSuccess);
        }



    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("success",mLastAttemptSuccess);
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
                    Thread getToken = new Thread(() -> {
                        try {
                            Log.i(getClass().getName(),"Getting token");
                            loginManager.createValidToken(username,password);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    Log.d(getClass().getName(), "starting thread");
                    getToken.start();
                    getToken.join();
                    Log.d(getClass().getName(), "token thread joined");
                    if(loginManager.validToken()) {
                        Log.i(getClass().getName(),"Token is ready.");
                        mLastAttemptSuccess = true;
                        setupResult(true);
                        finish();
                    } else {
                        Log.i(getClass().getName(),"Token not valid");
                        setupResult(false);
                        mLastAttemptSuccess = false;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        mRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void setupResult(boolean outcome) {
        Intent intent = new Intent();

        intent.putExtra(SUCCESS_EXTRA, outcome);
        if(outcome) {
            setResult(Activity.RESULT_OK, intent);
        }else setResult(Activity.RESULT_CANCELED,intent);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra(EXIT_EXTRA, true);
        setResult(Activity.RESULT_OK,intent);
        super.onBackPressed();
    }
}
