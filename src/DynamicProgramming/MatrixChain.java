package DynamicProgramming;

public class MatrixChain {
    public static void main(String[] args) {
        int[][] mat1, mat2, mat3, mat4;
        mat1 = new int[10][20];
        mat2 = new int[20][5];
        mat3 = new int[5][15];
        mat4 = new int[15][30];

        MatrixChain matrixChain = new MatrixChain();
        System.out.println(matrixChain.Minimum(mat1, mat2, mat3, mat4));
    }
    private int Minimum(int[][]...matrix) {
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
}
