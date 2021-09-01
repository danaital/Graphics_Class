package edu.cg;

public class temp {
    public static void main(String[] args) {
        int temp = 17;
        int half = temp / 2;

        boolean[][] objInPos = new boolean[temp + 1][temp];
        for (int i = 0; i < objInPos.length; i++) {
            for (int j = 0; j < objInPos[i].length; j++) {
                objInPos[i][j] = isTrue(temp, i, j);
            }
        }
        mirror(objInPos);
        printStars(objInPos);
    }

    public static boolean isTrue(int lenSq, int i, int j) {
        if (i < 2 || lenSq - i < 2) {
            return true;
        }
        if (i < 4 || lenSq - i < 4) {
            return false;
        }
        if ((j < 2 || lenSq - j < 2)) {
            return false;
        }
        if ((i == 6) || (i == 10)) {
            return true;
        }
        int top = 4;
        int bottom = 12;
        if (Math.abs(i - j) == top || Math.abs(i + j) == bottom) return true;
        return false;
    }

    public static void mirror(boolean[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                arr[i][arr[i].length - j - 1] = arr[i][j];
            }
        }
    }

    public static void print2Darr(boolean[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void printStars(boolean[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j]) System.out.print("*");
                else System.out.print(" ");
            }
            System.out.println();
        }
    }
}
