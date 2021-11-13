package com.myapp.finance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.myapp.finance.FireBase.DatabaseOperations;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class Register extends AppCompatActivity {

    private Button b1, b2;
    private EditText t1, t2, t3,t4;
    private String s1,s2,s3,s4;
    private Map<String, String> map = new HashMap<>();
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    DatabaseOperations operations = new DatabaseOperations(this);
    Intent intent;
    private static String systemCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();

        b1 =  findViewById(R.id.create);
        b2 =  findViewById(R.id.cancel);
        t1 =  findViewById(R.id.user);
        t2 =  findViewById(R.id.pass);
        t3 =  findViewById(R.id.repass);
        t4 =  findViewById(R.id.mob_num);

        b1.setOnClickListener((v) -> {
            s1 = t1.getText().toString();
            s2 = t2.getText().toString();
            s3 = t3.getText().toString();
            s4 = t4.getText().toString();
            int phone_number = s4.length();

                if (!s2.equals(s3) || s1.isEmpty() || s2.isEmpty() || s3.isEmpty() || s4.isEmpty() && phone_number==10) {
                    new sql(this).show("Error","Password Mismatch or empty values or Invalid number","Ok");
                    t1.setText("");
                    t2.setText("");
                    t3.setText("");
                    t4.setText("");
                } else {
                    //String url = sql.ip("register");;
                    //new Background(this).execute(s1,s2,url,s4);
                    map.put("Username", s1);
                    map.put("Password", s2);
                    map.put("MobileNumber", s4);
                    intent = new Intent(Register.this, VerifyPhoneNumber.class);
                    intent.putExtra("Datas", (Serializable) map);
                    startActivity(intent);
                    finish();
                }
        });

        b2.setOnClickListener((v)-> {
                startActivity(new Intent(Register.this,login.class));
                finish();
            });
    }

    public void onBackPressed() {
        startActivity(new Intent(Register.this,login.class));
        finish();
    }
}