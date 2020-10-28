package com.myapp.finance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

import androidx.annotation.RequiresApi;

public class ImageStorage  {

    String filename;

    public static String saveInternalStorage(Context context,Bitmap bitmap,String directory_name, String filename) {

        sql.setData("Profile_Pic",filename +".jpg",context);

        String stored = "Sorry the image already exists";

        File sdcard = context.getFilesDir();

        File folder = new File(sdcard.getAbsoluteFile(), "/"+directory_name+"/");
        if(!folder.exists())
        folder.mkdir();
        File file = new File(folder.getAbsoluteFile(), filename + ".jpg");


        if (file.exists())
            return stored ;

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            stored = "success";
        } catch (Exception e) {
            new sql(context).show("Error",e.toString(),"ok");
        }
        return stored;
    }

    public static File getImage(Context context,String directory_name,String imagename ) {

        File mediaImage = null;
        try {
            String root = Environment.getExternalStorageDirectory().getPath()+"/Financial Manager/"+directory_name;
            File myDir = new File(root);
            if (!myDir.exists())
                return mediaImage;

            mediaImage = new File(myDir.getPath() + "/"+directory_name+"/"+imagename);
        } catch (Exception e) {
            new sql(context).show("Error",e.toString(),"ok");
        }
        return mediaImage;
    }


    public static String profilePic(Context context, Bitmap bitmap, String directory_name, String filename){
        String result = "Failed";
            String path1 = Environment.getExternalStorageDirectory().getPath()+"/Financial Manager/"+directory_name;
            String path = android.os.Environment.getDataDirectory().getAbsolutePath();
            new sql(context).show("File path","Absolute path is:"+path,"ok");
            File profile = new File(path);
            if(!profile.exists())
                profile.mkdir();

            File pic = new File(profile.getAbsoluteFile(),filename+".jpg");
            sql.setData("Profile_Pic",filename+".jpg",context);
            if(pic.exists())
                pic.delete();

            try {
                FileOutputStream out = new FileOutputStream(pic);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                result = "success";
            } catch (Exception e) {
                new sql(context).show("Error",e.toString(),"ok");
            }
        return result;
    }
}
