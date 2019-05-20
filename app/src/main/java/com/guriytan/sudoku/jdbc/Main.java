package com.guriytan.sudoku.jdbc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.guriytan.sudoku.dao.Generator;

public class Main {
    private static ExecutorService service = Executors.newCachedThreadPool();
    public volatile static int count = 200;

    public static void main(String[] args) {
        SQLiteJDBC.setConnection();
        SQLiteJDBC.create("HARD");
        for (int i = 0; i < 200; i++) {
            service.submit(new Generator(4));
        }
        try{
            while (count != 0){
                Thread.sleep(1000);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        service.shutdown();
        SQLiteJDBC.endConnection();
    }
}
