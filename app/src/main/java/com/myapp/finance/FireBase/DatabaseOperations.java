package com.myapp.finance.FireBase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myapp.finance.Database;
import com.myapp.finance.display;
import com.myapp.finance.login;
import com.myapp.finance.sql;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DatabaseOperations {

    Context context;
    public DatabaseOperations(Context context){
        this.context = context;
    }

    sql s = new sql(context);
    Intent intent_name;

    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://financial-manager-7a019-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private DatabaseReference data = database.getReference();
    String date = s.date();

    public void login(Map<String, String> map){
        try {
            data.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String user = map.get("Username");
                    String passWord = map.get("Password");
                    if(snapshot.hasChild(user)){
                        String pass = snapshot.child(user).child("Password").getValue(String.class);
                        if(pass.equals(passWord)){
                            Toast.makeText(context, "Logged In...!", Toast.LENGTH_SHORT).show();
                            intent_name = new Intent();
                            intent_name.setClass(context.getApplicationContext(), Database.class);
                            context.startActivity(intent_name);
                            sql.setData("Username", user, context);
                            displayData("Expense", "24-10-2021", "25-10-2021");
                            ((Activity)context).finish();
                        }
                        else
                            Toast.makeText(context, "Incorrect Password...!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "Incrrect Username or Password...!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    s.show("Error Occured",error.toString(),"Ok");
                }
            });
        }
        catch (Exception e){
            Toast.makeText(context,"Incorrect Username or Password...!",Toast.LENGTH_SHORT).show();
        }
    }

    public void register(Map<String, String > map){
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String user = map.get("Username");
                String pass = map.get("Password");
                String mobile = map.get("MobileNumber");
                if(snapshot.hasChild(user))
                    Toast.makeText(context, "Username Exists already...!", Toast.LENGTH_SHORT).show();
                else {
                    data.child(user).setValue(map,(DatabaseError error, DatabaseReference ref) -> {
                        if(ref.getKey().equals(user)){
                            intent_name = new Intent();
                            intent_name.setClass(context.getApplicationContext(), login.class);
                            context.startActivity(intent_name);
                            ((Activity)context).finish();
                            Toast.makeText(context, "Values inserted Successfully...!", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(context, "Error in inserting Values...!", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                s.show("Error Occured", error.toString(), "Ok");
            }
        });
    }



    public void insertData(Map<String, Object> map, String option){
        String username = sql.getData("Username", context);
        data.child(username).child(option).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(!snapshot.hasChild(option)){
                    data.child(username).child(option).child(date).updateChildren(map, (DatabaseError error, DatabaseReference ref) -> {
                        if(ref.getKey().equals(date))
                            Toast.makeText(context, "Values inserted Successfully...!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Error in inserting Values...!", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                s.show("Error","Error "+error.toString(),"Ok");
            }
        });
    }

    public void displayData(String option, String startDate, String endDate) {
        String username = sql.getData("Username", context);
        System.out.println("Username is:"+username);
        JSONObject map = new JSONObject();
        JSONArray jrr = new JSONArray();
        data.child(username).child(option).orderByKey().startAt(startDate).endAt(endDate).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot snapshots : snapshot.getChildren()) {
                    String allDate = snapshots.getKey();
                    Object value = snapshots.getValue();
                    String con = String.valueOf(value);
                    String replace = con.replaceAll("\\{"," ");
                    String replacedString = replace.replaceAll("\\}"," ");
                    String[] split = replacedString.split(",");
                    System.out.println("All Dates "+allDate+" values "+split);
                    for (String a: split) {
                        String[] arr = a.split("=");
                        JSONObject jobj = new JSONObject();
                        System.out.println("Value is "+a);
                        try {
                            map.put(arr[0], arr[1]);
                            jobj.put("Date",allDate);
                            jobj.put(option+"Des",arr[0]);
                            jobj.put(option+"Amt",arr[1]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jrr.put(jobj);
                        System.out.println("Jobj is "+jobj);
                    }
                }
                System.out.println("Jarr is "+jrr);
                intent_name = new Intent();
                intent_name.putExtra("Jsondata", String.valueOf(jrr));
                intent_name.setClass(context.getApplicationContext(), display.class);
                context.startActivity(intent_name);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                s.show("Error", "Error is "+error,"Ok");
            }
        });
    }

    public void displayData(String startDate, String endDate){
        
    }
}
