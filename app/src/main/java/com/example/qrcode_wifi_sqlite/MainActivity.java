package com.example.qrcode_wifi_sqlite;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private Button buttonScan,buttonSave,buttonIP;
    private TextView textViewOutput;
    private  DBHelper adatbazis;
    private ImageView imageViewQR;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();






            buttonScan.setOnClickListener(view -> {

                IntentIntegrator intentIntegrator =
                        new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.setPrompt("QR Code Scan-elése");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setBarcodeImageEnabled(false);
                intentIntegrator.initiateScan();

            });

            buttonSave.setOnClickListener(v -> {
                String qrcodetext = textViewOutput.getText().toString().trim();
                String date = new Date().toString();
                String ip_address ="";
                ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (networkInfo.isConnected()) {
                    int ip_number = wifiInfo.getIpAddress();
                    String ip = Formatter.formatIpAddress(ip_number);
                    ip_address = ip;
                }



                if (qrcodetext.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Nincs menthető kód, Scanneljen be egy QR kódot", Toast.LENGTH_SHORT).show();
                } else {
                     if (adatbazis.rogzites(qrcodetext, ip_address, date)) {

                        Toast.makeText(MainActivity.this, "Sikeres rögzítés", Toast.LENGTH_SHORT).show();
                        textViewOutput.setText("");
                    } else {
                        Toast.makeText(MainActivity.this, "Sikertelen rögzítés", Toast.LENGTH_SHORT).show();

                    }

                }

        });


            buttonIP.setOnClickListener(v -> {



                ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (networkInfo.isConnected()){
                    int ip_number=wifiInfo.getIpAddress();
                    String ip= Formatter.formatIpAddress(ip_number);

                    textViewOutput.setText(ip);
                }else{

                    textViewOutput.setText("Nem csatlakoztál wifi hálozatra");

                }
                MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
                String data= textViewOutput.getText().toString().trim();
                try {
                    BitMatrix bitMatrix=multiFormatWriter.encode(
                            data,
                            BarcodeFormat.QR_CODE,
                            150,150
                    );
                    BarcodeEncoder barcodeEncoder= new BarcodeEncoder();
                    Bitmap bitmap= barcodeEncoder.createBitmap(bitMatrix);
                    imageViewQR.setImageBitmap(bitmap);
                }catch (WriterException e){
                    e.printStackTrace();
                }

            });



        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Kiléptél a QRCode Scanner-ből", Toast.LENGTH_SHORT).show();
            } else {
                textViewOutput.setText(result.getContents());

                AlertDialog urlalert = new AlertDialog.Builder(MainActivity.this).setCancelable(false)
                        .setMessage("Szeretné megnyitni az URL-t?")
                        .setTitle("QR code link")
                        .setPositiveButton("igen, bízok benne", (dialogInterface, i) -> {
                            Uri uri = Uri.parse(result.getContents());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            super.onActivityResult(requestCode, resultCode, data);
                        }).setNegativeButton("Nem, nem bízok benne", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create();
                urlalert.show();
            }
        }



    }

    public void init(){
        buttonScan=findViewById(R.id.buttonScan);
        buttonSave=findViewById(R.id.buttonSave);
        textViewOutput=findViewById(R.id.textViewOutput);
        adatbazis=new DBHelper(MainActivity.this);
        buttonIP=findViewById(R.id.buttonIP);
        imageViewQR=findViewById(R.id.imageViewQR);
        wifiManager=(WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiInfo=wifiManager.getConnectionInfo();
    }

}