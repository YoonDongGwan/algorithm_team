package DynamicProgramming;

import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;


public class Multification {
    public int[][] Lab006(int[][] arr1, int[][] arr2) {

        int[][] arr = new int[arr1.length][arr2[0].length];

        for(int i = 0; i < arr1.length; ++i) {
            for(int j = 0; j < arr2[0].length; ++j) {
                for(int k = 0; k < arr1[0].length; ++k) {
                    arr[i][j] += arr1[i][k] * arr2[k][j];
                }
            }
        }

        return arr;
    }
    public static void main(String[] args) throws IOException {
        BufferedReader mat1 = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter mat2 = new BufferedWriter(new OutputStreamWriter(System.out));

        int n = Integer.parseInt(mat1.readLine());

        int [][] array = new int[n][2];
        StringTokenizer st;
        for(int i = 0; i < n; i++) {
            st = new StringTokenizer(mat1.readLine(), " ");
            array[i][0] = Integer.parseInt(st.nextToken());
            array[i][1] = Integer.parseInt(st.nextToken());
        }
    }

    static int Minimum(int[][]...matrix) {
        int n = matrix.length, p = 0;
        int[] d = new int[n+1];
        for (int[][] array : matrix) {
            d[p] = array.length;
            d[++p] = array[0].length;
        }

        int[][] c = new int[n][n];
        for (int a = 0; a < n; a++) {
            for (int i = 0; i < c.length - a; i++) {
                int j = i + a;
                if(i == j){
                    c[i][j] = 0;
                }
                else{
                    c[i][j] = Integer.MAX_VALUE;
                    for (int k = i; k <= j - 1; k++){
                        c[i][j] = Math.min(c[i][j], c[i][k] + c[k+1][j] + (d[i] * d[k+1] * d[j+1]));
                    }
                }
            }
        }
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                System.out.print(c[i][j] + "\t\t");
            }
            System.out.println();
        }
        return c[0][n-1];
    }
}
