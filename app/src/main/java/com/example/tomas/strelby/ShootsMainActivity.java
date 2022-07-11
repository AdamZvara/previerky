package com.example.tomas.strelby;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomas.R;
import com.example.tomas.common.CentrumENUM;
import com.example.tomas.common.RankENUM;
import com.example.tomas.common.Person;
import com.example.tomas.common.Session;
import com.example.tomas.common.Utilities;
import com.example.tomas.strelby.Exceptions.NoResultException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.text.InputType.TYPE_CLASS_NUMBER;

/*
 * Main class -
 * moveToPerson - int or string "next", "prev"
 * */
public class ShootsMainActivity extends AppCompatActivity {
    Button mNie;
    Button mAno;
    Button mOK;
    Button mZrus;
    Button mNajdi;

    EditText najdi_COZ;
    TextView editDate;
    Button btnAddPerson;
    Button btnUp;
    Button btnDown;
    Button btnShowResults;
    Button btnWriteResult;
    Spinner editCentrum;
    Spinner editRank;
    EditText editID;
    EditText editMunition;
    EditText editName;
    TextView textDressNumber;
    EditText editBorn;
    TextView editAttempt;

    Button btnStartTimer;
    TextView textTimer;

    MyTimer myTimer;

    SelectionButton btnGroup;
    SelectionButton btnGender;
    SelectionButton btnGun;
    SelectionButton btnNullTarget;
    SelectionButton btnStatus;

    EditText editRatio;
    DisciplinesEditText editPoints;
    DisciplinesEditText editTime;

    ShootsDatabaseHelper db;
    Session session;

    String btnAddPersonState = "add"; //state of add button - add - addperson, edit - edit person
    String btnWriteResultState = "active"; //active - write result , passive  - does not do anything

    MediaPlayer clickSound;

    static final int DISPLAY_RESULTS_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strelby_main);
        setIDs();
        setEvents();
        myTimer = new MyTimer(this, btnStartTimer, textTimer);
        clickSound = MediaPlayer.create(getApplicationContext(), R.raw.beep);
        initializeSelectionButtons();
        LinearLayout l = (LinearLayout) findViewById(R.id.timer_layout);
        l.setVisibility(View.GONE);
        editAttempt.setVisibility(View.GONE);

        db = new ShootsDatabaseHelper(this);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Long sessionId = intent.getLongExtra("sessionId", 0);
        session = db.findSession(sessionId); //for now use only this one session maybe later more
        if (session == null) {
            session = db.createSession(1, null);

        }

        editDate.setText(Utilities.timestampToString(session.getTimeStamp(), "dd.MM.yyyy"));


    }

    @Override
    protected void onResume() {
        super.onResume();



/*
        db.createOrUpdatePerson(new Person("123456","Tomas",1949,"I","m"));
        db.createOrUpdatePerson(new Person("123465","Adam",2001,"II","m"));
        db.addOrReplacePersonToStrelbyResults(session, db.findPerson("123456"),1);
        db.addOrReplacePersonToStrelbyResults(session, db.findPerson("123465"),2);
  */    /*  try {
        ShootsFileManager.createResultXLS(
                Arrays.asList(new ShootResult(session,db.findPerson("123456"),1),
                new ShootResult(session,db.findPerson("123465"),2)
        ));}
        catch (IOException|NoResultException e){
            e.printStackTrace();
        }*/
        //db.addOrReplacePersonToStrelbyResults(session, db.findPerson("298652"),1);
        //db.addOrReplacePersonToStrelbyResults(session, db.findPerson("286118"),2);

        //db.addOrReplacePersonToStrelbyResults(session, db.findPerson("123456"),1);
        //db.addOrReplacePersonToStrelbyResults(session, db.findPerson("123465"),2);
        //testALotOfPeople();
        moveToPerson(session.getActualNumber());
    }


    private void initializeSelectionButtons() {
        btnNullTarget.initialize(new ArrayList<String>() {{
                                     add("OK");
                                     add("N");
                                     add("D");
                                 }},
                new ArrayList<String>() {{
                    add("--");
                    add("N");
                    add("D");
                }});
        btnGender.initialize(new ArrayList<String>() {{
                                 add("empty");
                                 add("m");
                                 add("f");
                             }},
                new ArrayList<String>() {{
                    add(getResources().getString(R.string.b_empty_choice));
                    add(getResources().getString(R.string.b_man));
                    add(getResources().getString(R.string.b_woman));
                }});
        btnGun.initialize(new ArrayList<String>() {{
                              add("empty");
                              add("L");
                              add("M");
                          }},
                new ArrayList<String>() {{
                    add(getResources().getString(R.string.b_empty_choice));
                    add("L");
                    add("M");
                }});
        btnGroup.initialize(new ArrayList<String>() {{
                                add("empty");
                                add("I");
                                add("II");
                            }},
                new ArrayList<String>() {{
                    add(getResources().getString(R.string.b_empty_choice));
                    add(getResources().getString(R.string.b_group_I));
                    add(getResources().getString(R.string.b_group_II));
                }});
        btnStatus.initialize(new ArrayList<String>() {{
                                 add("empty");
                                 add("y");
                                 add("n");
                             }},
                new ArrayList<String>() {{
                    add("");
                    add(getResources().getString(R.string.b_passed));
                    add(getResources().getString(R.string.b_failed));
                }});

    }

    // << ------------------------------------------------ IDS ----------------------------------------------->>
    private void setIDs() {
        editDate = (TextView) findViewById(R.id.edit_datum);
        btnAddPerson = (Button) findViewById(R.id.b_add_person);
        editID = (EditText) findViewById(R.id.edit_id);
        editName = (EditText) findViewById(R.id.edit_name);
        textDressNumber = (TextView) findViewById(R.id.text_dress_number);
        btnUp = (Button) findViewById(R.id.b_up);
        btnDown = (Button) findViewById(R.id.b_down);
        btnWriteResult = (Button) findViewById(R.id.b_write);
        editBorn = (EditText) findViewById(R.id.edit_born);
        editAttempt = (TextView) findViewById(R.id.edit_attempt);
        editRank = (Spinner) findViewById(R.id.spinner_hodnost);
        editCentrum = (Spinner) findViewById(R.id.spinner_odbor);
        editRatio = (EditText) findViewById(R.id.edit_ratio);
        editPoints = (DisciplinesEditText) findViewById(R.id.edit_points);
        editTime = (DisciplinesEditText) findViewById(R.id.edit_time);
        btnStatus = (SelectionButton) findViewById(R.id.edit_status);
        btnShowResults = (Button) findViewById(R.id.btn_show_results);
        btnShowResults = (Button) findViewById(R.id.btn_show_results);

        btnStartTimer = (Button) findViewById(R.id.btn_start_timer);
        textTimer = (TextView) findViewById(R.id.text_time);

        btnNullTarget = (SelectionButton) findViewById(R.id.btn_null_target);
        btnGender = (SelectionButton) findViewById(R.id.btn_gender);
        btnGun = (SelectionButton) findViewById(R.id.btn_gun);
        btnGroup = (SelectionButton) findViewById(R.id.btn_group);

        Spinner spinner_hodnost = (Spinner) findViewById(R.id.spinner_hodnost);
        spinner_hodnost.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_hodnost, RankENUM.values()));

        Spinner spinner_odbor = (Spinner) findViewById(R.id.spinner_odbor);
        spinner_odbor.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_odbor, CentrumENUM.values()));

    }
    // << ------------------------------------------------ END IDS ----------------------------------------------->>


    // << ----------------------------------- LISTENERS ----------------------------------------->>
    @Override
    public void onBackPressed() {
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.alert_ukonc_aplikaciu_strelby, null);
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
                finish();
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

        btnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* get info, validate and add/edit person
                 * validation -
                 * personId, born - if not integer get value -1*/
                String name = editName.getText().toString();
                String personId = editID.getText().toString();
                RankENUM rank = (RankENUM) editRank.getSelectedItem();
                CentrumENUM centrum = (CentrumENUM) editCentrum.getSelectedItem();
                Integer born;
                try {
                    born = Integer.parseInt(editBorn.getText().toString());
                } catch (NumberFormatException e) {
                    born = -1;
                }

                if (personId.trim().length() == 0 || born == -1
                        || btnGender.getSelectedKey().equals("empty") || btnGroup.getSelectedKey().equals("empty")
                        || btnGun.getSelectedKey().equals("empty")) {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_strelby,
                            (ViewGroup) findViewById(R.id.strelby_toast_container));

                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText("Vyplň správne formulár!");

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    return;
                }

                ShootResult result = db.findPersonInStrelbyResultsById(session, personId);

                if (db.getMinDressInSession(session) != 0) {
                    if (result != null && result.dressNum != session.getActualNumber()) {    /* if person exist and we are not editing person */
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_strelby,
                                (ViewGroup) findViewById(R.id.strelby_toast_container));

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
                    //Solving problem with adding the first person and check group
                    if (db.getMinDressInSession(session) == session.getActualNumber()) { //if person has the lowest dress number
                        if (db.getMinDressInSession(session) != db.getMaxDressInSession(session)) { //if it is not only one person in results
                            if (!btnGroup.getSelectedKey().equals(db.findPersonInStrelbyResultsByDress(session, db.getMaxDressInSession(session)).person.getGroup())) {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.toast_strelby,
                                        (ViewGroup) findViewById(R.id.strelby_toast_container));

                                TextView text = (TextView) layout.findViewById(R.id.text);
                                text.setText("Nie je možné pridať osobu s inou skupinou!");

                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();
                                moveToPerson(session.getActualNumber());
                                return;
                            }
                        }
                    } else {
                        //If person has other group then the first one do not add him
                        if (!btnGroup.getSelectedKey().equals(db.findPersonInStrelbyResultsByDress(session, db.getMinDressInSession(session)).person.getGroup())) {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_strelby,
                                    (ViewGroup) findViewById(R.id.strelby_toast_container));

                            TextView text = (TextView) layout.findViewById(R.id.text);
                            text.setText("Nie je možné pridať osobu s inou skupinou!");

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            moveToPerson(session.getActualNumber());
                            return;
                        }
                    }
                }

                db.createOrUpdatePerson(new Person(personId, rank, name, centrum, born, btnGroup.getSelectedKey(), btnGender.getSelectedKey(), btnGun.getSelectedKey()));
                db.addOrReplacePersonToStrelbyResults(session, db.findPerson(personId), session.getActualNumber()); /* deletes strelby_disciplines_form and passed but that is calculated in the end */

                Person person = db.findPerson(personId);
                if (result != null) { /* when editing user remember his strelby_disciplines_form */
                    if (person.getGroup().equals("II")) {
                        db.updatePersonStrelbyResults(session, session.getActualNumber(), result.getPoints(), 120f, result.getRatio(), result.getNullTarget(), result.getStatus());
                    } else {
                        db.updatePersonStrelbyResults(session, session.getActualNumber(), result.getPoints(), 0f, result.getRatio(), result.getNullTarget(), result.getStatus());
                    }
                    if (!checkIfSaved(result)) {
                        showUnsavedDisciplinesAlert(session.getActualNumber(), getTimeFromForm(), getPointsFromForm(), getNullTargerFromForm(), result.person);
                    }
                }
                moveToPerson("next");
            }
        });


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


        editTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                evaluatePerson();
            }
        });

        editPoints.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                evaluatePerson();
            }
        });


        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShootResult result = db.findPersonInStrelbyResultsByDress(session, session.getActualNumber());
                if (result != null) {
                    Float time = getTimeFromForm();
                    Integer points = getPointsFromForm();
                    String nullTarget = getNullTargerFromForm();
                    if (!checkIfSaved(result)) {
                        showUnsavedDisciplinesAlert(session.getActualNumber(), time, points, nullTarget, result.person);
                    }
                }
                moveToPerson("next");
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShootResult result = db.findPersonInStrelbyResultsByDress(session, session.getActualNumber());
                if (result != null) {
                    Float time = getTimeFromForm();
                    Integer points = getPointsFromForm();
                    String nullTarget = getNullTargerFromForm();
                    if (!checkIfSaved(result)) {
                        showUnsavedDisciplinesAlert(session.getActualNumber(), time, points, nullTarget, result.person);
                    }
                }
                moveToPerson("prev");
            }
        });

        btnDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ShootResult result = db.findPersonInStrelbyResultsByDress(session, session.getActualNumber());
                if (result != null) {
                    Float time = getTimeFromForm();
                    Integer points = getPointsFromForm();
                    String nullTarget = getNullTargerFromForm();
                    if (!checkIfSaved(result)) {
                        showUnsavedDisciplinesAlert(session.getActualNumber(), time, points, nullTarget, result.person);
                    }
                }
                moveToPerson(db.getMinDressInSession(session));
                return true;
            }
        });
        btnUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ShootResult result = db.findPersonInStrelbyResultsByDress(session, session.getActualNumber());
                if (result != null) {
                    Float time = getTimeFromForm();
                    Integer points = getPointsFromForm();
                    String nullTarget = getNullTargerFromForm();
                    if (!checkIfSaved(result)) {
                        showUnsavedDisciplinesAlert(session.getActualNumber(), time, points, nullTarget, result.person);
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
                    db.updatePersonStrelbyResults(session, session.getActualNumber(), getPointsFromForm(), getTimeFromForm(), Float.parseFloat(editRatio.getText().toString()),
                            getNullTargerFromForm(), getStatusFromForm());
                    if (db.getMaxDressInSession(session) <= session.getActualNumber()) {
                        moveToPerson(db.getMinDressInSession(session));
                    } else {
                        while (db.findPersonInStrelbyResultsByDress(session, session.getActualNumber() + 1) == null) {
                            moveToPerson("next");
                        }
                        moveToPerson("next");
                    }
                } else if (btnWriteResultState.equals("passive")) {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_strelby,
                            (ViewGroup) findViewById(R.id.strelby_toast_container));

                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText("Najprv pridaj osobu!");

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }

            }
        });

        btnNullTarget.setOnClickListener(new View.OnClickListener() {
            @Override //rewrite the default listener for person in group I
            public void onClick(View view) {
                btnNullTarget.next();
                Person person = db.findPersonInStrelbyResultsByDress(session, session.getActualNumber()).person;
                if (person != null) {
                    evaluatePerson();
                }
            }
        });
    }

    public void showResultPage(View view) {
        Intent intent = new Intent(this, ShootsDisplayResults.class);
        intent.putExtra("SESSION_ID", session.id);
        startActivityForResult(intent, DISPLAY_RESULTS_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DISPLAY_RESULTS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Long sessionId = data.getLongExtra("SESSION_ID", 0);
                if (!sessionId.equals(0L)) {
                    session = db.findSession(sessionId);
                    moveToPerson(session.getActualNumber());
                }
            }
        }
    }

    /**
     * creates XLS file for printing with contestants, munition and display info message.
     */
    public void printLists(View view) {


        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.alert_pocet_nabojov, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShootsMainActivity.this);
        mBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setTitle(R.string.previerky_strelby);

        mOK = (Button) mView.findViewById(R.id.btnOk);
        mZrus = (Button) mView.findViewById(R.id.btnZrus);
        editMunition = (EditText) mView.findViewById(R.id.edit_munition);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP;
        dialog.show();

        mOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {

                db.updateSessionNumOfBullets(session, Integer.valueOf(editMunition.getText().toString()));
                ShootsFileManager.numOfBullets = Integer.valueOf(editMunition.getText().toString());
                dialog.dismiss();
                try {
                    ShootsFileManager.makeExcelParticipation(db.getAllTelesnaResultsinStrelbyResults(session), session);
                    ShootsFileManager.makeExcelMunition(db.getAllTelesnaResultsinStrelbyResults(session), session);
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
                    text.setText("Zoznam sa nepodarilo vytvoriť.");

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();


                }

            }
        });

        mZrus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public void najdiCOZ(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.alert_najdi_coz, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShootsMainActivity.this);
        mBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setTitle(R.string.previerky_strelby);

        mNajdi = mView.findViewById(R.id.btnNajdi);
        mZrus = mView.findViewById(R.id.btnZrus);
        najdi_COZ = mView.findViewById(R.id.edit_najdi_coz);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP;
        dialog.show();

        mNajdi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                ShootResult result = db.findPersonInStrelbyResultsById(session,najdi_COZ.getText().toString());
                if (result==null){
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_strelby,
                            (ViewGroup) findViewById(R.id.strelby_toast_container));

                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText("Osoba sa nenašla.");

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                } else {
                    moveToPerson(result.getDressNum());
                }
                dialog.dismiss();
            }
        });
        mZrus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }
    // << ----------------------------------- END LISTENERS ----------------------------------------->>
    public void evaluatePerson() {
        ShootResult currentResult = db.findPersonInStrelbyResultsByDress(session, session.getActualNumber());
        if (currentResult != null) {
            try {
                Float time = getTimeFromForm();
                Integer points = getPointsFromForm();
                try { // if btnNullTarget doesnt have text set -- just initialization
                    String nullTarget = getNullTargerFromForm();
                    editRatio.setText(String.format(Locale.US, "%.2f", ShootResult.calculateRatio(time, points, currentResult.person.getGroup())));
                    btnStatus.select(ShootResult.calculateStatus(time, points, currentResult.person.getGroup(), nullTarget) ? "y" : "n");
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.d("initialize", "initialize");
                }

            } catch (NumberFormatException e) {
                Log.d("mistake", "mistake");
            }
        }
    }

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
        clearResults();
        /* Dress Moving */
        session.setActualNumber(num);
        textDressNumber.setText(new Integer(session.getActualNumber()).toString());
        db.updateSessionDress(session, session.getActualNumber());
        if (!(db.findPersonInStrelbyResultsByDress(session, session.getActualNumber()) == null)) {
            ShootResult result = db.findPersonInStrelbyResultsByDress(session, session.getActualNumber());
            showPersonalInfo(result.person);
            setBtnAddPersonAction("edit");
            showResults(result.getPoints(), result.getTime(), result.getRatio(), result.getNullTarget(), result.getStatus() ? "y" : "n");
            //disable nullTarget when group equals II
            if (result.person.getGroup().equals("II")) {
                focusEdit(editPoints);
                editTime.setEnabled(false);
                myTimer.btnStart.setEnabled(false);
            } else if (result.person.getGroup().equals("I")) {
                focusEdit(getFirstEmptyDisciplinesEdit());
                editTime.setEnabled(true);
                myTimer.btnStart.setEnabled(true);
            }
            btnNullTarget.setEnabled(true);
            setBtnWriteResultAction("active");
        } else {
            focusEdit(editID);
            setBtnAddPersonAction("add");
            myTimer.btnStart.setEnabled(false);
            setBtnWriteResultAction("passive");
            btnNullTarget.setEnabled(false);
        }

    }

    public boolean checkIfSaved(ShootResult result) {
        if (Math.abs(result.getTime() - getTimeFromForm()) < 0.000001 && result.getPoints() == getPointsFromForm() && result.getNullTarget().equals(getNullTargerFromForm())) {
            return true;
        }
        return false;
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

    public void showAlertResults() {
        String message = "";
        List<ShootResult> rs = db.getAllTelesnaResultsinStrelbyResults(session);
        for (ShootResult r : rs) {
            if (!r.getStatus()) { //show all people that failed
                message += r.person.id + " - " + r.person.name + System.getProperty("line.separator");
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("NEPROSPELI")
                .setMessage(message)
                .setPositiveButton("Nové", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton("Opravit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void clearPersonalInfo() {
        editID.setText("");
        editName.setText("");
        editRank.setSelection(RankENUM.EMPTY.ordinal());
        editCentrum.setSelection(CentrumENUM.EMPTY.ordinal());
        editBorn.setText("19");
        btnGender.select("empty");
        btnGroup.select("empty");
        btnGun.select("empty");
        editAttempt.setText("");
    }

    public void clearPersonalInfoExceptId() {
        editName.setText("");
        editRank.setSelection(RankENUM.EMPTY.ordinal());
        editCentrum.setSelection(CentrumENUM.EMPTY.ordinal());
        editBorn.setText("19");
        btnGender.select("empty");
        btnGroup.select("empty");
        btnGun.select("empty");
        editAttempt.setText("");
    }

    public void clearResults() {
        editPoints.setText("");
        editTime.setText("");
        btnStatus.select("empty");
        btnNullTarget.select("OK");
        editRatio.setText("");
    }

    public void showResults(Integer points, Float time, Float ratio, String nullTarget, String status) {
        editPoints.setText(points.toString());
        editTime.setText(time.toString());
        editRatio.setText(ratio.toString());
        btnNullTarget.select(nullTarget);
        btnStatus.select(status);

    }

    public void showPersonalInfo(Person person) {
        editID.setText(person.id.toString());
        editRank.setSelection(person.getRank().ordinal());
        editCentrum.setSelection(person.getCentrum().ordinal());
        showPersonalInfoWithoutId(person);
    }

    public void showPersonalInfoWithoutId(Person person) {
        editName.setText(person.name);
        editRank.setSelection(person.getRank().ordinal());
        editCentrum.setSelection(person.getCentrum().ordinal());
        if (person.getDateOfBirth() == 0) {
            editBorn.setText("19");
        } else {
            editBorn.setText(person.getDateOfBirth().toString());
        }
        if (person.getGroup().equals("I")) {
            btnGroup.select("I");
        } else if (person.getGroup().equals("II")) {
            btnGroup.select("II");
        } else {
            btnGroup.select("empty");
        }
        if (person.getGender().equals("m")) {
            btnGender.select("m");
        } else if (person.getGender().equals("f")) {
            btnGender.select("f");
        } else {
            btnGender.select("empty");
        }
        if (person.getGun().equals("L") || person.getGun().equals("M"))
            btnGun.select(person.getGun());
        else btnGun.select("empty");

        editAttempt.setText(person.getAttempt().toString());
    }

    public void focusEdit(EditText e) {
        if (e.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public EditText getFirstEmptyDisciplinesEdit() {
        if (!editTime.getText().toString().equals("0") && editPoints.getText().toString().equals("0"))
            return editPoints;
        return editTime;
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
        } else if (state.equals("passive")) {
            btnWriteResult.setBackgroundResource(R.drawable.roundedbuttonpassive);
            btnWriteResultState = "passive";
        }
    }

    public void showUnsavedDisciplinesAlert(final Integer oldNum, final Float time, final Integer points, final String nullTarget, final Person person) {
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.alert_zmena_vykonu_strelby, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShootsMainActivity.this);
        mBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setTitle(R.string.previerky_strelby);

        mAno = (Button) mView.findViewById(R.id.btnAno);
        mNie = (Button) mView.findViewById(R.id.btnNie);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mAno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                db.updatePersonStrelbyResults(session, oldNum, points, time, ShootResult.calculateRatio(time, points, person.getGroup()), nullTarget, ShootResult.calculateStatus(time, points, person.getGroup(), nullTarget));
            }
        });

        mNie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    public Float getTimeFromForm() {
        return Float.parseFloat(editTime.getText().toString());
    }

    public Integer getPointsFromForm() {
        return Integer.parseInt(editPoints.getText().toString());
    }

    public String getNullTargerFromForm() {
        return btnNullTarget.getSelectedKey();
    }

    public boolean getStatusFromForm() {
        if (btnStatus.getSelectedKey().equals("y")) {
            return true;
        }

        return false;
    }


    // << ------------------------------- END HELP ---------------------->>
}
