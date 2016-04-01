package com.shpp.sv.sqlite;

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

    private static final String LOG_TAG = "svcom";
    private UserDatabaseHelper dbHelper;
    private SettingsHelper settingsHelper;
    private EditText edtUsername;
    private EditText edtPassword;

    private static final String ISLANDS_FILE_NAME = "islands.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        addIslandsToDB();
        loginSavedUser();

    }

    private void loginSavedUser() {
        String savedUserName = settingsHelper.getLoggedUserName();
        if (!savedUserName.isEmpty()){
            login(savedUserName);
        }
    }

    private void addIslandsToDB() {

        if (!dbHelper.islandsIsInDB()){
            ArrayList<String> islands = readIslandsFromFile();
            dbHelper.addIslandsToBase(islands);
        }


    }

    private ArrayList<String> readIslandsFromFile() {
        ArrayList<String> islands = new ArrayList<>();
        String line;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().
                    open(ISLANDS_FILE_NAME)));

            line = br.readLine();
            while (line != null){
                islands.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return islands;
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

        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
    }

    private void login(String name) {
        settingsHelper.saveLoggedUser(name, dbHelper.getUsersLocation(name));
        openMainActivity();
    }

    private void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private boolean accountIsValid(String name, String password) {
        if (name.isEmpty()){
            Toast.makeText(this, "User name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty()){
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!dbHelper.userIsExist(name)){
            Toast.makeText(this, "User name not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!dbHelper.passwordIsCorrect(name, password)){
            Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
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
