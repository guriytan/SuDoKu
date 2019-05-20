package com.guriytan.sudoku.listener;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.guriytan.sudoku.MainActivity;

// 重写触摸事件
public class OnTouch implements View.OnTouchListener {
    private boolean isUp = false, isDown = false;
    private int downX = 0, downY = 0;
    private MainActivity mainActivity;

    public OnTouch(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getRawX();
                downY = (int) event.getRawY();
                isUp = false;
                isDown = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getRawX();
                int moveY = (int) event.getRawY();
                if (Math.abs(downX - moveX) < 10) {
                    if ((downY - moveY) > 30) // 上滑
                        isUp = true;
                    if ((moveY - downY) > 30) // 下滑
                        isDown = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isUp && !isDown) { // 点击事件
                    v.performClick();
                    Button bn = (Button) v;
                    for (int i = 0; i < 9; i++)
                        for (int j = 0; j < 9; j++)
                            if (mainActivity.padBtn[i][j].hasFocus()) {
                                String s = String.valueOf(bn.getText().charAt(0));
                                mainActivity.padBtn[i][j].setText(s);
                            }
                    if (gettest())  // 如果完全填入数独,则判断是否正确
                        if (judge())
                            Toast.makeText(mainActivity, "恭喜", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(mainActivity, "错误", Toast.LENGTH_SHORT).show();
                } else if (isUp) { // 增加候选数字
                    Toast.makeText(mainActivity, "上滑", Toast.LENGTH_SHORT).show();
                } else { // 删除候选数字
                    Toast.makeText(mainActivity, "下滑", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return false;
    }

    // 判断
    private boolean judge() {
        // 判断行与列
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                int tmp = mainActivity.sudokuPad[i][j];
                for (int k = 8; k > j; k--)
                    if (mainActivity.sudokuPad[i][k] == tmp)
                        return false;
                for (int k = 8; k > i; k--)
                    if (mainActivity.sudokuPad[k][j] == tmp)
                        return false;
            }
        // 判断9宫格
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                int tmp = mainActivity.sudokuPad[i][j];
                int baseRow = i / 3 * 3;
                int baseCol = j / 3 * 3;
                for (int m = baseRow + 2; m >= baseRow; m--)
                    for (int n = baseCol + 2; n >= baseCol; n--) {
                        if (mainActivity.sudokuPad[m][n] == tmp && (m != i || n != j))
                            return false;
                    }
            }
        return true;
    }

    // 读取数独,判断是否填完或者填入内容是否为数字
    private Boolean gettest() {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                CharSequence chars = mainActivity.padBtn[i][j].getText();
                if (!chars.equals("")) {
                    String string = String.valueOf(chars.charAt(0));
                    mainActivity.sudokuPad[i][j] = Integer.parseInt(string);
                } else
                    return false; // 未填完则返回false
            }
        return true; // 填完则返回true
    }
}
