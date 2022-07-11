package com.example.tomas.telesna;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomas.R;
import com.example.tomas.common.Session;
import com.example.tomas.strelby.Exceptions.NoResultException;

import java.io.IOException;
import java.util.List;


public class PhysicalDisplayResults extends AppCompatActivity {

    Button mNie;
    Button mAno;

    FitnessDatabaseHelper db = new FitnessDatabaseHelper(this);
    Session session;

    TableWrapper tableWrapper = new TableWrapper();
    List<PhysicalResult> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telesna_results_header);

        /* get intent info */
        Intent intent = getIntent();
        Long sessionId = intent.getLongExtra("SESSION_ID", 0);
        session = db.findSession(sessionId);
        results = db.getAllTelesnaResultsinSession(session);


        /*calculate and show results*/
        TableLayout lLayout = findViewById(R.id.tableTelesna);

        for (PhysicalResult res : results) {
            LayoutInflater inflater = getLayoutInflater();
            TableRow tablerow =  (TableRow)  inflater.inflate(R.layout.telesna_results_table, null);

            Points points = tableWrapper.calculateResult(res);
            int color;
            if (points.passed) color = getResources().getColor(R.color.colorGreen);
            else color = getResources().getColor(R.color.colorRed);
            tablerow.setBackgroundColor(color);


            TextView coz = tablerow.findViewById(R.id.coz);
            coz.setText(res.person.id);
            TextView meno= tablerow.findViewById(R.id.meno);
            meno.setText(res.person.name);
            TextView skupina = tablerow.findViewById(R.id.skupina);
            skupina.setText(res.person.getGroup());
            TextView hrazda = tablerow.findViewById(R.id.hrazda);
            hrazda.setText(res.disciplines.get("pull_up")+"/"+points.disciplines[Indexes.PULL_UP].toString());
                TextView skok = tablerow.findViewById(R.id.skok);
                    if ( res.person.getGender().equals("f")){
                        skok.setText("---");
                    }else {
                        skok.setText(res.disciplines.get("jump") + "/" + points.disciplines[Indexes.JUMP].toString());
                    }

            TextView brusaky = tablerow.findViewById(R.id.brusaky);
            brusaky.setText(res.disciplines.get("crunch")+"/"+points.disciplines[Indexes.CRUNCH].toString());
            TextView sprint= tablerow.findViewById(R.id.sprint);
            sprint.setText(res.disciplines.get("sprint")+"/"+points.disciplines[Indexes.SPRINT].toString());
            TextView beh = tablerow.findViewById(R.id.beh);
            beh.setText(res.disciplines.get("12min")+"/"+points.disciplines[Indexes.RUN_12min].toString());
            TextView plavanie= tablerow.findViewById(R.id.plavanie);
            plavanie.setText(res.disciplines.get("swimming")+"/"+points.disciplines[Indexes.SWIMMING].toString());
            TextView body = tablerow.findViewById(R.id.body);
            body.setText(points.summary.toString());

                lLayout.addView(tablerow);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    public void endSession() {
        printDocuments();
        for (PhysicalResult res : results) {
            res.person.setAttempt(res.person.getAttempt() + 1);
            db.updatePersonAttempt(res.person, res.person.getAttempt());
        }

        finish();
    }

    void printDocuments() {

        try {
            PhysicalFileManager.makeExcel_PhysicalPeopleAndResults(
                    db.getAllTelesnaResultsinSession(session), session.getTimeStamp(),
                    session.getStartStadiumTime(), session.getEndStadiumTime()
            );
            PhysicalFileManager.createTXT_telesna(db.getAllTelesnaResultsinSession(session), session);
            PhysicalFileManager.createTXT_telesna_vysledky(db.getAllTelesnaResultsinSession(session), session);
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_telesna,
                    (ViewGroup) findViewById(R.id.custom_toast_container));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Súbory boli vyexportované.");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (IOException ex) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_telesna,
                    (ViewGroup) findViewById(R.id.custom_toast_container));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Pravdepodobne chýba zdrojový súbor!");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (NoResultException ex) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_telesna,
                    (ViewGroup) findViewById(R.id.custom_toast_container));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Zoznam sa nepodarilo vytvoriť!");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
    }


    public void alertUser(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.alert_ukonc_meranie_telesna, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setTitle(R.string.previerky_telesna);
        mBuilder.setView(mView);

        mAno = (Button) mView.findViewById(R.id.btnAno);
        mNie = (Button) mView.findViewById(R.id.btnNie);

        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mAno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                endSession();
            }
        });

        mNie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void goBack(View view) {
        finish();
    }
}
