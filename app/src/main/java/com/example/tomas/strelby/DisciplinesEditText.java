package com.example.tomas.strelby;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by tomas on 24.9.2016.
 */
public class DisciplinesEditText extends EditText {
/* edit for disciplines, extends Edit and overridesmethods
 * for setting and getting text*/

    public DisciplinesEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
    public DisciplinesEditText(Context context) {
        super(context);

    }

    public DisciplinesEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }
    public void setText(String text){
        //if text is 0 set ""
        if(text.equals("0") || text.equals("0.0")){
            super.setText("");
        }else{
            super.setText(text);
        }
    }
    @Override
    public Editable getText(){
        // if text is "" returns 0
        Editable text;
        if(super.getText().toString().equals("")){
            text = new SpannableStringBuilder("0");
        }else{
            text = super.getText();
        }
        return text;
    }
}
