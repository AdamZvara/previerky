package com.example.tomas.telesna;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tomas.R;

/**
 * Created by tomas on 1.9.2016.
 */
public class AfterRunDialog extends DialogFragment {
    Button mZapis;
    Button mZrus;
    EditText edit_Zvysne_metre;
    Integer oldValue;
    Integer dress;
    TextView oldValueText;
    TextView dressNumberText;
    EditText addedValue;

    public interface AfterRunDialogListener {
        void onAfterDialogPositiveClick(AfterRunDialog dialog);
        void onAfterDialogNegativeClick(AfterRunDialog dialog);
    }

    // Use this instance of the interface to deliver action events
    AfterRunDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AfterRunDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement this");
        }
    }
    @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // pass values
            Bundle args = getArguments();
            oldValue = args.getInt("oldValue");
            dress = args.getInt("dress");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.after_run_dialog, null);
        setID(mView);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setTitle(R.string.previerky_telesna);

        mZapis = (Button) mView.findViewById(R.id.btnZapis);
        mZrus = (Button) mView.findViewById(R.id.btnZrus);
        edit_Zvysne_metre = (EditText) mView.findViewById(R.id.edit_plus_value);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP;
        dialog.show();

        mZapis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                mListener.onAfterDialogPositiveClick(AfterRunDialog.this);
                dialog.dismiss();
            }

    });
        mZrus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mListener.onAfterDialogNegativeClick(AfterRunDialog.this);
                dialog.dismiss();
            }
        });

        fillValues();

        return dialog;
    }

    private void setID(View view){
        dressNumberText = (TextView) view.findViewById(R.id.dialog_dress_number);
        oldValueText = (TextView) view.findViewById(R.id.old_value_text);
        addedValue = (EditText) view.findViewById(R.id.edit_plus_value);
        }

    private void fillValues(){
        dressNumberText.setText(dress.toString() + " -> ");
        oldValueText.setText(oldValue.toString());
    }

    public Integer getAddedValue(){
        if(addedValue.getText().toString().trim().length() == 0 ){
            return 0;
        }else {
            return Integer.parseInt(addedValue.getText().toString());
        }
    }

}



