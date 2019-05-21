package com.guriytan.sudoku;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import com.guriytan.sudoku.dao.Generator;
import com.guriytan.sudoku.handler.LoadingHandler;
import com.guriytan.sudoku.listener.OnTouch;
import com.guriytan.sudoku.view.PadButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends Activity {
    public GridLayout pad;
    public ProgressBar progressBar;
    public PadButton[][] padBtn = new PadButton[9][9];
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private LoadingHandler loadingHandler = new LoadingHandler(this,threadPool);
    private String[] chars = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
    public int[][] sudokuMartix = new int[9][9]; // 用于显示的数独
    public int[][] sudokuPad = new int[9][9]; // 储存玩家填写后的数独
    private int systemLevel = 2;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        Future<int[][]> res = threadPool.submit(new Generator(systemLevel));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress);
        Toolbar bar = findViewById(R.id.toolbar); // 设置菜单栏
        setActionBar(bar);
        bar.setNavigationIcon(R.drawable.icon);
        pad = findViewById(R.id.pad);
        try {
            sudokuMartix = res.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < sudokuMartix.length; i++) {// 形成数独主面板
            for (int j = 0; j < sudokuMartix[0].length; j++) {
                padBtn[i][j] = new PadButton(this, sudokuMartix[i][j]);
                GridLayout.LayoutParams GridLayoutParams = new GridLayout.LayoutParams();
                GridLayoutParams.rowSpec = GridLayout.spec(i, 1.0f);
                GridLayoutParams.columnSpec = GridLayout.spec(j, 1.0f);
                GridLayoutParams.setMargins(1, 1, 1, 1);
                padBtn[i][j].setLayoutParams(GridLayoutParams);
                pad.addView(padBtn[i][j]);
            }
        }
        GridLayout number = findViewById(R.id.number);
        for (int i = 0; i < chars.length; i++) { // 形成选择数字面板
            Button number_bn = new Button(this);
            number_bn.setTextSize(20f);
            number_bn.setText(chars[i]);
            number_bn.setBackgroundResource(R.drawable.btn_state);
            number_bn.setOnTouchListener(new OnTouch(this));
            GridLayout.LayoutParams GridLayoutParams = new GridLayout.LayoutParams();
            GridLayoutParams.rowSpec = GridLayout.spec(i / 3, 1.0f);
            GridLayoutParams.columnSpec = GridLayout.spec(i % 3, 1.0f);
            number.addView(number_bn, GridLayoutParams);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu); // 载入菜单
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game: // 新游戏
                new Draw(-1).start();
                break;
            case R.id.restart: // 重新开始
                new Draw(0).start();
                break;
            case R.id.casual: // 选择入门难度
                new Draw(1).start();
                break;
            case R.id.easy: // 选择简单难度
                new Draw(2).start();
                break;
            case R.id.mid: // 选择中等难度
                new Draw(3).start();
                break;
            case R.id.hard: // 选择困难难度
                new Draw(4).start();
                break;
            case R.id.tooHard: // 选择困难难度
                new Draw(5).start();
                break;
        }
        return true;
    }

    private class Draw extends Thread {
        int level;

        Draw(int level) {
            if (level > 0)
                systemLevel = level;
            this.level = level;
        }

        @Override
        public void run() {
            Message msg = new Message();
            if (level == 0) {
                // 重新开始
                msg.what = 3;
                loadingHandler.sendMessage(msg);
            } else {
                // 新游戏
                msg.what = 1;
                loadingHandler.sendMessage(msg);
                Message msg2 = new Message();
                msg2.what = 2;
                msg2.arg1 = systemLevel; // 传送数独难度
                loadingHandler.sendMessage(msg2);
            }
        }
    }
}
