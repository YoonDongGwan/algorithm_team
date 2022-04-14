# 연속 행렬 곱셈(Chaned Matrix Multipilications)

고예진, 윤동관, 조준희

------------------------------

## 연속 행렬 곱셈

* 연속된 행렬들에 대해 곱셈을 시행할 때 최소 곱셈 횟수를 찾는다.
* 계산은 기본 행렬 곱셈과 같은 방식으로 계산한다

  *행렬의 곱셈 조건*

    * 연산은 **앞쪽의 열과 뒤쪽의 행이 같아야** 이루어질 수 있다.   
      -> 3x2 2x2 행렬을 곱하면 3x2의 행렬이 나온다.

    * 연산의 결과 행렬의 크기는 **압쪽의 행 * 뒤쪽의 열**이다.

      `arr[i][j] += arr1[i][k] * arr2[k][j];`   
      (k는 앞쪽의 열이자 뒤쪽의 행이다.)
* 교환법칙은 허용되지 않는다.


## 연속 행렬 곱셈 JAVA로 구현

------------------------

**Header File**
* 입출력 속도 향상을 위해 `BufferedReader` 사용한다.
* 예외 처리를 위해 `IOException` 사용한다.   
  코드에서는 `throws IOException`을 사용하는데 이는 예외가 발생하면 해당 클래스에서 벗어나게 된다는 것이다.
* `InputStream`은 외부에서 데이터를 읽는 역할을 수행하고, `OutputStream'은 외부로 데이터를 출력하는 역할을 수행한다.
* `StringTokenizer`클래스를 통해 문자열을 분리한다.


    import java.io.*;
    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.OutputStream;
    import java.util.StringTokenizer;

**행렬 곱셈**
public class Lab006 {

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

결과행렬 우리는 A행XB열의 크기의 행렬을 구하는 것이고 각 요소는 행렬 곱셈법칙을 기반으로 더한다.

    arr[0][0] = arr1[0][0] * arr2[0][0] + arr1[0][1] * arr2[1][0]

    arr[0][1] = arr1[0][0] * arr2[0][1] + arr1[0][1] * arr2[1][1]
    ....

이때 반복문을 사용하여 코드를 구현할 수 있다.

A행렬의 **행**을 **i**로 B행렬의 **열**을 **j**로 arr[i][j]로 결과행렬을 만들 수 있다.

중간에 필요한 연산(EX. A[0][0] * B[0][0] + A[0][1] * B[1][0])은 A행렬의 열과 B행렬의 행의 **개수가 같다는 조건을 사용한다.**

**행렬 만들기**
* 문자열을 분리하기 위해 `StringTokennizer` 사용


    int [][] array = new int[n][2];
    StringTokenizer st;
    for(int i = 0; i < n; i++) {
    st = new StringTokenizer(mat1.readLine(), " ");
    array[i][0] = Integer.parseInt(st.nextToken());
    array[i][1] = Integer.parseInt(st.nextToken());
    }

**Dynamic Programming**


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

**최종 출력**

    System.out.print(c[i][j] + "\t\t");

-> 최종적으로 연속 행렬 곱셈 값이 출력된다.

----------------------------------

### 전체 코드

    package DynamicProgramming;

    import java.io.*;
    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.OutputStream;
    import java.util.StringTokenizer; // 문자열 분리하기


    public class MatricMulfication {
    // 행렬 곱셈
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
        BufferedReader mat1 = new BufferedReader(new InputStreamReader(System.in)); // Input
        BufferedWriter mat2 = new BufferedWriter(new OutputStreamWriter(System.out)); //Output

        int n = Integer.parseInt(mat1.readLine());

        // 행렬 만들기
        int [][] array = new int[n][2];
        StringTokenizer st;
        for(int i = 0; i < n; i++) {
            st = new StringTokenizer(mat1.readLine(), " ");
            array[i][0] = Integer.parseInt(st.nextToken());
            array[i][1] = Integer.parseInt(st.nextToken());
        }
    }

    // Dynamic Programming
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
                System.out.print(c[i][j] + "\t\t");             }
            System.out.println();
        }
        return c[0][n-1];
      }
    }