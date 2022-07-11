package com.example.tomas.telesna;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tomas.R;

import java.util.List;

/**
 * Created by tomas on 1.9.2016.
 */
public class RunDialog extends DialogFragment {
    Button mNie;
    Button mAno;

    List numbers;
    Integer minimalNumber;
    TextView textMin;
    Button minusMin;
    Button plusMin;

    TextView textMax;
    Button minusMax;
    Button plusMax;
    public interface RunDialogListener {
        public void onDialogPositiveClick(RunDialog dialog);
        public void onDialogNegativeClick(RunDialog dialog);
    }

    // Use this instance of the interface to deliver action events
    RunDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (RunDialogListener) activity;
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
            numbers = args.getIntegerArrayList("numbers");
            minimalNumber = args.getInt("minimalNumber");
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View mView = inflater.inflate(R.layout.dialog,null);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
            mBuilder.setIcon(android.R.drawable.ic_dialog_alert);
            mBuilder.setTitle(R.string.previerky_telesna);

            mAno = (Button) mView.findViewById(R.id.btnAno);
            mNie = (Button) mView.findViewById(R.id.btnNie);
            setID(mView);
            addListeners();
            mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        mAno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                            mListener.onDialogPositiveClick(RunDialog.this);
                dialog.dismiss();
                        }
                    });
        mNie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                            mListener.onDialogNegativeClick(RunDialog.this);
                dialog.dismiss();
                        }
                    });
        fillValues();

        return dialog;
        }
    private void setID(View view){
            textMin = (TextView) view.findViewById(R.id.min_val);
            minusMin = (Button) view.findViewById(R.id.min_minus);
            plusMin = (Button) view.findViewById(R.id.min_plus);

            textMax = (TextView) view.findViewById(R.id.max_Val);
            minusMax = (Button) view.findViewById(R.id.max_minus);
            plusMax = (Button) view.findViewById(R.id.max_plus);
        }
    private void addListeners(){
        minusMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer cur = numbers.indexOf(Integer.parseInt(textMin.getText().toString()));
                cur = cur <=0 ? 0 : cur -1;
                textMin.setText(numbers.get(cur).toString());
                }
        });

        plusMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer cur =  numbers.indexOf(Integer.parseInt(textMin.getText().toString()));
                if( numbers.size() == 1 ) return;
                    if((int) numbers.get(cur) < Integer.parseInt(textMax.getText().toString())){
                        ++cur;
                    }
                textMin.setText(numbers.get(cur).toString());
            }
        });
        minusMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer cur = numbers.indexOf(Integer.parseInt(textMax.getText().toString()));
                if(numbers.size() == 1) return;
                if((int) numbers.get(cur) > Integer.parseInt(textMin.getText().toString())) {
                    --cur;
                }
                textMax.setText(numbers.get(cur).toString());
            }
        });
        plusMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer cur = numbers.indexOf(Integer.parseInt(textMax.getText().toString()));
                cur = cur >=numbers.size()-1 ? numbers.size()-1 : cur +1;
                textMax.setText(numbers.get(cur).toString());
            }
        });
    }
    private void fillValues(){
        textMin.setText(minimalNumber.toString());
        textMax.setText(numbers.get(numbers.size()-1).toString());
    }

    public List<Integer> getSelectedNumbers(){
        return numbers.subList(numbers.indexOf(Integer.parseInt(textMin.getText().toString())), numbers.indexOf(Integer.parseInt(textMax.getText().toString()))+1);//neviem preco ta 1
    }


}



