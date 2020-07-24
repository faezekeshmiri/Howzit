package com.example.faridam.howzit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.UUID;
import android.provider.Settings.Secure;

/**
 * Created by farida.M on 6/27/2020.
 */

public class
LoginActivity extends AppCompatActivity {
    ImageButton pickImg;
    EditText name;
    ImageView profile;
    Button enter;
    private static final int PICK_IMAGE=100;
    String imgPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        pickImg = (ImageButton)findViewById(R.id.imageButton);
        enter = (Button)findViewById(R.id.login) ;
        name = (EditText)findViewById(R.id.login_username);
        profile = (ImageView)findViewById(R.id.imageView);
        pickImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");


                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String n = name.getText().toString();
                String address = MacAddress.getMacAddr();
                if (!(n .matches(""))){
                    MainActivity.currentUser =MainActivity.db.insertUser(n,address,imgPath);
                    if (MainActivity.currentUser!= null){
                        Toast.makeText(getApplicationContext(),MainActivity.currentUser.getName() ,Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, ContactActivity.class));
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"not successful",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"please enter a name first",Toast.LENGTH_SHORT).show();
                }



            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            Uri uri = data.getData();
            profile.setImageURI(uri );
            String x = getPath(uri);
            imgPath = x;
        }else{
            imgPath = null;
        }

    }
   public String getPath(Uri uri){
        if (uri == null)return null;
        String[] projection ={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( uri , projection ,null , null , null);
        if(cursor!=null){
            int colIndex=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(colIndex);
        }
        return uri.getPath();
    }
}
