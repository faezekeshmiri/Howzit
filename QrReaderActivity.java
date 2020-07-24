package com.example.faridam.howzit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrReaderActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    public static ArrayList<String> names = new ArrayList<>();
    ZXingScannerView scannerView;
    public static String addr ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        scannerView = new ZXingScannerView(this);
        super.onCreate(savedInstanceState);
        setContentView(scannerView);
    }

    @Override
    public void handleResult(Result result) {
        AccountActivity.qrResult = result.getText();
        String [] qrRes = AccountActivity.qrResult.split( "," );
        names.add( qrRes[1]);
        addr = qrRes[2];
        // insert contact as user
        MainActivity.db.insertUser( qrRes[1],qrRes[2] ,null );
        startActivity(new Intent(QrReaderActivity.this , ScanActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}