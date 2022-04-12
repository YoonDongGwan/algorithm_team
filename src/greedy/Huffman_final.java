package greedy;

import jdk.jfr.Frequency;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Huffman_final {
    private String text = ""; // 적재된 텍스트 파일을 저장할 문자열
    private long fileSize; // 원래 파일의 크기(byte)
    public static void main(String[] args) {
        Huffman huffman = new Huffman();
        File file = huffman.fileChoose();
        HashMap<Character, Integer> freq = huffman.countFrequency(file);
        PriorityQueue<Node> queue = huffman.makeTree(freq);
        // 여기서부턴 해쉬맵에 문자랑 빈도수 잘 들어갔는지 확인용도, 지워도됨
//        Iterator<Character> keys = freq.keySet().iterator();
//
//        while (keys.hasNext()){
//            char key = keys.next();
//            System.out.println("character : " + key + " frequency : " + freq.get(key));
//        }

        // 여기서부턴 우선순위 큐에 노드 잘 들어갔는지 확인용도, 지워도 됨
//        while (!queue.isEmpty()){
//            System.out.println(queue.remove().frequency+"");
//        }
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

        public Node(char character, int frequency, Node left, Node right) {
            this.character = character;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }
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

    public PriorityQueue makeTree(HashMap<Character, Integer> freq){
        Iterator<Character> keys = freq.keySet().iterator();
        PriorityQueue<Node> queue = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.frequency - o2.frequency;
            }
        });
        while (keys.hasNext()){
            char key = keys.next();
            Node node = new Node(key, freq.get(key), null, null);
            queue.add(node);
        }

        return queue;
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

    public static void main(String[] args) throws IOException {
        String text = new Scanner(new File("src/Alice’s Adventures in Wonderland")).next();
        System.out.println(text); // print

        HashMap<Character, Integer> dict = new HashMap<Character, Integer>();
        System.out.println(dict); //print

        PriorityQueue<Node> queue = new PriorityQueue<Node>(100, new freqComparator);
        int num = 0;
        for(Character c: dict.keySet()) {
            Node node = new Node();
            node.character = c;
            node.frequency = dict.get(c);
            queue.add(node);
            num++;
        }
        Node root = String encoding(String text, HashMap<Character, Integer> freq);

        binaryEncode(root, new String());
        String result = new String();
        for(int i = 0; i < text.length(); i++) {
            result = result + binaryEncode.get();
        }
        System.out.println(result);

        int origin = text.getBytes(StandardCharsets.UTF_8).length;

        System.out.println("기존 데이터 사이즈 : " + origin * 8 + "Bit");
        System.out.println("인코딩 데이터 사이즈 : " + result.length() + "Bit");
    }

    private static void binaryEncode(Node n, String s) {
        if (n == null) {
            return;
        }

        binaryEncode(n.left, s + "0");
        binaryEncode(n.right, s + "1");

        if(n.key != '\0') {
            System.out.println(n.key + ":" + s); // print
            binaryEncode.put(n.key, s);
        }
    }
}
