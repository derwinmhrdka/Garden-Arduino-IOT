package com.example.dashboardui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.RecognizerResultsIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    DatabaseReference ref;
    boolean isClick_button1=false;
    boolean isClick_button2=false;

    private ProgressBar progressBar;

    private Handler handler = new Handler();

    int pStatus;
    String soil;
    String temperature;
    String humidity;

    ArrayList<String> result;
    String perintah_txt;

    String statusAC1;
    String statusAC2;

    TextToSpeech textToSpeech;

    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ref = FirebaseDatabase.getInstance().getReference().child("TemperatureDB");

        TextView labelT  = (TextView) findViewById(R.id.labelT);
        TextView labelH  = (TextView) findViewById(R.id.labelH);
        TextView txtProgress = (TextView) findViewById(R.id.txtProgress);
        TextView txtSoil = (TextView) findViewById(R.id.soil_cd);

        TextView acStat1  = (TextView) findViewById(R.id.acStat1);
        TextView acStat2  = (TextView) findViewById(R.id.acStat2);

        Button buttonAC1 = (Button) findViewById(R.id.buttonAC1);
        Button buttonAC2 = (Button) findViewById(R.id.buttonAC2);

        ImageView indicatorAC1 = (ImageView) findViewById(R.id.indicatorAC1);
        ImageView indicatorAC2 = (ImageView) findViewById(R.id.indicatorAC2);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        buttonAC1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClick_button1) {
                    acStat1.setText("OFF");
                    buttonAC1.setText("Turn ON");
                    indicatorAC1.setColorFilter(getColor(R.color.ind_red));
                    isClick_button1=true;
                    ref.child("ac1").setValue("OFF");
                    System.out.println(perintah_txt);

                }
                else if (isClick_button1=true){
                    acStat1.setText("ON");
                    buttonAC1.setText("Turn OFF");
                    indicatorAC1.setColorFilter(getColor(R.color.ind_green));
                    isClick_button1=false;
                    ref.child("ac1").setValue("ON");
                }
            }
        });

        buttonAC2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClick_button2) {
                    acStat2.setText("OFF");
                    buttonAC2.setText("Turn ON");
                    indicatorAC2.setColorFilter(getColor(R.color.ind_red));
                    isClick_button2=true;
                    ref.child("ac2").setValue("OFF");
                }
                else if (isClick_button2=true){
                    acStat2.setText("ON");
                    buttonAC2.setText("Turn OFF");
                    indicatorAC2.setColorFilter(getColor(R.color.ind_green));
                    isClick_button2=false;
                    ref.child("ac2").setValue("ON");
                }
            }
        });

        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                statusAC1 = dataSnapshot.child("ac1").getValue().toString();
                statusAC2 = dataSnapshot.child("ac2").getValue().toString();

                temperature = dataSnapshot.child("temperature").getValue().toString();
                humidity = dataSnapshot.child("humidity").getValue().toString();
                soil = dataSnapshot.child("soil").getValue().toString();
                pStatus = Integer.valueOf(soil);
                labelT.setText((temperature)+"\u2103");
                labelH.setText((humidity)+"%");
                progressBar.setProgress(pStatus);
                txtProgress.setText(soil);

                if (pStatus>0 && pStatus<=30){
                    txtSoil.setText("Kering");
                    txtSoil.setTextColor(getColor(R.color.ind_red));
                }
                else if (pStatus>30 && pStatus<=70){
                    txtSoil.setText("Cukup");
                    txtSoil.setTextColor(getColor(R.color.dashboard_item_1));
                }
                else if (pStatus>70){
                    txtSoil.setText("Basah");
                    txtSoil.setTextColor(getColor(R.color.ind_green));
                }
                else txtSoil.setText("Tidak Terdeteksi");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }


    public void btnSpeech(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Butuh Bantuan");

        try {
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK && null!=data){
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    perintah_txt = result.get(0).toLowerCase(Locale.ROOT);

                    if (perintah_txt.indexOf("nyalakan pompa")!=-1){
                        ref.child("ac1").setValue("ON");
                        textToSpeech.speak("keren! pompa menyala",TextToSpeech.QUEUE_FLUSH,null);
                    }
                    else if (perintah_txt.indexOf("matikan pompa")!=-1){
                        ref.child("ac1").setValue("OFF");
                        textToSpeech.speak("parah! pompa mati",TextToSpeech.QUEUE_FLUSH,null);
                    }
                    else if (perintah_txt.indexOf("nyalakan lampu")!=-1){
                        ref.child("ac2").setValue("ON");
                        textToSpeech.speak("keren! lampu menyala",TextToSpeech.QUEUE_FLUSH,null);
                    }
                    else if (perintah_txt.indexOf("matikan lampu")!=-1){
                        ref.child("ac2").setValue("OFF");
                        textToSpeech.speak("yah! lampu mati",TextToSpeech.QUEUE_FLUSH,null);
                    }
                    else if (perintah_txt.indexOf("matikan semua")!=-1 || perintah_txt.indexOf("matikan semua perangkat")!=-1) {
                        ref.child("ac1").setValue("OFF");
                        ref.child("ac2").setValue("OFF");
                        textToSpeech.speak("yah, mati semua deh",TextToSpeech.QUEUE_FLUSH,null);
                    }
                    else if (perintah_txt.indexOf("nyalakan semua")!=-1 || perintah_txt.indexOf("nyalakan semua perangkat")!=-1) {
                        ref.child("ac1").setValue("ON");
                        ref.child("ac2").setValue("ON");
                        textToSpeech.speak("hore! semua perangkat menyala",TextToSpeech.QUEUE_FLUSH,null);
                    }
                    else if (perintah_txt.indexOf("berapa suhu sekarang")!=-1){
                        textToSpeech.speak("Sekarang suhu sekitar taman sebesar"+temperature+"derajat celcius",TextToSpeech.QUEUE_FLUSH,null);
                    }
                    else if (perintah_txt.indexOf("berapa kelembapan udara sekarang")!=-1||perintah_txt.indexOf("berapa kelembaban udara sekarang")!=-1){
                        textToSpeech.speak("Sekarang kelembapan udara sekitar taman anda sebesar"+humidity+"%",TextToSpeech.QUEUE_FLUSH,null);
                    }
                    else if (perintah_txt.indexOf("berapa kelembapan tanah sekarang")!=-1||perintah_txt.indexOf("berapa kelembaban tanah sekarang")!=-1){
                        textToSpeech.speak("Sekarang kelembapan tanah sebesar"+soil+"%",TextToSpeech.QUEUE_FLUSH,null);
                    }
                    else if (perintah_txt.indexOf("kondisi aktuator")!=-1){
                        textToSpeech.speak("kondisi pompa,"+statusAC1+", kondisi lampu,"+statusAC2,TextToSpeech.QUEUE_FLUSH,null);
                    }
                }
                break;
        }
    }
}