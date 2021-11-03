package com.myapp.finance.FireBase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.finance.Database;
import com.myapp.finance.MainFragment;
import com.myapp.finance.display;
import com.myapp.finance.login;
import com.myapp.finance.sql;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class DatabaseOperations {

    Context context;
    public DatabaseOperations(Context context){
        this.context = context;
    }

    sql s = new sql(context);
    Intent intent_name;
    List<Date> col = new ArrayList<>();

    JSONArray jrr = new JSONArray();

    public JSONArray getJrr() {
        return jrr;
    }

    public void setJrr(JSONArray jrr) {
        this.jrr = jrr;
    }

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
                            ((Activity)context).finish();
                        }
                        else
                            Toast.makeText(context, "Incorrect Password...!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(context, "Incrrect Username or Password...!", Toast.LENGTH_SHORT).show();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void displayData(String option, String startDate, String endDate) {
        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String username = sql.getData("Username", context);
        System.out.println("Username is:"+username);

        //Code for getting all the dates
        data.child(username).child(option).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot snapshots : snapshot.getChildren()) {
                    String allDate = snapshots.getKey();
                    try {
                        //Strorig all the dates in an arraylist
                        col.add(sdf.parse(allDate));
                    } catch (ParseException e) {
                        s.show("Parsing error", e.getMessage(), "ok");
                    }
                }
                Collections.sort(col);
                int i=0;
                System.out.println("Collection is "+col);
                for (Date date : col) {
                    i++;
                    try {
                        System.out.println("Date is "+date+" My Date is "+sdf.parse(startDate));
                        if(date.after(sdf.parse(startDate)) && date.before(sdf.parse(endDate))){
                            System.out.println("Dates are "+date+" My Date is "+sdf.parse(startDate));
                            String newdate = dateConverter(date);
                            System.out.println("New Date Format is "+newdate);
                            int finalI = i;
                            data.child(username).child(option).child(newdate).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String allDate = snapshot.getKey();
                                    System.out.println("DAtes "+allDate);
                                    Object value = snapshot.getValue();
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
                                            jobj.put("Date",allDate);
                                            jobj.put(option+"Des", arr[0].trim());
                                            jobj.put(option+"Amt", arr[1].trim());
                                        } catch (JSONException e) {
                                            s.show("Json Error", e.getMessage(), "Ok");
                                        }
                                        jrr.put(jobj);
                                        if(finalI >= col.size()){
                                            intent_name = new Intent();
                                            intent_name.putExtra("Jsondata", String.valueOf(jrr));
                                            System.out.println("Jarr is "+getJrr());
                                            intent_name.setClass(context.getApplicationContext(), display.class);
                                            context.startActivity(intent_name);
                                            ((Activity)context).finish();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    s.show("Error", error.getMessage(), "Ok");
                                }
                            });
                        }

                    } catch (ParseException e) {
                        s.show("Parsing Error", e.getMessage(), "Ok");
                    }
                    catch (Exception el){
                        s.show("Error", el.getMessage(), "ok");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                s.show("Error", error.getMessage(),"Ok");
            }
        });
    }

    public void displayData(String startDate, String endDate){
        String username = sql.getData("Username", context);
        String Keyword;
        JSONArray jrr = new JSONArray();
        for (int i=0;i<2;i++){
            if(i==0)
                Keyword = "Expense";
            else
                Keyword = "Income";
            data.child(username).child(Keyword).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshots : snapshot.getChildren()) {
                        String keyword = snapshots.getKey();
                            String allDate = snapshots.getKey();
                            Object value = snapshots.getValue();
                            String con = String.valueOf(value);
                            String replace = con.replaceAll("\\{"," ");
                            String replacedString = replace.replaceAll("\\}"," ");
                            String[] split = replacedString.split(",");
                            System.out.println("All Dates "+allDate+" values "+split);
                            JSONObject jobj = new JSONObject();
                            for (String a: split) {
                                String[] arr = a.split("=");
                                System.out.println("Value is "+a);
                                try {
                                    jobj.put("Date",allDate);
                                    jobj.put(keyword+"Des",arr[0].trim());
                                    jobj.put(keyword+"Amt",arr[1].trim());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                jrr.put(jobj);
                            }
                            System.out.println("Jrr is "+jrr);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    s.show("Error", "Error is "+error,"Ok");
                }

            });
            if(jrr.equals(null) || jrr==null || jrr.length()==0){
                s.show("Sorry", "Jsonarray is empty", "Done");
                break;
            }
            intent_name = new Intent();
            intent_name.putExtra("Jsondata", String.valueOf(jrr));
            intent_name.setClass(context.getApplicationContext(), MainFragment.class);
            context.startActivity(intent_name);
        }
    }

    public void displayData(){
        String username = sql.getData("Username", context);
        JSONArray jrr = new JSONArray();
        data.child(username).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshots : snapshot.getChildren()) {
                    String keyword = snapshots.getKey();
                    if(keyword.equals("Income")){
                        data.child(username).child(keyword).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshots : snapshot.getChildren()) {
                                    String allDate = snapshots.getKey();
                                    Object value = snapshots.getValue();
                                    String con = String.valueOf(value);
                                    String replace = con.replaceAll("\\{"," ");
                                    String replacedString = replace.replaceAll("\\}"," ");
                                    String[] split = replacedString.split(",");
                                    System.out.println("All Dates "+allDate+" values "+split);
                                    JSONObject jobj = new JSONObject();
                                    for (String a: split) {
                                        String[] arr = a.split("=");
                                        System.out.println("Value is "+a);
                                        try {
                                            jobj.put("Date",allDate);
                                            jobj.put(keyword+"Des",arr[0]);
                                            jobj.put(keyword+"Amt",arr[1]);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        jrr.put(jobj);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                s.show("Error", error.getMessage(),"Ok");
                            }
                        });
                    }

                    if(keyword.equals("Expense")){
                        data.child(username).child(keyword).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshots : snapshot.getChildren()) {
                                    String allDate = snapshots.getKey();
                                    Object value = snapshots.getValue();
                                    String con = String.valueOf(value);
                                    String replace = con.replaceAll("\\{"," ");
                                    String replacedString = replace.replaceAll("\\}"," ");
                                    String[] split = replacedString.split(",");
                                    System.out.println("All Dates "+allDate+" values "+split);
                                    JSONObject jobj = new JSONObject();
                                    for (String a: split) {
                                        String[] arr = a.split("=");
                                        System.out.println("Value is "+a);
                                        try {
                                            jobj.put("Date",allDate);
                                            jobj.put(keyword+"Des",arr[0]);
                                            jobj.put(keyword+"Amt",arr[1]);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        jrr.put(jobj);
                                        System.out.println("Jobj is "+jrr);
                                    }
                                }
                                intent_name = new Intent();
                                intent_name.putExtra("Jsondata", String.valueOf(jrr));
                                intent_name.setClass(context.getApplicationContext(), MainFragment.class);
                                context.startActivity(intent_name);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                s.show("Error", error.getMessage(),"Ok");
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                s.show("Error", "Error is "+error,"Ok");
            }
        });
    }

    public String dateConverter(Date dates) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String format = formatter.format(dates);
        System.out.println(format);
        return format;
    }
}