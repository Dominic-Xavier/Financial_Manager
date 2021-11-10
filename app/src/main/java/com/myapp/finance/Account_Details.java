package com.myapp.finance;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Account_Details extends AppCompatActivity {

    Button quit;
    TextView name,mobile_number;
    ImageButton profile_button;
    ImageView profile_pic;
    final int IMAGE_CODE = 1;
    Uri image;
    ProgressBar progressBar;
    Bitmap bitmap;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    DatabaseReference reference = FirebaseDatabase.getInstance("https://financial-manager-7a019-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

    Handler handler = new Handler();
    TextView loadingImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_details);

        String u_id="",Name="",mob_num="";

        Intent in = getIntent();
        String data = in.getStringExtra("Jsondata");

        System.out.println("Account Details:"+data);

        progressBar = findViewById(R.id.progressBar);
        loadingImage = findViewById(R.id.loadingImage);

        handler.postAtFrontOfQueue(() -> {
            String Username = sql.getData("Username", this);
            String imageName = sql.getData("ImageName", this);
            if(Username!=null && !Username.equals("") && !Username.isEmpty()){
                if(imageName!=null)
                    downloadImage(imageName);
                else
                    getimageNames();
                displayData();
            }
        });

        name = findViewById(R.id.Name);
        mobile_number = findViewById(R.id.Mobile_number);
        quit = findViewById(R.id.quit);
        profile_pic = findViewById(R.id.profile_pic);
        profile_button = findViewById(R.id.profile_pic_button);

        profile_pic.setOnClickListener((View view) -> {
            try {
                Intent intent = new Intent(Account_Details.this, DisplayImage.class);
                System.out.println("BitMap is "+bitmap);
                intent.putExtra("Uri", image);
                intent.putExtra("bitMapImage", bitmap);
                startActivity(intent);
                finish();
            }
            catch (Exception e){
                new sql(this).show("Error", e.getMessage(), "Ok");
            }
        });

        profile_button.setOnClickListener((v)-> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.setType("image/*");
            if (Build.VERSION.SDK_INT >= 23) {
                startActivityForResult(intent,IMAGE_CODE);
                /*if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED ) {
                    startActivityForResult(intent,IMAGE_CODE);
                }
                else if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    new sql(this).show("Permission Denied","You need to grant permission to set profile pic. To grant permission Go to Settings-> App Manager -> Financial Manager and give permission","Ok");
                }*/

            } else {
                startActivityForResult(intent,IMAGE_CODE);
            }

        });


            /*JSONArray jrr = new JSONArray(data);
            for(int i=0;i<jrr.length();i++){
                JSONObject arr = jrr.getJSONObject(i);
                u_id = arr.getString("id");
                Name = arr.getString("Username");
                mob_num = arr.getString("Mobile_Number");
                id.setText(u_id);
                name.setText(Name);
                mobile_number.setText(mob_num);
            }
        } catch (JSONException e) {
            new sql(this).show("Error",e.toString(),"Ok");
        }*/

        quit.setOnClickListener((v)->{
            startActivity(new Intent(Account_Details.this,Database.class));
            finish();
        });
    }

    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(Account_Details.this,Database.class));
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_CODE && resultCode==RESULT_OK && null!=data){ //null!=data Can we give condition like this?
            try {
                image = data.getData();
                uploadImage();
            }
            catch (Exception e){}
            /*try {
                image = data.getData();
                ImageDecoder.Source profile_picture = ImageDecoder.createSource(this.getContentResolver(), image);
                Bitmap bitmap = ImageDecoder.decodeBitmap(profile_picture);
                String directory_name = sql.getData("User_name",this);
                String file_name = profilePicName(directory_name);
                new sql(this).show("Image Name","File name:"+file_name,"ok");
                String result = ImageStorage.profilePic(this,bitmap,directory_name,file_name);
                String PROFILE_PIC_NAME = sql.getData("Profile_Pic",this);
                Toast.makeText(this,"Image name is:"+PROFILE_PIC_NAME,Toast.LENGTH_LONG).show();
                File result1 = ImageStorage.getImage(this,directory_name,PROFILE_PIC_NAME);
                if(result.equals("success")) {
                    String name = result1.getAbsolutePath();
                    new sql(this).show("File path","Image name is:"+PROFILE_PIC_NAME,"ok");
                    profile_pic.setImageURI(Uri.parse(new File(name).toString()));
                    new sql(this).show("File object","File:"+new File(name).toString(),"ok");
                    new sql(this).show("Success", "Image stored Successfully", "Ok");
                }
                else
                    new sql(this).show("Failed",result,"Ok");

            } catch (IOException io) {
                new sql(this).show("Failed",io.toString(),"Ok");
            }
            catch (Exception ex){
                new sql(this).show("Failed",ex.toString(),"Ok");
            }*/
        }
    }

    public String profilePicName(String Image_Name){
        Random rand = new Random();
        int rand_int1 = rand.nextInt(1000);
        String file_name = Image_Name+rand_int1;
        return file_name;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String TAG = "Permsission : ";
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            //Define the path you want
            File mFolder = new File(Environment.getExternalStorageDirectory(), "Folder_Name");
            if (!mFolder.exists()) {
                boolean b = mFolder.mkdirs();
            }
        }
    }

    private String getFileExtension(Uri image) {
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap typeMap = MimeTypeMap.getSingleton();

        return typeMap.getExtensionFromMimeType(contentResolver.getType(image));
    }

    private void uploadImage(){
        progressBar.setVisibility(View.VISIBLE);
        loadingImage.setVisibility(View.VISIBLE);
        loadingImage.setText("Upoading Image");
        loadingImage.setTypeface(Typeface.DEFAULT_BOLD);
        loadingImage.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        String username = sql.getData("Username", this);
        if(image!=null){
            long milliSec =  System.currentTimeMillis();
            String imageName = milliSec+"."+getFileExtension(image);
            sql.setData("ImageName", imageName, this);
            StorageReference ref = storageReference.child("Profile Pics").child(username).child(imageName);
            ref.putFile(image).addOnCompleteListener((@NonNull Task<UploadTask.TaskSnapshot> task) -> {
                ref.getDownloadUrl().addOnSuccessListener((Uri uri) -> {
                    updateImageNames(imageName);
                    displayImage(image);
                    Toast.makeText(Account_Details.this, "Image is uploaded Successfully...!", Toast.LENGTH_SHORT).show();
                });
            });
        }
    }

    private void displayImage(Uri uri){
        profile_pic.setImageURI(uri);
        progressBar.setVisibility(View.GONE);
        loadingImage.setVisibility(View.GONE);
    }

    private void downloadImage(String ImageName){
        progressBar.setVisibility(View.VISIBLE);
        loadingImage.setVisibility(View.VISIBLE);
        loadingImage.setText("Loading Image");
        String username = sql.getData("Username", this);
        StorageReference ref = storageReference.child("Profile Pics").child(username).child(ImageName);
        ref.getBytes(1024*1024*5).addOnSuccessListener((byte[] bytes) -> {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            profile_pic.setImageBitmap(bitmap);
            progressBar.setVisibility(View.GONE);
            loadingImage.setVisibility(View.GONE);
        }).addOnFailureListener((@NonNull Exception e) -> {
            progressBar.setVisibility(View.GONE);
            loadingImage.setVisibility(View.GONE);
            new sql(Account_Details.this).show("Firebase Error", e.getMessage(), "Ok");
        });
    }

    private void displayData(){
        String UserName = sql.getData("Username",this);
        reference.child(UserName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    Object value = dataSnapshot.getValue();
                    if(key.equals("MobileNumber"))
                        mobile_number.setText(value.toString());
                    else if(key.equals("Username"))
                        name.setText(value.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new sql(Account_Details.this).show("FireBase Error", error.getMessage(),"Ok");
            }
        });
    }

    private void updateImageNames(String ImageName){
        String UserName = sql.getData("Username",this);
        Map<String, Object> map = new HashMap<>();
        map.put("ImageName", ImageName);
        reference.child(UserName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reference.child(UserName).updateChildren(map, (@Nullable DatabaseError error, @NonNull DatabaseReference ref) -> {
                    Toast.makeText(Account_Details.this, "Name inserted successfully", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getimageNames(){
        String UserName = sql.getData("Username",this);
        reference.child(UserName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    if(key.equals("ImageName")){
                        Object value = dataSnapshot.getValue();
                        downloadImage(value.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}