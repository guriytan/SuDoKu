package com.guriytan.sudoku;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.guriytan.sudoku.dao.Generator;
import com.guriytan.sudoku.jdbc.DBOpenHelper;

import java.util.Random;

public class Problem extends Application {
    private SQLiteDatabase db;
    private Context context;
    private DBOpenHelper dbOpenHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        dbOpenHelper = new DBOpenHelper(context);
        dbOpenHelper.openDatabase();
    }

    public int[][] get(int level) {
        db = SQLiteDatabase.openOrCreateDatabase(DBOpenHelper.DB_PATH + "/" + DBOpenHelper.DB_NAME, null);
        String table = encode(level);
        int[][] data = null;
        int id = 0;
        int index = new Random().nextInt(200);
        Cursor cursor = db.query(table, null, null, null, null, null, null, index + ",1");
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
            String s = cursor.getString(1);
            data = convert(s);
        }
        cursor.close();
//        if (id != 0)
//            delete(table, id);
        db.close();
        return data;
    }

    private int[][] convert(String s) {
        int[][] tmp = new int[9][9];
        int row = 0, col = 0;
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == ';') {
                row++;
                col = 0;
            } else
                tmp[row][col++] = s.charAt(i) - '0';
        }
        return tmp;
    }

    private void delete(String table, int id) {
        db.delete(table, "ID = ?", new String[]{"id"});
    }

    private String encode(int level) {
        if (level == 1)
            return "CASUAL";
        else if (level == 2)
            return "EASY";
        else if (level == 3)
            return "MID";
        else if (level == 4)
            return "HARD";
        else
            return "EASY";
    }
}
