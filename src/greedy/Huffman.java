package greedy;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

public class Huffman {
    private String text = ""; // 적재된 텍스트 파일을 저장할 문자열
    private HashMap<Character, String> binaryCode = new HashMap<>();
    public static void main(String[] args) {
        Huffman huffman = new Huffman();

        File file = huffman.fileChoose(); // 텍스트 파일 적재

        HashMap<Character, Integer> freq = huffman.countFrequency(file); // 적재된 파일의 문자들의 빈도수

        Node root = huffman.makeTree(freq); // 빈도수에 따른 우선순위 큐로 허프만 트리 생성

        String str = "";
        huffman.binaryEncode(root, str); // binaryCode에 각 문자와 할당된 프리픽스 값을 삽입

        String result = huffman.encoding(huffman.text, huffman.binaryCode);

        System.out.println("기존 텍스트 파일");
        System.out.println("==============");
        System.out.print(huffman.text);

        System.out.println("인코딩 후");
        System.out.println("==============");
        System.out.println(result);


        int origin = huffman.text.getBytes(StandardCharsets.UTF_8).length;

        System.out.println("기존 데이터 사이즈 : " + origin * 8 + "Bit");
        System.out.println("인코딩 데이터 사이즈 : " + result.length() + "Bit");
    }

    private File fileChoose(){  // 파일 적재 -->> 파일 리턴으로 바꿈
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        File file = new File("");
        int res = fileChooser.showOpenDialog(null);
        if(res == JFileChooser.APPROVE_OPTION){
            file = fileChooser.getSelectedFile();
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String read;
                while ((read = bufferedReader.readLine()) != null){
                    text += read + '\n';
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public String encoding(String text, HashMap<Character, String> binaryCode){ // 텍스트 인코딩
        int i = 0;
        String encodedText = new String();
        while (i < text.length()){
            encodedText += binaryCode.get(text.charAt(i));
            i++;
        }
        return encodedText;
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
    public HashMap<Character, Integer> countFrequency(File file){ // 빈도수 체크
        String line;
        HashMap<Character, Integer> frequency = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null){
                for (int i = 0; i < line.length(); i++){
                    char c = line.charAt(i);
                    if (frequency.containsKey(c)) {
                        frequency.put(c, frequency.get(c) + 1);
                    } else {
                        frequency.put(c, 1);
                    }
                }
                if(frequency.get('\n') == null) {
                    frequency.put('\n', 1);
                }else{
                    frequency.put('\n', frequency.get('\n') + 1);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return frequency;
    }
    public Node makeTree(HashMap<Character, Integer> freq){ // 허프만 트리 생성
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
        while (queue.size() > 1){
            Node left = queue.remove();
            Node right = queue.remove();
            Node parent = new Node('\0', left.frequency + right.frequency, left, right);
            queue.add(parent);
        }
        Node root = queue.remove();

        return root;
    }

    private void binaryEncode(Node n, String s) { // 문자들을 이진수로 변환
        if (n == null) {
            return;
        }

        binaryEncode(n.left, s + "0");
        binaryEncode(n.right, s + "1");

        if(n.character != '\0') {
            if(n.character == '\n'){
                System.out.println("\\n" + " : " + s);
            }else {
                System.out.println(n.character + " : " + s); // print
            }
            binaryCode.put(n.character, s);
        }
    }

}
