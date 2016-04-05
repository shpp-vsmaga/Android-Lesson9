package com.shpp.sv.sqlite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    private UserDatabaseHelper dbHelper;
    private EditText edtUsername;
    private EditText edtPassword;
    private Spinner spnLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
    }

    private void init() {
        dbHelper = UserDatabaseHelper.getInstance(this);
        edtUsername = (EditText)findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        spnLocation = (Spinner) findViewById(R.id.spnLocation);
        String islands[] = dbHelper.getIslandsList();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, islands);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLocation.setAdapter(spinnerAdapter);
    }

    public void onBtnRegisterClick(View view) {
        String name = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();


        if (name.isEmpty()){
            Toast.makeText(this,
                    getResources().getString(R.string.msg_login_cnt_empty), Toast.LENGTH_LONG).show();
            return;
        }

        if (password.isEmpty()){
            Toast.makeText(this,
                    getResources().getString(R.string.msg_psw_cnt_empty), Toast.LENGTH_LONG).show();
            return;
        }

        if (name.contains(" ")){
            Toast.makeText(this,
                    getResources().getString(R.string.msg_login_cnt_spaces), Toast.LENGTH_LONG).show();
            return;
        }

        if (password.contains(" ")){
            Toast.makeText(this,
                    getResources().getString(R.string.msg_psw_cnt_spaces), Toast.LENGTH_LONG).show();
            return;
        }

        if (dbHelper.userIsExist(name)){
            Toast.makeText(this,
                    getResources().getString(R.string.msg_user_exists), Toast.LENGTH_LONG).show();
            return;
        }

        dbHelper.addUser(name, password, spnLocation.getSelectedItemPosition() + 1);
        Toast.makeText(this,
                getResources().getString(R.string.msg_reg_success), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
