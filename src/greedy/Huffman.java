package greedy;

import javax.swing.*;
import java.io.*;

public class Huffman {
    private String text = ""; // 적재된 텍스트 파일을 저장할 문자열
    private long fileSize; // 원래 파일의 크기(byte)
    public static void main(String[] args) {
        Huffman huffman = new Huffman();
        huffman.fileChoose();
    }
    private void fileChoose(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        int res = fileChooser.showOpenDialog(null);
        if(res == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            fileSize = file.length();
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String read;
                while ((read = bufferedReader.readLine()) != null){
                    text += "\n" + read;
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(text);
        System.out.println(fileSize + " bytes");
    }
}
