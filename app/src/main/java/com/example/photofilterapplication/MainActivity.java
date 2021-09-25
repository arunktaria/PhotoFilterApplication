package com.example.photofilterapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.VignetteSubFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    Bitmap bitmagglobal;
    ImageView fullimgview, filter1img, filter2img, filter3img, filter4img;
    ImageButton refreshbtn, downloadbtn;
    Button gallerybtn;
    File directory;
    String directorypath;
    OutputStream outputstream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshbtn = findViewById(R.id.refreshbtn);
        downloadbtn = findViewById(R.id.downloadbtn);

        //reset Image filter
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullimgview.setImageBitmap(bitmagglobal);
            }
        });

        //save image into our external/internal storage
        downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

        //getting path where to store the image
        directorypath = Environment.getExternalStorageDirectory().getAbsolutePath();
        directory = new File(directorypath, "/DCIM");
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 201);
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 202);
            }

        }

        bitmagglobal = BitmapFactory.decodeResource(getResources(), R.drawable.arun);
        //finding id's of itemview's
        gallerybtn = findViewById(R.id.gallerybtn);
        fullimgview = findViewById(R.id.bigimage);
        filter1img = findViewById(R.id.filter1);
        filter2img = findViewById(R.id.filter2);
        filter3img = findViewById(R.id.filter3);
        filter4img = findViewById(R.id.filter4);

        filter1img.setOnClickListener(this);
        filter2img.setOnClickListener(this);
        filter3img.setOnClickListener(this);
        filter4img.setOnClickListener(this);
        applyFilterOndemos();


        //for selecting image from gallery
        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 100);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 201) {
            Toast.makeText(MainActivity.this, "write granted!", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 202) {
            Toast.makeText(MainActivity.this, "read granted!", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //method for storing data into storage
    public void saveImage() {
        String imagename = "img-" + SystemClock.currentThreadTimeMillis() + "-filteredimg" + ".jpg";
        File file2 = new File(directory, imagename);
        try {
            outputstream = new FileOutputStream(file2);
            BitmapDrawable drawablet = (BitmapDrawable) fullimgview.getDrawable();
            Bitmap bitmaptemp = drawablet.getBitmap();
            bitmaptemp.compress(Bitmap.CompressFormat.JPEG, 100, outputstream);
            outputstream.flush();
            outputstream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Uri uri = data.getData();

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            fullimgview.setImageBitmap(bitmap);
            filter1img.setImageBitmap(bitmap);
            filter2img.setImageBitmap(bitmap);
            filter3img.setImageBitmap(bitmap);
            filter4img.setImageBitmap(bitmap);
            bitmagglobal = bitmap;


        } catch (IOException e) {
            e.printStackTrace();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    //on click listener of all imagebuttons
    //setting filter on image by pressing image btn's
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter1:
                Filter myFilter = new Filter();
                myFilter.addSubFilter(new BrightnessSubFilter(30));
                myFilter.addSubFilter(new ContrastSubFilter(1.1f));
                BitmapDrawable drawable = (BitmapDrawable) fullimgview.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                Bitmap fbitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Bitmap outputImage = myFilter.processFilter(fbitmap);
                fullimgview.setImageBitmap(outputImage);
                break;

            case R.id.filter2:
                Filter myFilter2 = new Filter();
                myFilter2.addSubFilter(new ContrastSubFilter(1.2f));
                BitmapDrawable drawable2 = (BitmapDrawable) fullimgview.getDrawable();
                Bitmap bitmap2 = drawable2.getBitmap();
                Bitmap fbitmap2 = bitmap2.copy(Bitmap.Config.ARGB_8888, true);
                Bitmap outputImage2 = myFilter2.processFilter(fbitmap2);
                fullimgview.setImageBitmap(outputImage2);

            case R.id.filter3:
                Filter myFilter3 = new Filter();
                myFilter3.addSubFilter(new SaturationSubFilter(1.2f));
                BitmapDrawable drawable3 = (BitmapDrawable) fullimgview.getDrawable();
                Bitmap bitmap3 = drawable3.getBitmap();
                Bitmap fbitmap3 = bitmap3.copy(Bitmap.Config.ARGB_8888, true);
                Bitmap outputImage3 = myFilter3.processFilter(fbitmap3);
                fullimgview.setImageBitmap(outputImage3);
                break;


            case R.id.filter4:
                Filter myFilter4 = new Filter();
                myFilter4.addSubFilter(new BrightnessSubFilter(10));
                BitmapDrawable drawable4 = (BitmapDrawable) fullimgview.getDrawable();
                Bitmap bitmap4 = drawable4.getBitmap();
                Bitmap fbitmap4 = bitmap4.copy(Bitmap.Config.ARGB_8888, true);
                Bitmap outputImage4 = myFilter4.processFilter(fbitmap4);
                fullimgview.setImageBitmap(outputImage4);
                break;


        }


    }

    //apply filter to sample filter image
    public void applyFilterOndemos() {
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(30));
        myFilter.addSubFilter(new ContrastSubFilter(1.1f));
        BitmapDrawable drawable = (BitmapDrawable) fullimgview.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Bitmap fbitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap outputImage = myFilter.processFilter(fbitmap);
        filter1img.setImageBitmap(outputImage);


        Filter myFilter2 = new Filter();
        myFilter2.addSubFilter(new ContrastSubFilter(1.2f));
        BitmapDrawable drawable2 = (BitmapDrawable) fullimgview.getDrawable();
        Bitmap bitmap2 = drawable2.getBitmap();
        Bitmap fbitmap2 = bitmap2.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap outputImage2 = myFilter2.processFilter(fbitmap2);
        filter2img.setImageBitmap(outputImage2);


        Filter myFilter3 = new Filter();
        myFilter3.addSubFilter(new SaturationSubFilter(1.2f));
        BitmapDrawable drawable3 = (BitmapDrawable) fullimgview.getDrawable();
        Bitmap bitmap3 = drawable3.getBitmap();
        Bitmap fbitmap3 = bitmap3.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap outputImage3 = myFilter3.processFilter(fbitmap3);
        filter3img.setImageBitmap(outputImage3);

        Filter myFilter4 = new Filter();
        myFilter4.addSubFilter(new BrightnessSubFilter(10));
        BitmapDrawable drawable4 = (BitmapDrawable) fullimgview.getDrawable();
        Bitmap bitmap4 = drawable4.getBitmap();
        Bitmap fbitmap4 = bitmap4.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap outputImage4 = myFilter4.processFilter(fbitmap4);
        filter4img.setImageBitmap(outputImage4);

    }


}