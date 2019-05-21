package com.guriytan.sudoku.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Button;
import com.guriytan.sudoku.R;

@SuppressLint({"AppCompatCustomView", "ViewConstructor"})
public class PadButton extends Button {
    public PadButton(Context context, int number) {
        super(context);
        generate(number);
        this.setMinWidth(0);
        this.setMinimumWidth(0);
    }

    public void reset(int number) {
        if (number == 0)
            this.setText("");
        else {
            String string = number + "";
            this.setText(string);
        }
    }

    public void generate(int number) {
        if (number == 0) {
            this.setText("");
            this.setOnLongClickListener((v) -> {
                Button bn = (Button) v;
                bn.setText("");
                return true;
            });
            this.setFocusable(true);
            this.setFocusableInTouchMode(true);
            this.requestFocus();
            this.requestFocusFromTouch();
            this.setBackgroundResource(R.drawable.btn_state);
        } else {
            String string = number + "";
            this.setText(string);
            this.setFocusable(false);
            this.setFocusableInTouchMode(false);
            this.setBackgroundResource(R.color.bn_normal);
        }
    }
}
