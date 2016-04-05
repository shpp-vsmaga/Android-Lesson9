package com.shpp.sv.sqlite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private UserDatabaseHelper dbHelper;
    private SettingsHelper settingsHelper;
    private EditText edtUsername;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        loginSavedUser();

    }

    private void loginSavedUser() {

        int savedUserID = settingsHelper.getLoggedUserID();
        if (savedUserID != SettingsHelper.ERROR_ID){
            openMainActivity();
        }
    }


    private void init() {
        edtUsername = (EditText)findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        dbHelper = UserDatabaseHelper.getInstance(this);
        settingsHelper = SettingsHelper.getInstance(this);
    }

    public void onBtnRegisterClick(View view) {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }


    public void onBtnLoginClick(View view) {
        String name = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();


        if (accountIsValid(name, password)){
            login(name);
        }

    }

    private void login(String name) {
        settingsHelper.saveLoggedUser(dbHelper.getUserID(name));

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.msg_authenticating));
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        openMainActivity();
                        progressDialog.dismiss();
                    }
                }, 1000);
    }

    private void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private boolean accountIsValid(String name, String password) {
        if (name.isEmpty()){
            Toast.makeText(this,
                    getResources().getString(R.string.msg_name_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty()){
            Toast.makeText(this,
                    getResources().getString(R.string.msg_passw_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!dbHelper.userIsExist(name)){
            Toast.makeText(this,
                    getResources().getString(R.string.msg_name_not_found), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!dbHelper.passwordIsCorrect(name, password)){
            Toast.makeText(this,
                    getResources().getString(R.string.msg_wrong_pass), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        edtUsername.setText("");
        edtPassword.setText("");
    }
}
