package com.example.extratask1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by satori on 1/19/14.
 */
public class FullImageActivity extends Activity {
    private ImageView image;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullimage);
        image = (ImageView)findViewById(R.id.image);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        setImageSourceForPosition(position);
    }

    private void setImageSourceForPosition(int position) {
        File f = new File(MainActivity.put_context.getFilesDir(), "orig_" + position);
        if (f.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(MainActivity.put_context.getFilesDir().getPath() + "/orig_" + position);
            image.setImageBitmap(bm);

        }
        else {
            Toast.makeText(this, "Image is not available", 1000).show();
        }
    }
}