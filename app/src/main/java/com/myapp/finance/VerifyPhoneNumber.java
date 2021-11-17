package com.myapp.finance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.myapp.finance.FireBase.DatabaseOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneNumber extends AppCompatActivity {

    private PinView pinView;
    private AppCompatButton Verify;
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private TextView sendOTPAgain;
    private DatabaseOperations databaseOperations = new DatabaseOperations(this);
    private static String systemCode;
    private ProgressBar progressBar;
    private Map<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_phone_number);

        map = (HashMap<String, String>)getIntent().getSerializableExtra("Datas");
        String mobilenumber = "+91 "+map.get("MobileNumber");
        System.out.println("Mobile number is "+mobilenumber);

        pinView = findViewById(R.id.pin);
        Verify = findViewById(R.id.verify);
        sendOTPAgain = findViewById(R.id.click_here);
        progressBar = findViewById(R.id.loading);
        auth = FirebaseAuth.getInstance();
        Verify.setVisibility(View.GONE);

        sendVerificationCode(mobilenumber);

        sendOTPAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode(mobilenumber);
            }
        });

        Verify.setOnClickListener((View view) -> {
            String text = pinView.getText().toString();
            System.out.println("Text is "+text);;
            verifyCode(text, map);
        });
    }



    private void sendVerificationCode(String phone_number){
        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String credential = phoneAuthCredential.getSmsCode();
                verifyCode(credential, map);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                new sql(VerifyPhoneNumber.this).show("Error", e.getMessage(), "Ok");
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                //Send OTP Verification Code to another Activity
                systemCode = s;
                progressBar.setVisibility(View.GONE);
                Verify.setVisibility(View.VISIBLE);
                Toast.makeText(VerifyPhoneNumber.this, "OTP Sent Successfully..!", Toast.LENGTH_SHORT).show();
            }
        };

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone_number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(VerifyPhoneNumber.this)
                .setCallbacks(mCallBacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String codeByUser, Map<String, String> map){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(systemCode, codeByUser);
        signInByUserCredentials(credential);
    }

    private void signInByUserCredentials(PhoneAuthCredential credential){
        progressBar.setVisibility(View.VISIBLE);
        Verify.setVisibility(View.GONE);
        auth.signInWithCredential(credential)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(VerifyPhoneNumber.this, "Authenticated Successfully...!", Toast.LENGTH_SHORT).show();
                        System.out.println("Map obj is "+map);
                        databaseOperations.register(map);
                    }
                    else
                        new sql(VerifyPhoneNumber.this).show("Error", task.getException().getMessage(), "OK");
                    progressBar.setVisibility(View.GONE);
                    Verify.setVisibility(View.VISIBLE);
                }
            });
    }
}