package com.example.tomas.strelby;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ShootsDisplayResults extends AppCompatActivity {
    Button mNie;
    Button mAno;

    ShootsDatabaseHelper db = new ShootsDatabaseHelper(this);
    Session session;
    //TableWrapper tableWrapper = new TableWrapper();
    List<ShootResult> results;
    boolean hasAmend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strelby_results_header);

        /* get intent info */
        Intent intent = getIntent();
        Long sessionId = intent.getLongExtra("SESSION_ID", 0);
        session = db.findSession(sessionId);

        results = db.getAllTelesnaResultsinStrelbyResults(session);

        /*calculate and show results */
        hasAmend = false;  /* set true if show people change it to false then there are amend People */
        showPeople();

        Button amend = (Button) findViewById(R.id.b_amend_session);
        if (hasAmend) {
            amend.setVisibility(View.VISIBLE);
        } else {
            amend.setVisibility(View.GONE);
        }
    }

   private void showPeople() {
        TableLayout lLayout = findViewById(R.id.tableStrelby);

        for (ShootResult res : results) {
            LayoutInflater inflater = getLayoutInflater();
            TableRow tablerow =  (TableRow)  inflater.inflate(R.layout.strelby_results_table, null);

            int color;
            if (res.getStatus()) {
                color = getResources().getColor(R.color.colorGreen);
            } else {
                hasAmend = true;
                color = getResources().getColor(R.color.colorRed);
            }
            tablerow.setBackgroundColor(color);

            TextView coz = tablerow.findViewById(R.id.coz);
            coz.setText(res.person.id);
            TextView meno= tablerow.findViewById(R.id.meno);
            meno.setText(res.person.name);
            TextView skupina = tablerow.findViewById(R.id.skupina);
            skupina.setText(res.person.getGroup());
            TextView cas = tablerow.findViewById(R.id.cas);
            cas.setText(res.getTime().toString());


            TextView body = tablerow.findViewById(R.id.body);
            body.setText(res.getPoints().toString());
            TextView nul= tablerow.findViewById(R.id.nul);
            if (res.getNullTarget().equals("OK")){
                nul.setText("---");
            }else {
                nul.setText( res.getNullTarget());
            }

            TextView koef = tablerow.findViewById(R.id.koef);
            koef.setText(new DecimalFormat("#.####").format(res.getRatio()));

                lLayout.addView(tablerow);

        }
    }

    public void endSession() {
        printDocuments();
        for (ShootResult res : results) {
            res.person.setAttempt(res.person.getAttempt() + 1);
            db.updatePersonAttempt(res.person, res.person.getAttempt());
        }

        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    public void amendSession() {
        printDocuments();
        List<ShootResult> failed = new ArrayList<ShootResult>();
        for (ShootResult res : results) {
            res.person.setAttempt(res.person.getAttempt() + 1);
            db.updatePersonAttempt(res.person, res.person.getAttempt());
            if (!res.getStatus()) {
                failed.add(res);
            }
        }
        Session newSession = db.createSession(1, session.id);
        for (int i = 1; i <= failed.size(); ++i) {
            db.addOrReplacePersonToStrelbyResults(newSession, failed.get(i - 1).person, i);
        }
        Intent data = new Intent();
        data.putExtra("SESSION_ID", newSession.id);
        setResult(RESULT_OK, data);
        finish();
    }

    void printDocuments() {

        try {
            ShootsFileManager.makeExcelParticipation(db.getAllTelesnaResultsinStrelbyResults(session), session);
            if (session.getNumOfBullets() != 0)
                ShootsFileManager.numOfBullets = session.getNumOfBullets();
            ShootsFileManager.makeExcelMunition(db.getAllTelesnaResultsinStrelbyResults(session), session);
            ShootsFileManager.makeExcel_ShootsResults(db.getAllTelesnaResultsinStrelbyResults(session), session);
            ShootsFileManager.createTXTList(db.getAllTelesnaResultsinStrelbyResults(session), session);
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_strelby,
                    (ViewGroup) findViewById(R.id.strelby_toast_container));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Excel zoznam bol vyexportovaný.");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (IOException ex) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_strelby,
                    (ViewGroup) findViewById(R.id.strelby_toast_container));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Pravdepodobne chýba zdrojový súbor.");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (NoResultException ex) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_strelby,
                    (ViewGroup) findViewById(R.id.strelby_toast_container));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Zoznam sa nepodarilo vytvoriť");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }

    }


    public void alertUserAmend(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.alert_opravne_strelby, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShootsDisplayResults.this);
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
                dialog.dismiss();
                amendSession();
            }
        });

        mNie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void alertUserEnd(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.alert_ukonc_meranie_strelby, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
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
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

}
