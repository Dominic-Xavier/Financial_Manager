package com.myapp.finance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.myapp.finance.FireBase.DatabaseOperations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    Button b1,b2,b3;
    TextView t1,t2;
    Boolean bool = false;
    Map<String, String > map = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DatabaseOperations dos = new DatabaseOperations(this);

        b1 = findViewById(R.id.login);
        b2 = findViewById(R.id.can);
        b3 = findViewById(R.id.register);
        t1 = findViewById(R.id.user);
        t2 = findViewById(R.id.pass);
        AlertDialog.Builder b = new AlertDialog.Builder(login.this);


        b1.setOnClickListener((v) -> {
            String user = t1.getText().toString();
            String pass = t2.getText().toString();

            if(user.equals("") || pass.equals("")) {
                new sql(this).show("Error","Username or password is empty","Ok");
            }
            else {
                /*Background bg = new Background(this);
                String url = sql.ip("login");
                bg.execute(user, pass, url);
                bg.setUser_name(user);*/
                map.put("Username", user);
                map.put("Password", pass);
                dos.login(map);
            }
        });

        b2.setOnClickListener((v) -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(login.this)
            .setMessage("Are you sure want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No",null);
            AlertDialog al = alert.create();
            al.show();
        }
        );

        b3.setOnClickListener((v)->{
            startActivity(new Intent(login.this,Register.class));
            finish();
        });
    }

    public void onBackPressed()
    {
        AlertDialog.Builder b = new AlertDialog.Builder(login.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(login.this);
        alert.setMessage("Are you sure want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No",null);
        AlertDialog al = alert.create();
        al.show();
    }
}
