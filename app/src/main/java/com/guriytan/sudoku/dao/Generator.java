package com.guriytan.sudoku.dao;

import com.guriytan.sudoku.jdbc.*;

import java.util.Collections;
import java.util.Random;
import java.util.Stack;

// 生成数独
public class Generator implements Runnable {
    private Random r = new Random();
    private static final int MAX_TIMES = 100;
    private int currentTimes = 0;
    private volatile int[][] sudokuMartix = new int[9][9];
    private int[] level = {17, 29, 41, 53, 65}; // 不同游戏难度挖空的数量
    private int hard; // 玩家选择的难度，1-4由低难度到高难度，默认难度2
    private int answer = 0; // 数独解的数量

    public Generator(int hard) {
        this.hard = hard;
    }

    // 按照不同难度挖空
    private void fitZeros() {
        int number = r.nextInt(level[hard] - level[hard - 1]) + level[hard - 1];
        switch (hard) {
            case 1:
            case 2:
            case 3: {
                Stack<Integer> tmp = new Stack<>();
                Stack<Integer> reserve = new Stack<>();
                for (int i = 0; i < 81; i++)
                    tmp.push(i);
                Collections.shuffle(tmp);
                for (int i = 0; i < number; i++) {
                    answer = 0;
                    int loc = r.nextInt(tmp.size());
                    int pos = tmp.get(loc);
                    while (reserve.contains(pos)) {
                        loc = r.nextInt(tmp.size());
                        pos = tmp.get(loc);
                    }
                    int temp = validate(pos);
                    if (answer != 1) {
                        reserve.add(pos);
                        sudokuMartix[pos / 9][pos % 9] = temp;
                        i -= 1;
                    }
                    tmp.remove(loc);
                }
                break;
            }
            case 4: {
                int digg = 0;
                for (int i = 0; i < 81; i += 2) {
                    answer = 0;
                    int temp = validate(i);
                    if (answer != 1) {
                        sudokuMartix[i / 9][i % 9] = temp;
                    } else
                        digg++;
                    if (digg >= number || i == 79)
                        break;
                    else if (i == 80)
                        i = 1;
                }
                break;
            }
        }
    }

    private int validate(int locate) {
        int temp = sudokuMartix[locate / 9][locate % 9];
        sudokuMartix[locate / 9][locate % 9] = 0;
        checkinCavern(sudokuMartix, 0, 0);
        return temp;
    }

    private void sudokuGenerator() {
        for (int row = 0; row < 9; row++) {
            if (row == 0)
                sudokuMartix[row] = arrayGenerator();
            else {
                int[] tmp = arrayGenerator();
                for (int col = 0; col < 9; col++) {
                    if (currentTimes < MAX_TIMES) {
                        if (!checkinGenerator(tmp, row, col)) {
                            initialzeRow(row--);
                            col = 8;
                        }
                    } else {
                        row = -1;
                        col = 8;
                        initialzeAll();
                        currentTimes = 0;
                    }
                }
            }
        }
    }

    private boolean checkinGenerator(int[] array, int row, int col) {
        for (int i = 0; i < 9; i++) {
            sudokuMartix[row][col] = array[i];
            if (repeat(row, col)) {
                return true;
            }
        }
        return false;
    }

    private boolean repeat(int row, int col) {
        int tmp = sudokuMartix[row][col];
        for (int i = 0; i < row; i++) {
            if (tmp == sudokuMartix[i][col])
                return false;
        }
        for (int j = 0; j < col; j++) {
            if (tmp == sudokuMartix[row][j])
                return false;
        }
        int baseRow = row / 3 * 3;
        int baseCol = col / 3 * 3;
        for (int i = baseRow; i < baseRow + 3; i++) {
            for (int j = baseCol; j < baseCol + 3; j++) {
                if (tmp == sudokuMartix[i][j] && i != row && j != col)
                    return false;
            }
        }
        return true;
    }

    private int[] arrayGenerator() {
        currentTimes++;
        int[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int i = array.length; i > 0; i--) {
            int Position = r.nextInt(i);
            int tmp = array[Position]; // 将最后一位的数值与cbPosition位置的数值交换
            array[Position] = array[i - 1];
            array[i - 1] = tmp;
        }
        return array;
    }

    private void initialzeAll() {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                sudokuMartix[i][j] = 0;
    }

    private void initialzeRow(int row) {
        for (int j = 0; j < 9; j++)
            sudokuMartix[row][j] = 0;
    }

    private void checkinCavern(int[][] Martix, int row, int col) {
        if (answer < 2) {
            if (row >= Martix.length) {
                answer++;
                return;
            }
            if (col >= Martix.length) {
                checkinCavern(Martix, row + 1, 0);
                return;
            }
            if (Martix[row][col] == 0) {
                Stack<Integer> choice = new Stack<>();
                int baseRow = row / 3 * 3, baseCol = col / 3 * 3;
                for (int i = 0; i < 9; i++) {
                    if (Martix[i][col] != 0)
                        choice.add(Martix[i][col]);
                    if (Martix[row][i] != 0)
                        choice.add(Martix[row][i]);
                    if (Martix[i / 3 + baseRow][i % 3 + baseCol] != 0)
                        choice.add(Martix[i / 3 + baseRow][i % 3 + baseCol]);
                }
                for (int i = 1; i <= Martix.length; i++) {
                    if (!choice.contains(i))
                        if (checked(Martix, row, col, i)) {
                            Martix[row][col] = i;
                            checkinCavern(Martix, row, col + 1);
                            Martix[row][col] = 0;
                        }
                }
            } else {
                checkinCavern(Martix, row, col + 1);
            }
        }
    }

    private boolean checked(int[][] Martix, int row, int col, int number) {
        int baseRow = row / 3 * 3, baseCol = col / 3 * 3;
        for (int i = 0; i < Martix.length; i++) {
            if (Martix[i][col] == number)
                return false;
            if (Martix[row][i] == number)
                return false;
            if (Martix[i / 3 + baseRow][i % 3 + baseCol] == number)
                return false;
        }
        return true;
    }

    private void change() {
        Random random = new Random();
        // 换行
        for (int i = 0; i < 9; i++) {
            int base = (i % 3) * 3;
            //按行交换
            int r1 = random.nextInt(3) + base;
            int r2 = random.nextInt(3) + base;
            int c1 = random.nextInt(3) + base;
            int c2 = random.nextInt(3) + base;
            swap(r1, r2, c1, c2);
        }
        // 换九宫格
        for (int i = 0; i < 2; i++) {
            //按行交换
            int r1 = 3 * random.nextInt(3);
            int r2 = 3 * random.nextInt(3);
            int c1 = 3 * random.nextInt(3);
            int c2 = 3 * random.nextInt(3);
            int count = 0;
            while (count < 3) {
                swap(r1, r2, c1, c2);
                r1++;
                r2++;
                c1++;
                c2++;
                count++;
            }
        }
    }

    private void swap(int r1, int r2, int c1, int c2) {
        for (int j = 0; j < 9; j++) {
            int tmp = sudokuMartix[r1][j];
            sudokuMartix[r1][j] = sudokuMartix[r2][j];
            sudokuMartix[r2][j] = tmp;
            tmp = sudokuMartix[j][c1];
            sudokuMartix[j][c1] = sudokuMartix[j][c2];
            sudokuMartix[j][c2] = tmp;
        }
    }

    @Override
    public void run() {
        initialzeAll();
        sudokuGenerator();
        fitZeros();
        change();
        if (hard == 1) {
            SQLiteJDBC.insert("CASUAL", sudokuMartix);
        } else if (hard == 2) {
            SQLiteJDBC.insert("EASY", sudokuMartix);
        } else if (hard == 3) {
            SQLiteJDBC.insert("MID", sudokuMartix);
        } else if (hard == 4) {
            SQLiteJDBC.insert("HARD", sudokuMartix);
        }
        Main.count--;
    }
}
