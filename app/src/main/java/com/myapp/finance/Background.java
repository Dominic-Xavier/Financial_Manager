package com.myapp.finance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Background extends AsyncTask <String, Void,String> {
    String result = "";

    AlertDialog dialog;
    Context context;
    public Boolean login = false;

    public String getUser_name() { return User_name; }

    public void setUser_name(String user_name) { User_name = user_name; }

    String User_name;
    public Background(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Login Status");
    }

    @Override
    protected String doInBackground(String... voids) {

        String user = voids[0];
        String pass = voids[1];
        String Mobile_no="";

        String connstr = voids[2];
        String u="";

        try {

                if(connstr.equals("http://192.168.1.5/login.php"))
                {
                    u = connstr;
                    Mobile_no = "This is a blank variable";
                }
                else if(connstr.equals("http://192.168.1.5/register.php"))
                {
                    u = connstr;
                    Mobile_no = voids[3];
                }
                URL url = new URL(u);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String data = URLEncoder.encode("user","UTF-8")+"="+URLEncoder.encode(user,"UTF-8")
                        +"&&"+URLEncoder.encode("pass","UTF-8")+"="+URLEncoder.encode(pass,"UTF-8")
                        +"&&"+URLEncoder.encode("Mobile_no","UTF-8")+"="+URLEncoder.encode(Mobile_no,"UTF-8");
                writer.write(data);
                writer.flush();
                writer.close();


                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));
                String line ="";
                while ((line = reader.readLine()) != null)
                {
                    result += line;
                }
                reader.close();
                ips.close();
                ops.close();
                http.disconnect();
                return result;


            } catch (MalformedURLException e) {
                result = e.getMessage();
            } catch (IOException e) {
                result = e.getMessage();
            }
        return result;
        }


        @Override
        protected void onPostExecute(String s) {

        if(s.contains("u_id")) {
            String user = getUser_name();
            Intent intent_name = new Intent();
            intent_name.setClass(context.getApplicationContext(), Database.class);
            sql.setData("u_id",s,context);
            sql.setData("User_name",user,context);
            context.startActivity(intent_name);
            ((Activity)context).finish();
        }
        else if(s.equals("Registered Successfully")) {
            Intent intent_name = new Intent();
            intent_name.setClass(context.getApplicationContext(),login.class);
            context.startActivity(intent_name);
            ((Activity)context).finish();
            Toast toast=Toast.makeText(context.getApplicationContext(),"Successfully Registered",Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            new sql(context).show("Error",s,"Ok");
        }
    }
}