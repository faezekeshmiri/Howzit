package com.example.faridam.howzit;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    static DatabaseHelper db;
    static User currentUser;
    TextView textView;
    ImageView rocket;
    ImageView howzit;
    Animation move;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        db = new DatabaseHelper(this);
        rocket = (ImageView)findViewById(R.id.rocket) ;

        move = new TranslateAnimation(-300, 300,0, 0);
        move.setDuration(2000);

        rocket.startAnimation(move);

        howzit = (ImageView)findViewById(R.id.howzit) ;

        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                rocket.setImageResource(0);
                howzit.setVisibility(View.VISIBLE);
                Thread thread = new Thread() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                }
                            }
                        });

                    }

                };
                thread.start();

                String macAdd = MacAddress.getMacAddr();
                currentUser = db.checkUser(macAdd);
                if(currentUser != null){
                    Toast.makeText(getApplicationContext(),currentUser.getName(),Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, ContactActivity.class));

                }else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }


                finish();
            }
        }, 2000);
    }
}
