package com.example.piysharefile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

    public class givedata extends AppCompatActivity {

        private String veriadları[] = new String[5];
        private Uri veriler[] = new Uri[5];

        private Button Dosya, Yükle1, Yükle2, Yükle3, Yükle4, Yükle5;
        private EditText vertext;
        private TextView txt1, txt2, txt3, txt4, txt5;
        private Random rastgele;
        public static final int PICKFILE_RESULT_CODE = 1;

        //STORAGE
        FirebaseStorage storage;
        StorageReference storageReference;
        //REALTIME
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        private Uri fileUri;
        private String fileName;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.givedata);

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            rastgele = new Random();

            //NESNELER
            txt1 = (TextView) findViewById(R.id.ad1);
            Yükle1 = (Button) findViewById(R.id.yukle1);
            Dosya = (Button) findViewById(R.id.Dosya);
            vertext = (EditText) findViewById(R.id.adres);

            //İŞLEVLER
            Dosya.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DosyaAl();
                }
            });
            Yükle1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (veriler[0] != null) {
                        upload(veriler[0], veriadları[0]);
                    }
                }
            });

        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            try {
                if (resultCode == RESULT_OK && data != null) {
                    fileUri = data.getData();
                    DocumentFile documentFile = DocumentFile.fromSingleUri(this, fileUri);
                    String fileName = documentFile.getName();

                    veriler[0] = fileUri;
                    veriadları[0] = fileName;
                    listeekle();

                }
            } catch (Exception ex) {
            }
        }


        public void upload(final Uri fileUri, final String ad) {
            final StorageReference ref = storageReference.child(ad);

            ref.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            vtpkaydet(vertext.getText().toString(), ad, taskSnapshot.getUploadSessionUri());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });

        }


        public void listeekle() {
            txt1.setText(veriadları[0]);
        }

        public void vtpkaydet(String adres, String ResimAd, Uri ResimUrl) {
            myRef.child(adres).child("1").child("ResimAd").setValue(ResimAd);
            myRef.child(adres).child("1").child("ResimUrl").setValue(""+ResimUrl);
            myRef.child(getWifiName()).child("1").child("ResimAd").setValue(ResimAd);
            myRef.child(getWifiName()).child("1").child("ResimUrl").setValue(""+ResimUrl);

    }

        public void DosyaAl() {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Dosyayı Seçiniz");
            startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
        }

        public String getWifiName() {
            String kelime="";
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            String ssid  = info.getSSID();
            String bssid = info.getBSSID();

            for(int i=0;i<ssid.length();i++)
            {
                if(ssid.charAt(i) == '\"')
                {
                }
                else
                {
                 kelime+=ssid.charAt(i);
                }
}
            return kelime;
        }
    }