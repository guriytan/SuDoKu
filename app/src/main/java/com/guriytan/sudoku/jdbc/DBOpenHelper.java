package com.guriytan.sudoku.jdbc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.*;

public class DBOpenHelper {
    private int BUFFER_SIZE = 400000;
    public static final String DB_NAME = "sudoku.db"; //保存的数据库文件名
    private static final String PACKAGE_NAME = "com.guriytan.sudoku";//此处改为自己应用的包名。
    public static final String DB_PATH = "/data/data/"
            + PACKAGE_NAME + "/databases";  //在手机里存放数据库的位置
    private SQLiteDatabase database;
    private Context context;

    public DBOpenHelper(Context context) {
        this.context = context;
    }

    public void openDatabase() {
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    private SQLiteDatabase openDatabase(String dbfile) {
        try {
            if (!(new File(dbfile).exists())) {
                //没有创建文件夹
                File f = new File(DB_PATH);
                if (!f.exists()) {
                    f.mkdir();
                }
                //判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                InputStream is = this.context.getResources().getAssets().open(DB_NAME); //欲导入的数据库
                FileOutputStream fos = new FileOutputStream(new File(dbfile));
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,
                    null);
            return db;
        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return null;
    }

    public void closeDatabase() {
        this.database.close();
    }
}