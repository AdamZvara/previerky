package com.example.tomas.telesna;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomas.R;
import com.example.tomas.common.CentrumENUM;
import com.example.tomas.common.MainActivity;
import com.example.tomas.common.RankENUM;
import com.example.tomas.common.Person;
import com.example.tomas.common.Session;
import com.example.tomas.common.Utilities;
import com.example.tomas.strelby.Exceptions.NoResultException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/*
 * Main class -
 * moveToPerson - int or string "next", "prev"
 *
 *
 * */
public class FitnessMainActivity extends AppCompatActivity implements RunDialog.RunDialogListener, AfterRunDialog.AfterRunDialogListener {

    Button mNie;
    Button mAno;

    TextView textDressNumber;
    Button btnUp;
    Button btnDown;
    TextView editDate;
    EditText editID;
    Spinner editRank;
    EditText editName;
    Spinner editCentrum;
    EditText editBorn;
    Button btnGroup;
    Button btnGender;
    Button btnAddPerson;

    Button btnStadion;
    TextView editAttempt;
    TextView textGun;
    Button btnGun;
    Button btnLists;
    Button btnShowResults;
    Button btnWriteResult;

    ButtonGrid adapter;
    boolean isGridViewVisible = false;
    boolean is12minOver = false;
    boolean isRunStadionTime = false;

    MyTimer myTimer;
    WomanTimer womanTimer;
    EditText womantextTimer;

    TextView textTimer;
    Button btnStartTimer;
    Button btnUndo;
    Button btnRound;

    FitnessDatabaseHelper db;
    Session session;
    Long stadiumTime;

    String btnAddPersonState = "add"; //state of add button_osoba - add - addperson, edit - edit person
    String btnWriteResultState = "active"; //active - write result , passive  - does not do anything
    String btnStadionState = "active";

    List<String> groupList = new ArrayList<String>();
    List<String> genderList = new ArrayList<String>();
    List<String> gunList = new ArrayList<String>();
    ArrayList<EditText> editDisciplines;
    public final Integer EDIT_PULL_UP = 0;
    public final Integer EDIT_JUMP = 1;
    public final Integer EDIT_CRUNCH = 2;
    public final Integer EDIT_SPRINT = 3;
    public final Integer EDIT_12min = 4;
    public final Integer EDIT_SWIMMING = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telesna_main);

        // group - 0 - --; 1 - I; 2 - II;
        groupList.add(getResources().getString(R.string.b_empty_choice));
        groupList.add(getResources().getString(R.string.b_group_I));
        groupList.add(getResources().getString(R.string.b_group_II));
        // gender - 0 - --; 1 - Muž, 2 - Žena;
        genderList.add(getResources().getString(R.string.b_empty_choice));
        genderList.add(getResources().getString(R.string.b_man));
        genderList.add(getResources().getString(R.string.b_woman));
        // gun - 0 - --; 1 - Luger, 2 - Makarov;
        gunList.add(getResources().getString(R.string.b_empty_choice));
        gunList.add(getResources().getString(R.string.b_gun_L));
        gunList.add(getResources().getString(R.string.b_gun_M));

        /*
        File parentDir=new File(Environment.getExternalStorageDirectory().getParent());
        Toast.makeText(getApplicationContext(), parentDir.toString(),Toast.LENGTH_SHORT).show();

        String scaneddata="save data to file in sdcard";
        String dir = Environment.getExternalStorageDirectory() + File.separator + "extSdCard"+File.separator+"sample";
        File folder = new File(dir);
        folder.mkdirs();
*/

        setIDs();
        setEvents();


        db = new FitnessDatabaseHelper(this);

         // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Long sessionId = intent.getLongExtra("sessionId", 0);
        session = db.findSession(sessionId); //for now use only this one session maybe later more
        if (session == null) {
            session = db.createSession(1);
        }

        editDate.setText(Utilities.timestampToString(session.getTimeStamp(), "dd.MM.yyyy"));
    }


    @Override
    protected void onResume() {
        super.onResume();

        moveToPerson(session.getActualNumber());

        if (isGridViewVisible) {
            Log.d("timer", String.valueOf(myTimer.updatedTime));
        }
    }

    // << ------------------------------------------------ IDS ----------------------------------------------->>
    private void setIDs() {
        btnAddPerson = (Button) findViewById(R.id.b_add_person);
        editID = (EditText) findViewById(R.id.edit_id);
        editName = (EditText) findViewById(R.id.edit_name);
        textDressNumber = (TextView) findViewById(R.id.text_dress_number);
        btnUp = (Button) findViewById(R.id.b_up);
        btnDown = (Button) findViewById(R.id.b_down);
        editDate = (TextView) findViewById(R.id.edit_datum);
        editRank = (Spinner) findViewById(R.id.spinner_hodnost);
        editCentrum = (Spinner) findViewById(R.id.spinner_odbor);
        editBorn = (EditText) findViewById(R.id.edit_born);
        editAttempt = (TextView) findViewById(R.id.edit_attempt);

        editDisciplines = new ArrayList<>();
        editDisciplines.add((EditText) findViewById(R.id.edit_pullup));
        editDisciplines.add((EditText) findViewById(R.id.edit_jump));
        editDisciplines.add((EditText) findViewById(R.id.edit_crunch));
        editDisciplines.add((EditText) findViewById(R.id.edit_sprint));
        editDisciplines.add((EditText) findViewById(R.id.edit_12min));
        editDisciplines.add((EditText) findViewById(R.id.edit_swimming));
        btnStadion = (Button) findViewById(R.id.btnStadion);
        btnLists = (Button) findViewById(R.id.btn_lists);
        btnShowResults = (Button) findViewById(R.id.btn_show_results);
        btnWriteResult = (Button) findViewById(R.id.b_write);

        btnStartTimer = (Button) findViewById(R.id.btn_start_timer);
        btnUndo = (Button) findViewById(R.id.btn_undo);
        textTimer = (TextView) findViewById(R.id.text_time);
        btnRound = (Button) findViewById(R.id.b_round);
        textGun = (TextView) findViewById(R.id.text_gun);
        btnGun = (Button) findViewById(R.id.btn_gun);
        btnGender = (Button) findViewById(R.id.btn_gender);
        btnGroup = (Button) findViewById(R.id.btn_group);

        editRank.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_hodnost, RankENUM.values()));
        editCentrum .setAdapter(new ArrayAdapter<>(this, R.layout.spinner_odbor, CentrumENUM.values()));

        editAttempt.setVisibility(View.GONE);
        textGun.setVisibility(View.GONE);
        btnGun.setVisibility(View.GONE);
        LinearLayout l = (LinearLayout) findViewById(R.id.timer_layout);
        l.setVisibility(View.GONE);

        womanTimer = new WomanTimer(this,findViewById(R.id.edit_pullup));
    }
    // << ------------------------------------------------ END IDS ----------------------------------------------->>


    // << ----------------------------------- LISTENERS ----------------------------------------->>
    @Override
    public void onBackPressed() {
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.alert_ukonc_aplikaciu_telesna, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FitnessMainActivity.this);
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
                finish();
                dialog.dismiss();
                try {
                    myTimer.stopTimer();
                } catch (Exception e) {
                }
            }
        });

        mNie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    private void setEvents() {
        btnAddPerson.setOnClickListener((View v) -> {
            String name = editName.getText().toString();
            String personId = editID.getText().toString();
            RankENUM rank = (RankENUM) editRank.getSelectedItem();
            CentrumENUM centrum = (CentrumENUM) editCentrum.getSelectedItem();
            Integer born;
            String gender;
            String gun;
            try {
                born = Integer.parseInt(editBorn.getText().toString());
            } catch (NumberFormatException e) {
                born = -1;
            }
            if (btnGender.getText().toString().equals(genderList.get(1))) { // if man
                gender = "m";
            } else if (btnGender.getText().toString().equals(genderList.get(2))) { // if woman
                gender = "f";
            } else {
                gender = null;
            }

            if (btnGun.getText().toString().equals(gunList.get(1))) { // if Luger
                gun = "L";
            } else if (btnGun.getText().toString().equals(gunList.get(2))) { // if Makarov
                gun = "M";
            } else {
                gun = null;
            }


            if ((personId.trim().length() == 0) || (born == -1) || gender == null || btnGroup.getText().toString().equals(groupList.get(0))) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_telesna,
                        (ViewGroup) findViewById(R.id.custom_toast_container));

                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText("Nezapísal si všetky údaje!");

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
                return;
            }

            PhysicalResult oldResult = db.findPersonInSessionById(session, personId);
            if (oldResult != null && oldResult.dressNum != session.getActualNumber()) {    /* if person exist and we are not editing person */
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_telesna,
                        (ViewGroup) findViewById(R.id.custom_toast_container));

                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText("Osoba sa už v meraní nachádza!");

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
                moveToPerson(session.getActualNumber());
                return;
            }


            db.createOrUpdatePerson(new Person(personId, rank, name, centrum, born, btnGroup.getText().toString(), gender, gun));
            db.addOrReplacePersonToSession(session, db.findPerson(personId), session.getActualNumber()); /* deletes strelby_disciplines_form and passed but that is calculated in the end */
            if (oldResult != null) { /* when editing user remember his strelby_disciplines_form */
                db.updatePersonTelesnaResults(session, session.getActualNumber(), oldResult.disciplines);

                final HashMap<String, String> newResult = getTelesnaResultsFromForm(); /* check if user did not forget to save data */
                if (compareTelesnaResults(newResult, oldResult.disciplines))
                    showUnsavedDisciplinesAlert(session.getActualNumber(), newResult);
            }
            moveToPerson("next");
        });

        /* Zada COZ 6 cisel-najde osobu v DB a vytiahne jej udaje */

        editID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editID.getText().length() == 6) {
                    String id = editID.getText().toString();
                    if (db.findPerson(id) == null) {
                        clearPersonalInfoExceptId();
                        return;
                    }

                    Person person = db.findPerson(id);
                    showPersonalInfoWithoutId(person);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhysicalResult oldResult;
                oldResult = db.findPersonInSessionByDress(session, session.getActualNumber());
                if (oldResult != null) {
                    final HashMap<String, String> newResult = getTelesnaResultsFromForm();
                    final Integer oldNumber = session.getActualNumber();
                    if (compareTelesnaResults(newResult, oldResult.disciplines)) {
                        showUnsavedDisciplinesAlert(oldNumber, newResult);
                    }
                }
                moveToPerson("next");
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhysicalResult oldResult = db.findPersonInSessionByDress(session, session.getActualNumber());
                if (oldResult != null) {
                    final HashMap<String, String> newResult = getTelesnaResultsFromForm();
                    final Integer oldNumber = session.getActualNumber();
                    if (compareTelesnaResults(newResult, oldResult.disciplines)) {
                        showUnsavedDisciplinesAlert(oldNumber, newResult);
                    }
                }
                moveToPerson("prev");
            }
        });
        btnDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PhysicalResult oldResult = db.findPersonInSessionByDress(session, session.getActualNumber());
                if (oldResult != null) {
                    final HashMap<String, String> newResult = getTelesnaResultsFromForm();
                    final Integer oldNumber = session.getActualNumber();
                    if (compareTelesnaResults(newResult, oldResult.disciplines)) {
                        showUnsavedDisciplinesAlert(oldNumber, newResult);
                    }
                }
                moveToPerson(db.getMinDressInSession(session));
                return true;
            }
        });
        btnUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PhysicalResult oldResult = db.findPersonInSessionByDress(session, session.getActualNumber());
                if (oldResult != null) {
                    final HashMap<String, String> newResult = getTelesnaResultsFromForm();
                    final Integer oldNumber = session.getActualNumber();
                    if (compareTelesnaResults(newResult, oldResult.disciplines)) {
                        showUnsavedDisciplinesAlert(oldNumber, newResult);
                    }
                }
                moveToPerson(db.getMaxDressInSession(session));
                return true;
            }
        });

        btnWriteResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnWriteResultState.equals("active")) {
                    HashMap<String, String> results = getTelesnaResultsFromForm();
                    db.updatePersonTelesnaResults(session, session.getActualNumber(), results);
                    if (db.getMaxDressInSession(session) <= session.getActualNumber()) {
                        moveToPerson(db.getMinDressInSession(session));
                    } else {
                        while (db.findPersonInSessionByDress(session, session.getActualNumber() + 1) == null) {
                            moveToPerson("next");
                        }
                        moveToPerson("next");
                    }
                } else if (btnWriteResultState.equals("passive")) {
                    new AlertDialog.Builder(FitnessMainActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("OK", null)
                            .setMessage("Najprv pridaj osobu!")
                            .show();
                }
            }
        });

        editDisciplines.get(EDIT_PULL_UP).setOnLongClickListener((View view) -> {
            if(btnGender.getText().toString().equals(genderList.get(2))) {
                if (womanTimer.isRunning()) {
                    womanTimer.stopTimer();
                } else {
                    womanTimer.startTimer();
                }
            }
                return true;

        });

        editDisciplines.get(EDIT_12min).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {

                DialogFragment newFragment = new RunDialog();
                Bundle args = new Bundle();
                ArrayList<Integer> data = db.getOrderedDressFromSession(session);
                if (data.size() == 0) { /* if there are no users */
                    new AlertDialog.Builder(FitnessMainActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("OK", null)
                            .setMessage("Najprv pridaj osobu!")
                            .show();
                    return true;
                }
                args.putIntegerArrayList("numbers", db.getOrderedDressFromSession(session));
                args.putInt("minimalNumber", db.getMinDressWithFilled12Min(session));
                newFragment.setArguments(args);
                newFragment.show(getSupportFragmentManager(), "tag");


                /*btnShowTelesnaResults.setEnabled(false);
                btnAddPerson.setEnabled(false);
                btnWriteResult.setEnabled(false);*/
                return true;
            }
        });

        btnRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (myTimer.isRunning) {
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.beep1);
                    db.addRoundToPerson(session, adapter.getItems());

                    long start = SystemClock.uptimeMillis();

                    adapter.updateScores();
                    mp.start();
                    Log.d("AFTER REDRAWING", String.valueOf(
                            (SystemClock.uptimeMillis() - start)
                    ));
                    adapter.addToHistory(0);
                    btnUndo.setEnabled(true);

                    moveToPerson(session.getActualNumber());
                    mp.start();
                    Log.d("AFTER MOVING", String.valueOf(
                            (SystemClock.uptimeMillis() - start)
                    ));

                } else {
                    LayoutInflater inflater = getLayoutInflater();
                    View mView = inflater.inflate(R.layout.alert_ukonc_12min, null);
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(FitnessMainActivity.this);
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
                            LinearLayout l = (LinearLayout) findViewById(R.id.timer_layout);
                            l.setVisibility(View.GONE);
                            isGridViewVisible = false;
                            GridView grid = (GridView) findViewById(R.id.beh_grid);
                            grid.setVisibility(View.GONE);
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
        });

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer historySize = adapter.stepBack();
                moveToPerson(session.getActualNumber());
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.beep1);
                mp.start();
                if (historySize == 0) {
                    btnUndo.setEnabled(false);
                }
            }
        });

        btnGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    btnGender.setText(genderList.get(genderList.indexOf(btnGender.getText()) + 1));
                } catch (Exception e) {
                    btnGender.setText(genderList.get(0));
                }
            }
        });

        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    btnGroup.setText(groupList.get(groupList.indexOf(btnGroup.getText()) + 1));
                } catch (Exception e) {
                    btnGroup.setText(groupList.get(0));
                }
            }
        });
    }

    public void showResultPage(View view) {
        final Intent intent = new Intent(this, PhysicalDisplayResults.class);
        if (isRunStadionTime) {
            LayoutInflater inflater = getLayoutInflater();
            View mView = inflater.inflate(R.layout.alert_stop_cas_stadion, null);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(FitnessMainActivity.this);
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
                    session.setEndStadiumTime(new Date().getTime());
                    db.updateSessionEndTime(session);
                    btnStadion.setText(R.string.btnStadion);
                    setBtnStadionAction("passive");
                    btnStadion.setEnabled(false);
                    isRunStadionTime = false;
                    intent.putExtra("SESSION_ID", session.id);
                    startActivity(intent);

                }

            });
            mNie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    intent.putExtra("SESSION_ID", session.id);
                    startActivity(intent);
                }

            });
        } else {
            intent.putExtra("SESSION_ID", session.id);
            startActivity(intent);
        }
    }


    public void printLists(View view) {

        try {
            PhysicalFileManager.makeExcel_PhysicalPeople(db.getAllTelesnaResultsinSession(session), session.getTimeStamp());
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

    public void operateStadiumTimer(View view) {
        final TextView tv = (TextView) findViewById(R.id.btnStadion);
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.alert_start_cas_stadion, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FitnessMainActivity.this);
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

                tv.setText("Beží ... ");
                session.setStartStadiumTime(new Date().getTime());
                db.updateSessionStartTime(session);
                isRunStadionTime = true;

                tv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        LayoutInflater inflater = getLayoutInflater();
                        View mView = inflater.inflate(R.layout.alert_stop_cas_stadion, null);
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FitnessMainActivity.this);
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
                                session.setEndStadiumTime(new Date().getTime());
                                db.updateSessionEndTime(session);
                                isRunStadionTime = false;
                                setBtnStadionAction("passive");
                                btnStadion.setEnabled(false);
                                tv.setText(R.string.btnStadion);
                                Log.d("ShootsMainActivity", "End: " + stadiumTime);

                            }

                        });
                        mNie.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }

        });
        mNie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                isRunStadionTime = false;
            }
        });

    }

    @Override
    public void onDialogPositiveClick(RunDialog dialog) {

        LinearLayout l = (LinearLayout) findViewById(R.id.timer_layout);
        l.setVisibility(View.VISIBLE);
        isGridViewVisible = true;
        myTimer = new MyTimer(this, btnStartTimer, textTimer);
        final List<Integer> numbers = dialog.getSelectedNumbers();
        List<Integer> scores = new ArrayList<Integer>();
        for (Integer dress : numbers) {
            PhysicalResult r = db.findPersonInSessionByDress(session, dress);
            scores.add(Integer.parseInt(r.disciplines.get("12min")));
        }
        adapter = new ButtonGrid(FitnessMainActivity.this, numbers, scores);
        GridView grid = (GridView) findViewById(R.id.beh_grid);
        grid.setAdapter(adapter);
        grid.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDialogNegativeClick(RunDialog dialog) {

    }

    @Override
    public void onAfterDialogPositiveClick(AfterRunDialog dialog) {
        Integer dressNum = dialog.dress;
        db.addRoundToPerson(session, dressNum, dialog.getAddedValue());
        moveToPerson(dressNum);

        // hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        adapter.addToColored(dressNum);
        adapter.updateScores();
    }

    @Override
    public void onAfterDialogNegativeClick(AfterRunDialog dialog) {

    }

    // << ----------------------------------- END LISTENERS ----------------------------------------->>

    //< ------------------- MOVING ---------------------------- >
    public void moveToPerson(String state) {   // state can be "next" - next person , "prev" - previous person , Int - number of person
        int num = session.getActualNumber();
        if (state.equals("next")) {
            num++;
        } else if (state.equals("prev")) {
            num--;
        }
        moveToPerson(num);
    }

    public void moveToPerson(int num) {
        /* check if values has been stored*/
        clearPersonalInfo();
        clearTelesnaResults();
        /* Dress Moving */
        session.setActualNumber(num);
        textDressNumber.setText(new Integer(session.getActualNumber()).toString());
        db.updateSessionDress(session, session.getActualNumber());
        if (!(db.findPersonInSessionByDress(session, session.getActualNumber()) == null)) {
            PhysicalResult result = db.findPersonInSessionByDress(session, session.getActualNumber());
            showPersonalInfo(result.person);
            setBtnAddPersonAction("edit");

            showTelesnaResults(result.disciplines);
            //if woman disable jump
            if (result.person.getGender().equals("f")) {
                editDisciplines.get(EDIT_JUMP).setText("---");
                focusEdit(getFirstEmptyDisciplinesEdit());          /* because after disambling ouse loosis focus */

                disableJumpEdit();
            } else {
                focusEdit(getFirstEmptyDisciplinesEdit());          /* because after disambling ouse loosis focus */

                enableJumpEdit();
            }
            setBtnWriteResultAction("active");
        } else {
            focusEdit(editID);
            setBtnAddPersonAction("add");

            setBtnWriteResultAction("passive");
        }

    }

    public boolean compareTelesnaResults(HashMap<String, String> n, HashMap<String, String> o) {
        boolean change = false;
        if (!n.get("pull_up").equals(o.get("pull_up")) ||
                !n.get("jump").equals(o.get("jump")) ||
                !n.get("crunch").equals(o.get("crunch")) ||
                !n.get("sprint").equals(o.get("sprint")) ||
                !n.get("12min").equals(o.get("12min")) ||
                !n.get("swimming").equals(o.get("swimming"))) {
            change = true;
        }
        return change;
    }

    //< ------------------- END MOVING ---------------------------- >>
    // << ------------------------------- HELP ---------------------->>
    public void alert(String Message) {
        new AlertDialog.Builder(this)
                .setTitle("Sprava")
                .setMessage(Message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void clearPersonalInfo() {
        editID.setText("");
        clearPersonalInfoExceptId();
    }

    public void clearPersonalInfoExceptId() {
        editName.setText("");
        editRank.setSelection(RankENUM.EMPTY.ordinal());
        editCentrum.setSelection(CentrumENUM.EMPTY.ordinal());
        editBorn.setText("19");
        btnGender.setText(genderList.get(0));
        btnGroup.setText(groupList.get(0));
        btnGun.setText(gunList.get(0));
        editAttempt.setText("");
    }

    public void clearTelesnaResults() {
        for (EditText edit : editDisciplines) {
            edit.setText("");
        }
    }

    public void showTelesnaResults(HashMap<String, String> results) {
        results = validateTelesnaResultsForForm(results);
        editDisciplines.get(EDIT_PULL_UP).setText(results.get("pull_up"));
        editDisciplines.get(EDIT_JUMP).setText(results.get("jump"));
        editDisciplines.get(EDIT_CRUNCH).setText(results.get("crunch"));
        editDisciplines.get(EDIT_SPRINT).setText(results.get("sprint"));
        editDisciplines.get(EDIT_12min).setText(results.get("12min"));
        editDisciplines.get(EDIT_SWIMMING).setText(results.get("swimming"));
    }

    public void showPersonalInfo(Person person) {
        editID.setText(person.id.toString());
        editName.setText(person.name.toString());
        editRank.setSelection(person.getRank().ordinal());
        editCentrum.setSelection(person.getCentrum().ordinal());
        editBorn.setText(person.getDateOfBirth().toString());
        editAttempt.setText(person.getAttempt().toString());
        Log.d("getGroup", person.getGroup());
        if (person.getGroup().equals("I")) {
            btnGroup.setText(groupList.get(1));
        } else if (person.getGroup().equals("II")) {
            btnGroup.setText(groupList.get(2));
        } else {
            btnGroup.setText(groupList.get(0));
        }
        if (person.getGender().equals("m")) {
            btnGender.setText(genderList.get(1));
        } else if (person.getGender().equals("f")) {
            btnGender.setText(genderList.get(2));
        } else {
            btnGender.setText(genderList.get(0));
        }

        if (person.getGun().equals("L")) {
            btnGun.setText(gunList.get(1));
        } else if (person.getGun().equals("M")) {
            btnGun.setText(gunList.get(2));
        } else {
            btnGun.setText(gunList.get(0));
        }
    }

    public void showPersonalInfoWithoutId(Person person) {
        editName.setText(person.name.toString());
        editRank.setSelection(person.getRank().ordinal());
        editCentrum.setSelection(person.getCentrum().ordinal());
        editBorn.setText(person.getDateOfBirth().toString());
        editAttempt.setText(person.getAttempt().toString());
        if (person.getGroup().equals("I")) {
            btnGroup.setText(groupList.get(1));
        } else if (person.getGroup().equals("II")) {
            btnGroup.setText(groupList.get(2));
        } else {
            btnGroup.setText(groupList.get(0));
        }
        if (person.getGender().equals("m")) {
            btnGender.setText(genderList.get(1));
        } else if (person.getGender().equals("f")) {
            btnGender.setText(genderList.get(2));
        } else {
            btnGender.setText(genderList.get(0));
        }

        if (person.getGun().equals("L")) {
            btnGun.setText(gunList.get(1));
        } else if (person.getGun().equals("M")) {
            btnGun.setText(gunList.get(2));
        } else {
            btnGun.setText(gunList.get(0));
        }
    }

    public void disableJumpEdit() {
        editDisciplines.get(EDIT_JUMP).setEnabled(false);
        editDisciplines.get(EDIT_JUMP).setInputType(InputType.TYPE_NULL);
        editDisciplines.get(EDIT_JUMP).setFocusable(false);
    }

    public void enableJumpEdit() {
        editDisciplines.get(EDIT_JUMP).setEnabled(true);
        editDisciplines.get(EDIT_JUMP).setInputType(InputType.TYPE_CLASS_NUMBER);
        editDisciplines.get(EDIT_JUMP).setFocusable(true);
        editDisciplines.get(EDIT_JUMP).setFocusableInTouchMode(true);
    }

    public void focusEdit(EditText e) {
        if (e.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public EditText getFirstEmptyDisciplinesEdit() {
        EditText e = editDisciplines.get(EDIT_PULL_UP);
        for (EditText edit : editDisciplines) {
            if (edit.getText().toString().equals("")) {
                e = edit;
                break;
            }
        }
        return e;
    }

    public void setBtnAddPersonAction(String state) {
        if (state.equals("add")) {
            btnAddPerson.setText(R.string.pridaj_osobu);
            btnAddPersonState = "add";
        } else if (state.equals("edit")) {
            btnAddPerson.setText(R.string.uprav_osobu);
            btnAddPersonState = "edit";
        }
    }

    public void setBtnWriteResultAction(String state) {
        if (state.equals("active")) {
            btnWriteResult.setBackgroundResource(R.drawable.roundedbuttonactive);
            btnWriteResultState = "active";
            btnWriteResult.setEnabled(true);
        } else if (state.equals("passive")) {
            btnWriteResult.setBackgroundResource(R.drawable.roundedbuttonpassive);
            btnWriteResultState = "passive";
            btnWriteResult.setEnabled(false);
        }
    }

    public void setBtnStadionAction(String state) {
        if (state.equals("active")) {
            btnStadion.setBackgroundResource(R.drawable.roundedbuttonactive);
            btnStadionState = "active";
        } else if (state.equals("passive")) {
            btnStadion.setBackgroundResource(R.drawable.roundedbuttonpassive);
            btnStadionState = "passive";
        }
    }

    public void showUnsavedDisciplinesAlert(final Integer oldNumber, final HashMap<String, String> newResult) {
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.alert_zmena_vykonu_telesna, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FitnessMainActivity.this);
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
                db.updatePersonTelesnaResults(session, oldNumber, newResult);

            }
        });

        mNie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    public HashMap<String, String> validateTelesnaResultsForDb(HashMap<String, String> results) {
        /* if result equals "" set to 0 ; storing numbers in database */
        if (results.get("pull_up").equals("")) {
            results.put("pull_up", "0");
        }
        if (results.get("jump").equals("") || results.get("jump").equals("---")) {
            results.put("jump", "0");
        }
        if (results.get("crunch").equals("")) {
            results.put("crunch", "0");
        }
        if (results.get("sprint").equals("")) {
            results.put("sprint", "0");
        }
        if (results.get("12min").equals("")) {
            results.put("12min", "0");
        }
        if (results.get("swimming").equals("")) {
            results.put("swimming", "0");
        }
        return results;
    }

    public HashMap<String, String> validateTelesnaResultsForForm(HashMap<String, String> results) {
        /* if result equals 0 set to "" ; showing "" in form */
        if (results.get("pull_up").equals("0")) {
            results.put("pull_up", "");
        }
        if (results.get("jump").equals("0")) {
            results.put("jump", "");
        }
        if (results.get("crunch").equals("0")) {
            results.put("crunch", "");
        }
        if (results.get("sprint").equals("0")) {
            results.put("sprint", "");
        }
        if (results.get("12min").equals("0")) {
            results.put("12min", "");
        }
        if (results.get("swimming").equals("0")) {
            results.put("swimming", "");
        }
        return results;
    }


    public HashMap<String, String> getTelesnaResultsFromForm() {
        HashMap<String, String> results = new HashMap<String, String>();
        results.put("pull_up", editDisciplines.get(EDIT_PULL_UP).getText().toString());
        results.put("jump", editDisciplines.get(EDIT_JUMP).getText().toString());
        results.put("crunch", editDisciplines.get(EDIT_CRUNCH).getText().toString());
        results.put("sprint", editDisciplines.get(EDIT_SPRINT).getText().toString());
        results.put("12min", editDisciplines.get(EDIT_12min).getText().toString());
        results.put("swimming", editDisciplines.get(EDIT_SWIMMING).getText().toString());
        results = validateTelesnaResultsForDb(results);
        return results;
    }


    // << ------------------------------- END HELP ---------------------->>
}