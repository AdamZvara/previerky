package com.example.tomas.common;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomas.R;
import com.example.tomas.telesna.ExternalSessionLoader;
import com.example.tomas.telesna.FitnessDatabaseHelper;
import com.example.tomas.telesna.FitnessMainActivity;
import com.example.tomas.strelby.ShootsDatabaseHelper;
import com.example.tomas.strelby.ShootsMainActivity;

import java.io.IOException;
import java.text.ParseException;

public class MainActivity extends AppCompatActivity {
    Button mNie;
    Button mAno;

    Button strelby;
    Button telesna;
    GridView telesna_main_gridview;
    GridView strelby_main_gridview;
    FitnessDatabaseHelper fitnessDatabase;
    ShootsDatabaseHelper shootsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ask for permisions
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }

        telesna_main_gridview = (GridView) findViewById(R.id.telesna_main_gridview);
        strelby_main_gridview = (GridView) findViewById(R.id.strelby_main_gridview);
        //TODO make just one instance and pass it to other views
        fitnessDatabase = new FitnessDatabaseHelper(this);
        shootsDatabase = new ShootsDatabaseHelper(this);

        // load external sessions
        try {
            ExternalSessionLoader.addSessionsToDatabase(fitnessDatabase);
        } catch (ParseException e){
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_telesna,
                    (ViewGroup) findViewById(R.id.custom_toast_container));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Názov importného súboru sa nedá prekonvertovať na dátum!");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (IOException e){
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_telesna,
                    (ViewGroup) findViewById(R.id.custom_toast_container));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Súbor sa nedá vymazať. Odstráňte ho ručne.");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }

        telesna = (Button) findViewById(R.id.btn_telesna);
        strelby = (Button) findViewById(R.id.btn_strelby);


        telesna_main_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Session session =  (Session) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, FitnessMainActivity.class);
                intent.putExtra("sessionId", session.id);
                startActivity(intent);
            }
        });

        strelby_main_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Session session =  (Session) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, ShootsMainActivity.class);
                intent.putExtra("sessionId", session.id);
                startActivity(intent);
            }
        });

   }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayAdapter<Session> fitnessSessions = new ArrayAdapter<>(this,
               R.layout.telesna_main_gridview,fitnessDatabase.getSessions());
        telesna_main_gridview.setAdapter(fitnessSessions);

        ArrayAdapter<Session> shootsSessions = new ArrayAdapter<>(this,
                R.layout.strelby_main_gridview,shootsDatabase.getSessions());
        strelby_main_gridview.setAdapter(shootsSessions);

    }

    public void runStrelby(View view) {
        final Intent intent = new Intent(this, ShootsMainActivity.class);
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.alert_nove_meranie_strelby, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setTitle(R.string.previerky_telesna);

        mAno = (Button) mView.findViewById(R.id.btnAno);
        mNie = (Button) mView.findViewById(R.id.btnNie);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mAno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
                dialog.dismiss();
            }
        });

        mNie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void runTelesna(View view) {
        final Intent intent = new Intent(this,FitnessMainActivity.class);
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.alert_nove_meranie_telesna, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setTitle(R.string.previerky_telesna);

        mAno = (Button) mView.findViewById(R.id.btnAno);
        mNie = (Button) mView.findViewById(R.id.btnNie);


        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mAno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
                dialog.dismiss();
            }
        });

        mNie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }



}
