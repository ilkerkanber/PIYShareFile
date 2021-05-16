package com.example.piysharefile;


import android.os.Bundle;
import android.os.Environment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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


public class takedata extends AppCompatActivity {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    DatabaseReference myRef;
    FirebaseDatabase database;
    private Button adresbul,download1,download2,download3,download4,download5;
    private TextView altext,ad1,ad2,ad3,ad4,ad5;
    private String DosyaAd[]=new String[50];
    private String DosyaUrl[]=new String[50];
    private int data=-1,boyut=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.takedata);

        myRef = FirebaseDatabase.getInstance().getReference();
        //DEĞİŞKENLER
        adresbul = (Button) findViewById(R.id.adresbul);
        altext = (TextView) findViewById(R.id.adres);
        ad1 = (TextView)findViewById(R.id.ad1);
        download1=(Button)findViewById(R.id.download1);





        //OLAYLAR
        adresbul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adresbulma();
                temizleme();
            }
        });
        download1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageRef  = FirebaseStorage.getInstance().getReference().child(DosyaAd[0]);

                try {
                    download(DosyaAd[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


     }

     public void download(String dosyaadı) throws IOException {

         File localFile = new File("/storage/emulated/0/Download/"+dosyaadı);

         storageRef.getFile(localFile)
                 .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                         Log.e("Durum","Başarılı"+taskSnapshot.getBytesTransferred()+"/"+taskSnapshot.getTotalByteCount()
);
                     }
                 }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception exception) {
                 // Handle failed download
                 // ...
             }
         });
     }





    public void adresbulma()
    {try {
        myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child(altext.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Count " ,""+dataSnapshot.getChildrenCount());


                boyut = -1;
                for (DataSnapshot gun : dataSnapshot.getChildren())
                {
                    for (DataSnapshot ay : gun.getChildren())
                    {
                        boyut++;
                    if (boyut == 0) {

                        DosyaAd[0] = ay.getValue().toString();
                    }
                    else if (boyut == 1) {
                        DosyaUrl[0] = ay.getValue().toString();
                        boyut = -1;
                    }


                    }
                }
                yerlestir();

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }catch (Exception ex){}
    }
    public void yerlestir()
    {
        ad1.setText(DosyaAd[0]);
    }
    public void temizleme()
    {
        for(int i=0;i<=49;i++)
        {DosyaAd[i]="";}
    }

}