package com.example.faten;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;

public class ViewImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        String imagePath = getIntent().getStringExtra("IMAGE_PATH");
        String title = getIntent().getStringExtra("TITLE");

        MaterialToolbar toolbar = findViewById(R.id.toolbarViewImage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        toolbar.setNavigationOnClickListener(v -> finish());

        ImageView imageView = findViewById(R.id.ivFullImage);

        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "Image introuvable", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}