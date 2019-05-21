package com.guriytan.sudoku.dao;

import java.util.Random;
import java.util.concurrent.Callable;

// 生成数独
public class Generator implements Callable<int[][]> {
    private int[][] game = { // 初始化生成数独
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
    private int[][] result; // 储存答案的数独
    private int minFilled; // 对应难度下最小的已知格子数
    private int minKnow; // 对应难度下行列已知格的底线

    public Generator(int hard) {
        if (hard > 0 && hard < 6) {
            LevelSudoku(hard);
        } else {
            LevelSudoku(3);
        }
    }

    private void LevelSudoku(int n) { // 生成一个指定等级的数独，且只有唯一解
        Random rand = new Random();
        int p = rand.nextInt(9), q = rand.nextInt(9); // 初始当前挖洞位置
        int P = p, Q = q; // 保存初始挖洞位置
        Point next; // 下一个挖洞位置
        int filled = 81; // 当前已知格子数
        RandSudoku(); // 随机产生11个已知格的数独
        if (SolveSudoku(game)) { // 生成的数独有解（99.7%有解）
            for (int i = 0; i < 9; i++) {
                System.arraycopy(result[i],0,game[i],0,result[i].length);
            }
        } else {
            System.exit(0);
        }
        EqualChange(game); // 等效变换
        SetLevel(n); // 初始化minFilled和minKnow
        do {
            if (isOnlyOne(p, q, game) && MinKnow(p, q, game) >= minKnow) { // 此洞可挖
                game[p][q] = 0;
                filled--;
            }
            next = FindNext(p, q, n);
            p = next.x;
            q = next.y;
            if (n == 1 || n == 2) {
                while (p == P && q == Q) {
                    next = FindNext(p, q, n);
                    p = next.x;
                    q = next.y;
                }
            }
        } while (filled > minFilled && (P != p || Q != q));
    }

    private void RandSudoku() { // 随机选取n个空格放入1-9的随机数
        int n = 11;
        Random rand = new Random();
        int rand1 = 0;
        Point[] rand2 = new Point[n]; // rand2储存n个空格的行列
        for (int k = 0; k < n; k++) { // 初始化
            rand2[k] = new Point();
        }
        for (int k = 0; k < n; k++) { // 在n个空格产生随机数
            rand2[k].x = rand.nextInt(9); // 产生0-8的随机数,表示第k个空格的行
            rand2[k].y = rand.nextInt(9); // 产生0-8的随机数,表示第k个空格的列
            for (int p = 0; p < k; p++) {
                if (rand2[k].equals(rand2[p])) { // 生成了重复位置的空格
                    k--; // 重新生成
                    break;
                }
            }

        }
        for (int k = 0; k < n; k++) {
            rand1 = 1 + rand.nextInt(9);
            game[rand2[k].x][rand2[k].y] = rand1;
            if (test(game, rand2[k].x, rand2[k].y) == 0) { // 生成的空格填入的数字错误
                k--;
            }
        }
    }

    private boolean SolveSudoku(int[][] b) { // 判断数独b是否能解
        int cout = 0;
        int[][] temp = new int[9][9]; // 复制数独b
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                temp[i][j] = b[i][j];
                if (temp[i][j] == 0) {
                    cout++;
                }
            }
        }
        Point[] fill = new Point[cout]; // 储存空位子的横纵坐标
        int k = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (temp[i][j] == 0) {
                    fill[k] = new Point(i, j);
                    k++; // 空位子数目
                }
            }
        }
        if (test(temp) == 0) {
            return false;
        } else if (k == 0 && test(temp) == 1) { // 玩家填满格子，并且正确，再来求答案
            return true;
        } else if (put(temp, 0, fill)) {
            result = temp;
            return true;
        } else {
            return false;
        }
    }

    private void EqualChange(int[][] b) { // 将终盘等效变换
        Random rand = new Random();
        int num1 = 1 + rand.nextInt(9); // 将所有的1与num1互换
        int num2 = 1 + rand.nextInt(9); // 将所有的2与num2互换
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (b[i][j] == 1) {
                    b[i][j] = num1;
                } else if (b[i][j] == num1) {
                    b[i][j] = 1;
                }

                if (b[i][j] == 2) {
                    b[i][j] = num2;
                } else if (b[i][j] == num2) {
                    b[i][j] = 2;
                }
            }
        }
    }

    private void SetLevel(int n) { // 设置难度等级 ,1-5对应初级，低级，中级，高级，骨灰级
        Random rand = new Random();
        switch (n) { // 初始化minFilled和minKnow
            case 1:
                minFilled = 51 + rand.nextInt(10);
                minKnow = 5;
                break;
            case 2:
                minFilled = 41 + rand.nextInt(10);
                minKnow = 4;
                break;
            case 3:
                minFilled = 31 + rand.nextInt(10);
                minKnow = 3;
                break;
            case 4:
                minFilled = 21 + rand.nextInt(10);
                minKnow = 2;
                break;
            case 5:
                minFilled = 11 + rand.nextInt(10);
                minKnow = 0;
                break;
        }
    }

    private boolean isOnlyOne(int i, int j, int[][] b) { // 判断在i,j挖去数字后是否有唯一解
        int k = b[i][j]; // 待挖洞的原始数字
        for (int num = 1; num < 10; num++) {
            b[i][j] = num;
            if (num != k && SolveSudoku(b)) { // 除待挖的数字之外，还有其他的解，则返回false
                b[i][j] = k;
                return false;
            }
        }
        b[i][j] = k;
        return true; // 只有唯一解则返回true
    }

    private int MinKnow(int p, int q, int[][] b) { // 返回若将p q挖去后行列中已知格数的低限
        int temp = b[p][q];
        int minKnow = 9;
        int tempRowKnow = 9;
        int tempColKnow = 9;
        b[p][q] = 0;
        for (int i = 0; i < 9; i++) { // 搜索行最小已知
            for (int j = 0; j < 9; j++) {
                if (b[i][j] == 0) {
                    tempRowKnow--;
                    if (tempRowKnow < minKnow) {
                        minKnow = tempRowKnow;
                    }
                }
                if (b[j][i] == 0) {
                    tempColKnow--;
                    if (tempColKnow < minKnow) {
                        minKnow = tempColKnow;
                    }
                }
            }
            tempColKnow = 9;
            tempRowKnow = 9;
        }
        b[p][q] = temp;
        return minKnow;
    }

    private Point FindNext(int i, int j, int n) { // 设置对应难度下的挖洞顺序，参数i,j表示当前要挖洞的位置，n表示难度，返回值是下一个要挖的洞的位置
        Random rand = new Random();
        Point next = new Point();
        switch (n) {
            case 1: // 难度1随机
            case 2:
                next.x = rand.nextInt(9);
                next.y = rand.nextInt(9);
                break; // 难度2随机
            case 3: // 难度3间隔
                if (i == 8 && j == 7) {
                    next.x = 0;
                    next.y = 0;
                } else if (i == 8 && j == 8) {
                    next.x = 0;
                    next.y = 1;
                } else if ((i % 2 == 0 && j == 7) || (i % 2 == 1) && j == 0) {
                    next.x = i + 1;
                    next.y = j + 1;
                } else if ((i % 2 == 0 && j == 8) || (i % 2 == 1) && j == 1) {
                    next.x = i + 1;
                    next.y = j - 1;
                } else if (i % 2 == 0) {
                    next.x = i;
                    next.y = j + 2;
                } else if (i % 2 == 1) {
                    next.x = i;
                    next.y = j - 2;
                }
                break;
            case 4: // 难度4蛇形
                if (i == 8 && j == 8) {
                    next.x = 0;
                    next.y = 0;
                } else if (i % 2 == 0 && j < 8) { // 蛇形顺序，对下个位置列的求解
                    next.x = i;
                    next.y = j + 1;
                } else if ((i % 2 == 0 && j == 8) || (i % 2 == 1 && j == 0)) {
                    next.x = i + 1;
                    next.y = j;
                } else if (i % 2 == 1 && j > 0) {
                    next.x = i;
                    next.y = j - 1;
                } else
                    next.x = i;
                break;
            case 5: // 难度5从左至右，自顶向下
                if (j == 8) {
                    if (i == 8) {
                        next.x = 0;
                    } else {
                        next.x = i + 1;
                    }
                    next.y = 0;
                } else {
                    next.x = i;
                    next.y = j + 1;
                }
                break;
        }
        return next;
    }

    private int test(int[][] b) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (b[i][j] != 0 && test(b, i, j) == 0) {
                    return 0;
                }
            }
        }
        return 1;
    }

    private int test(int[][] b, int i, int j) { // 检验位置i,j 数字num是否可行
        int m, n, p, q; // m,n是计数器，p,q用于确定test点的方格位置
        for (m = 0; m < 9; m++) {
            if (m != i && b[m][j] == b[i][j]) {
                return 0;
            }
        }
        for (n = 0; n < 9; n++) {
            if (n != j && b[i][n] == b[i][j]) {
                return 0;
            }
        }
        for (p = i / 3 * 3, m = 0; m < 3; m++) {
            for (q = j / 3 * 3, n = 0; n < 3; n++) {
                if ((p + m != i || q + n != j) && (b[p + m][q + n] == b[i][j])) {
                    return 0;
                }
            }
        }
        return 1;
    }

    private boolean put(int[][] b, int n, Point[] fill) { // 在第n个空位子放入数字
        if (n < fill.length) {
            for (int i = 1; i < 10; i++) {
                b[fill[n].x][fill[n].y] = i;
                if (test(b, fill[n].x, fill[n].y) == 1 && put(b, n + 1, fill)) {
                    return true;
                }
            }
            b[fill[n].x][fill[n].y] = 0;
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int[][] call() {
        return game;
    }
}
