package com.myapp.finance.FireBase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.finance.Data;
import com.myapp.finance.Database;
import com.myapp.finance.FlowTracker;
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
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import androidx.annotation.NonNull;

public class DatabaseOperations {

    Context context;
    public DatabaseOperations(Context context){
        this.context = context;
    }

    sql s = new sql(context);
    Intent intent_name;
    List<Date> col = new ArrayList<>();
    DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    int exp = 0, exp1 = 0, inc = 0, inc1 = 0;
    static int count = 0;

    JSONArray jrr = new JSONArray();

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
                System.out.println("Map object is "+map);
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
                            Toast.makeText(context, "User Created Successfully...!", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(context, "Error in inserting Values...!", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
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


    public void displayData(String option, String startDate, String endDate) throws ParseException {

        String username = sql.getData("Username", context);
        System.out.println("Username is:"+username);
        Date stDate = StringToDateConverter(startDate);
        Date enDate = StringToDateConverter(endDate);

        //Code for getting all the dates
        data.child(username).child(option).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot snapshots : snapshot.getChildren()) {
                    String allDate = snapshots.getKey();
                    try {
                        Date date = StringToDateConverter(allDate);
                        if(date.after(stDate) && date.before(enDate))
                            //Storing all the dates which matches the criteria in a list
                            col.add(sdf.parse(allDate));
                        Log.i("Size of list", String.valueOf(col.size()));
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
                        System.out.println("Dates are "+date+" My Date is "+sdf.parse(startDate));
                        String newdate = dateConverter(date);
                        System.out.println("New Date Format is "+newdate);
                        int finalI = i;
                        data.child(username).child(option).child(newdate).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String allDate = snapshot.getKey();
                                Object value = snapshot.getValue();
                                if(value==null){
                                    intent_name = new Intent();
                                    intent_name.putExtra("Jsondata", String.valueOf(jrr));
                                    intent_name.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent_name.setClass(context.getApplicationContext(), display.class);
                                    context.startActivity(intent_name);
                                    ((Activity)context).finish();
                                    return;
                                }
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
                                    if(finalI == col.size()){
                                        System.out.println("Jrr Arr "+jrr);
                                        intent_name = new Intent();
                                        intent_name.putExtra("Jsondata", String.valueOf(jrr));
                                        intent_name.setClass(context.getApplicationContext(), display.class);
                                        intent_name.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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

                    } catch (ParseException e) {
                        s.show("Parsing Error", e.getMessage(), "Ok");
                    }
                    catch (Exception el){
                        s.show("Error", el.getMessage(), "ok");
                    }
                }
                intent_name = new Intent();
                intent_name.putExtra("Jsondata", String.valueOf(jrr));
                intent_name.setClass(context.getApplicationContext(), display.class);
                intent_name.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                ((Activity)context).finish();
                context.startActivity(intent_name);
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

        for (int i=0;i<2;i++){
            if(i==0)
                Keyword = "Expense";
            else
                Keyword = "Income";
            String finalKeyword = Keyword;
            data.child(username).child(finalKeyword).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshots : snapshot.getChildren()) {
                        String allDate = snapshots.getKey();
                        try {
                            count++;
                            Date date = StringToDateConverter(allDate);
                            if(date.after(sdf.parse(startDate)) && date.before(sdf.parse(endDate))){
                                data.child(username).child(finalKeyword).child(allDate).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        try {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                String des = dataSnapshot.getKey();
                                                Object value = dataSnapshot.getValue();
                                                JSONObject jobj = new JSONObject();
                                                jobj.put("Date",allDate);
                                                jobj.put(finalKeyword+"Des", des);
                                                jobj.put(finalKeyword+"Amt", value);
                                                jrr.put(jobj);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        System.out.println("JSON Arrays "+jrr);

                                        if(jrr==null)
                                            s.show("Sorry", "No Data in the specified date", "Ok");

                                        if(finalKeyword.equals("Income")){
                                            System.out.println("Jrrr Array "+jrr);
                                            intent_name = new Intent();
                                            intent_name.putExtra("Jsondata", String.valueOf(jrr));
                                            intent_name.setClass(context.getApplicationContext(), MainFragment.class);
                                            context.startActivity(intent_name);
                                            ((Activity)context).finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        s.show("Database Error", error.getMessage(), "Ok");
                                    }
                                });
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    s.show("Error", "Error is "+error,"Ok");
                }
            });
        }
        if(count==0) {
            intent_name = new Intent();
            intent_name.putExtra("Jsondata", String.valueOf(jrr));
            intent_name.setClass(context.getApplicationContext(), MainFragment.class);
            intent_name.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent_name);
            ((Activity)context).finish();
        }
    }

    public String dateConverter(Date dates) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String format = formatter.format(dates);
        System.out.println(format);
        return format;
    }

    public Date StringToDateConverter(String date) throws ParseException {
        Date date1=new SimpleDateFormat("dd-MM-yyyy").parse(date);
        return date1;
    }

    public void displayData() {
        String username = sql.getData("Username", context);
        String Keyword;

        for (int i=0;i<2;i++){
            if(i==0)
                Keyword = "Expense";
            else
                Keyword = "Income";
            String finalKeyword = Keyword;
            data.child(username).child(Keyword).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    count++;
                    //Parrent Loop for fetching date
                    for (DataSnapshot snapshots : snapshot.getChildren()) {
                        String allDate = snapshots.getKey();
                        data.child(username).child(finalKeyword).child(allDate).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    //Child Loop for fetching all the des and Amt
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Object value = dataSnapshot.getValue();
                                        /*if(finalKeyword.equals("Expense"))
                                            s.insertData(allDate, String.valueOf(value), "");
                                        else if(finalKeyword.equals("Income"))
                                            s.insertData(allDate, "", String.valueOf(value));*/
                                        /*String[] arr = allDate.split("-");
                                        String month = arr[1].trim();
                                        String year = arr[2].trim();
                                        String monthAndYear = month+"-"+year;*/
                                        JSONObject jobj = new JSONObject();
                                        jobj.put("monthAndYear", allDate);
                                        jobj.put(finalKeyword+"Amt", value);
                                        jrr.put(jobj);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                System.out.println("JSON Arrayyy "+jrr);
                                if(finalKeyword.equals("Income")){

                                    System.out.println("JSON Array is "+jrr);
                                    // Extract the JSONObjects
                                    List<JSONObject> list = new ArrayList();
                                    JSONArray sortedJsonArray = new JSONArray();
                                    for(int i = 0; i < jrr.length(); i++) {
                                        try {
                                            list.add(jrr.getJSONObject(i));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    System.out.println("Before Sorted JSONArray: " + jrr);
                                    final String KEY_NAME = "monthAndYear";
                                    Collections.sort(list, (JSONObject a, JSONObject b) -> {
                                        String str1 = null, str2 = null;
                                        try {
                                            str1 = a.getString(KEY_NAME);
                                            str2 = b.getString(KEY_NAME);
                                            System.out.println("STR 1 "+str1+" STR 2 "+str2);
                                            System.out.println("Returned Statement "+str1.compareTo(str2));
                                        } catch(JSONException e) {
                                            e.printStackTrace();
                                        }
                                        return str1.compareTo(str2);
                                    });

                                    List<Integer> totalExpense = new ArrayList<>();
                                    List<Integer> totalIncome = new ArrayList<>();

                                    Collections.sort(list, (JSONObject a, JSONObject b) -> {
                                        String str1 = null, str2 = null, expense = null, income = null, expense1 = null, income1 = null;
                                        try {
                                            str1 = a.getString(KEY_NAME);
                                            str2 = b.getString(KEY_NAME);
                                            try { expense = a.getString("ExpenseAmt");expense1 = b.getString("ExpenseAmt"); } catch (Exception e){ }

                                            try { income = a.getString("IncomeAmt");income1 = b.getString("IncomeAmt"); } catch (Exception e){ }

                                            if(str1.compareTo(str2)==0){
                                                try { exp = Integer.parseInt(expense);exp1 = Integer.parseInt(expense1); } catch (Exception e){ }
                                                try { inc = Integer.parseInt(income);inc1 = Integer.parseInt(income1); } catch (Exception e){ }
                                                totalExpense.add(exp+ exp1);
                                                totalIncome.add(inc + inc1);
                                            }
                                        } catch(JSONException e) {
                                            e.printStackTrace();
                                        }
                                        return str1.compareTo(str2);
                                    });

                                    System.out.println("Total Income "+totalIncome+" Total Expense"+totalExpense);

                                    for(int i = 0; i < jrr.length(); i++)
                                        sortedJsonArray.put(list.get(i));

                                    if(finalKeyword.equals("Income")){
                                        System.out.println("After Sorted JSONArray "+sortedJsonArray);
                                        intent_name = new Intent();
                                        intent_name.putExtra("Jsondata", String.valueOf(sortedJsonArray));
                                        intent_name.setClass(context.getApplicationContext(), FlowTracker.class);
                                        intent_name.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent_name);
                                        ((Activity)context).finish();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                s.show("Database Error", error.getMessage(), "Ok");
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    s.show("Error", "Error is "+error,"Ok");
                }
            });
        }
        if(count==0 || count==1) {
            intent_name = new Intent();
            intent_name.putExtra("Jsondata", String.valueOf(jrr));
            intent_name.setClass(context.getApplicationContext(), FlowTracker.class);
            intent_name.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent_name);
            ((Activity)context).finish();
        }
    }
}