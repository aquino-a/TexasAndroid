package com.aquino.texasandroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aquino.texasandroid.R;
import com.aquino.texasandroid.TexasRequestManager;
import com.aquino.texasandroid.model.NewUser;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    private Button mRegister;
    private EditText mUsername, mPassword, mPasswordConfirm, mEmail;
    private TexasRequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        requestManager = TexasRequestManager.getInstance("");

        setupView();
    }

    private void setupView() {
        mRegister = findViewById(R.id.register_button);
        mPassword = findViewById(R.id.password);
        mPasswordConfirm = findViewById(R.id.password_confirm);
        mUsername = findViewById(R.id.username);
        mEmail = findViewById(R.id.email);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String passwordConfirm = mPasswordConfirm.getText().toString();
                String email = mEmail.getText().toString();


                try {
                    if(username.isEmpty())
                        throw new IOException("No username");
                    if(!password.equals(passwordConfirm) || password.isEmpty())
                        throw new IOException("Check password.");
                    if(email.isEmpty() || !validate(email))
                        throw new IOException("Check Email");
                } catch(IOException e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }

                NewUser newUser = new NewUser(username,password,email);

                try {
                    requestManager.registerNewUser(newUser);
                    Toast.makeText(v.getContext(),
                            "Check your email for confirmation link,\nthen login."
                            ,Toast.LENGTH_SHORT).show();
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
