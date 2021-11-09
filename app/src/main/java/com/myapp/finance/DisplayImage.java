package com.myapp.finance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class DisplayImage extends AppCompatActivity {

    ScaleGestureDetector scaleGestureDetector;
    private Float FACTOR = 1.0f;
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_image);

        Intent intent = getIntent();
        String imageURI = intent.getStringExtra("Uri");
        Bitmap bitMap = (Bitmap)intent.getParcelableExtra("bitMapImage");

        imageView = findViewById(R.id.displayImage);
        if(imageURI!=null)
            imageView.setImageURI(Uri.parse(imageURI));
        else
            imageView.setImageBitmap(bitMap);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListner());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class ScaleListner extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            FACTOR *= detector.getScaleFactor();
            FACTOR = Math.max(1.0f, Math.min(FACTOR, 10.0f));
            imageView.setScaleX(FACTOR);
            imageView.setScaleY(FACTOR);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Account_Details.class));
        finish();
    }
}
