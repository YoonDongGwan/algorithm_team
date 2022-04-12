package greedy;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class Huffman {
    private String text = ""; // 적재된 텍스트 파일을 저장할 문자열
    private long fileSize; // 원래 파일의 크기(byte)
    public static void main(String[] args) {
        Huffman huffman = new Huffman();
        File file = huffman.fileChoose();
        HashMap<Character, Integer> freq = huffman.countFrequency(file);

        // 여기서부턴 그냥 문자랑 빈도수 잘 들어갔는지 확인용도, 지워도됨
        Iterator<Character> keys = freq.keySet().iterator();

        while (keys.hasNext()){
            char key = keys.next();
            System.out.println("character : " + key + " frequency : " + freq.get(key));
        }


    }
    private File fileChoose(){  // 파일 적재 -->> 파일 리턴으로 바꿈
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        File file = new File("");
        int res = fileChooser.showOpenDialog(null);
        if(res == JFileChooser.APPROVE_OPTION){
            file = fileChooser.getSelectedFile();
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
//        System.out.println(text);
//        System.out.println(fileSize + " bytes");
        return file;
    }
    public class Node{
        private char character;
        private int frequency;
        private Node left,right;
    }
    public HashMap<Character, Integer> countFrequency(File file){
        String line;
        HashMap<Character, Integer> frequency = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null){
                for (int i = 0; i < line.length(); i++){
                    char c = line.charAt(i);
                    if (frequency.containsKey(c)){
                        frequency.put(c, frequency.get(c)+1);
                    }
                    else{
                        frequency.put(c, 1);
                    }

                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return frequency;
    }


    public String encoding(String text, HashMap<Character, Integer> freq){ // 텍스트 인코딩
        int i = 0;
        String encodedText = "";
        while (i < text.length()){
            char cha = text.charAt(i);
            encodedText += freq.get(cha).toString();
            i++;
        }
        System.out.println(encodedText);
        return null;
    }

}
