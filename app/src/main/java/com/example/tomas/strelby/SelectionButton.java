package com.example.tomas.strelby;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomas on 25.9.2016.
 */
public class SelectionButton extends Button {
/* My button_osoba serves for selections
* you have to set 2 arrays - first is array of keys
*                           - second is array of values  --i have not used hashmap because i need sorted values
* array of values refers to values that will be shown inside button_osoba - view
* array fo keys servers to get values for programing purposes - i.e. button_osoba shows man program gets "m"
* */
    private List<String> keyArray;
    private List<String> valueArray;
    public SelectionButton(Context context, AttributeSet attrs) {
        // call super and setonclickevent
        super(context, attrs);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });
    }

    public void initialize(List<String> keyArray, List<String> valueArray){
        this.keyArray = keyArray;
        this.valueArray = valueArray;
    }

    public void next(){
        Integer index = valueArray.indexOf(super.getText());
        if(valueArray.size()-1 > index){
            super.setText(valueArray.get(index+1));
        }else{
            super.setText(valueArray.get(0));
        }
    }

    public void select(String key){
        super.setText(valueArray.get(keyArray.indexOf(key)));
    }

    public String getSelectedKey() {
      return keyArray.get(valueArray.indexOf(super.getText().toString()));
    }
}
