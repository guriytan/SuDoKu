package com.guriytan.sudoku.handler;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.guriytan.sudoku.MainActivity;

import java.lang.ref.WeakReference;

public class LoadingHandler extends Handler {

    private WeakReference<MainActivity> mWeakActivity;

    public LoadingHandler(MainActivity activity) {
        mWeakActivity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        MainActivity activity = mWeakActivity.get();
        if (activity != null) {
            // 更新进度条
            switch (msg.what) {
                case 1:
                    // 数独库没有数独，等待生成
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    activity.progressBar.setVisibility(View.VISIBLE);
                    Sprite wave = new Wave();
                    activity.progressBar.setIndeterminateDrawable(wave);
                    break;
                case 2:
                    // 数独库有数独，生成新盘
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    activity.progressBar.setVisibility(View.GONE);
                    activity.sudokuMartix = activity.problem.get(msg.arg1);
                    for (int i = 0; i < activity.sudokuMartix.length; i++) {
                        for (int j = 0; j < activity.sudokuMartix[0].length; j++) {
                            activity.padBtn[i][j].generate(activity.sudokuMartix[i][j]); // 使用新生成的数独矩阵数据
                        }
                    }
                    activity.pad.invalidate();
                    Thread.currentThread().interrupt();
                    break;
                case 3:
                    // 重置数独，不生成新游戏
                    for (int i = 0; i < activity.sudokuMartix.length; i++) {
                        for (int j = 0; j < activity.sudokuMartix[0].length; j++) {
                            activity.padBtn[i][j].reset(activity.sudokuMartix[i][j]); // 只把挖空的置0
                        }
                    }
                    activity.pad.invalidate();
                    Thread.currentThread().interrupt();
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
