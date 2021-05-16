package com.example.piysharefile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.net.wifi.ScanResult;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
private Button tdata,gdata;
private String wifiad="Boş",ad="",name="";
    private String DosyaAd[]=new String[50];


private int bekleme=0,boyut=-1;
    private StorageReference storageRef;

    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiad = getWifiName();
        //TANIMLAR
        gdata = (Button) findViewById(R.id.gdata);
        tdata = (Button) findViewById(R.id.tdata);

        //TIKLAMALAR
        gdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(getApplicationContext(), givedata.class);
                startActivity(a);
            }
        });

        tdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent b = new Intent(getApplicationContext(), takedata.class);
                startActivity(b);
            }
        });

        //DOSYA DİNLEME
        myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child(wifiad).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot gun : dataSnapshot.getChildren()) {
                    for (DataSnapshot ay : gun.getChildren()) {

                        boyut++;
                        if (boyut == 0) {

                            DosyaAd[0] = ay.getValue().toString();
                            bekleme++;
                            if (bekleme == 2) {
                                uyarı(DosyaAd[0]);
                                bekleme=1;
                            }
                        } else if (boyut == 1) {
                            boyut = -1;

                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });




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

public void uyarı(String ad)
{

    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    builder.setTitle("Size gönderilen bu dosyayı kabul etmek istiyormusunuz?");
    builder.setMessage("Dosya Adı:"+ad);

    builder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener(){
        public void onClick(DialogInterface dialog, int id) {


        }
    });


    builder.setPositiveButton("İNDİR", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {

            try {
                download(DosyaAd[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    });


    builder.show();
}
    public void başarılıdownload()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("SON");
        builder.setMessage("İndirme İşlemi Tamamlandı:"+ad);
        builder.setNegativeButton("ÇIKIŞ", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {


            }
        });
        builder.show();
    }

    public void download(String dosyaadı) throws IOException {
        storageRef  = FirebaseStorage.getInstance().getReference().child(dosyaadı);

        File localFile = new File(Environment.getExternalStorageDirectory() + "/Download/"+dosyaadı);

        storageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                       başarılıdownload();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });
    }


}