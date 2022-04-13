# 허프만 압축

고예진, 윤동관, 조준희

## 허프만 압축이란?
어느 파일 속의 각 문자들이 아스키코드(ASCII)로 저장되어 있다면, 그 파일은 문자 수 * 8 bit의 크기를 갖게 된다. 이 파일을 필요 시 크기를 압축하고 다시 복구할 수 있다면, 파일의 저장과 전송에 용이할 것이다. 트리를 이용해 각 문자의 빈도수에 따라 프리픽스 값을 할당해 파일을 압축하는 알고리즘을 허프만 압축 알고리즘이라고 한다.

## 알고리즘
```
입력 : 입력 파일의 n개의 문자에 대한 각각의 빈도수
출력 : 허프만 트리
각 문자에 대해 노드를 만들고, 그 문자의 빈도수를 노드에 저장한다.
n개의 노드들의 빈도수에 대해 우선순위 큐 Q를 만든다.
while(Q에 있는 노드 수 >= 2){
  빈도수가 가장 작은 2개의 노드(A와 B)를 Q에서 제거한다.
  새 노드 N을 만들고, A와 B를 N의 자식 노드로 만든다.
  N의 빈도수 ← A의 빈도수 + B의 빈도수
  노드 N의 Q에 삽입한다.
}   
```
위는 **알기 쉬운 알고리즘** 책에서 설명하고 있는 허프만 압축의 알고리즘이다.  
허프만 압축을 구현하기 위해서는 아래의 단계를 거치면된다.  
1. 컴퓨터에서 txt 파일 하나를 가져와 읽은 후 String 변수에 담는다.
2. 읽어온 문자열의 각 문자와 그 문자의 출현빈도 수를 저장한다.
3. 문자와 출현빈도를 가진 노드 하나를 만들고, 우선순위 큐에 저장한다.
4. 저장된 큐에서 빈도수가 가장 작은 2개의 노드를 제거한 후 그 2개의 노드를 자식으로 삼는 부모 노드를 하나 만든다.
5. 그 노드를 다시 큐에 넣고 4의 과정을 반복한다. 큐에 노드가 1개 남을 때 까지 반복하면, 최종적으로는 트리 구조를 가진 최상위 루트 노드 하나만 남게 된다.
6. 루트 노드를 기준으로 왼쪽 자식으로 내려가면 0, 오른쪽 자식으로 내려가며 1을 추가하여 각 문자에 프리픽스 값을 할당한다.
7. 할당된 프리픽스 값을 이용해 읽은 문자열을 변환(압축)한다.
---
### 코드 설명  
**자바로 구현한 허프만 압축 알고리즘의 메인 함수는 아래와 같다.**
```
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
    }
```
**먼저 내 컴퓨터에서 txt 파일을 하나 선택하고 그 파일을 읽는다. 그 후 전역변수로 선언된 text에 읽은 문자들을 삽입한다.**
```
private File fileChoose(){  
        JFileChooser fileChooser = new JFileChooser(); // JFileChooser로 파일을 선택한다.
        fileChooser.setMultiSelectionEnabled(false);
        File file = new File("");
        int res = fileChooser.showOpenDialog(null);
        if(res == JFileChooser.APPROVE_OPTION){
            file = fileChooser.getSelectedFile();
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file)); 
                String read;
                while ((read = bufferedReader.readLine()) != null){ // 선택된 파일을 줄 단위로 읽어 String text에 넣는다.
                    text += read + '\n';
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
```

**읽어온 문자열을 차례대로 읽으며 각 문자와 그 문자의 빈도수를 HashMap에 저장한다**
```
public HashMap<Character, Integer> countFrequency(File file){ // 빈도수 체크
        String line;
        HashMap<Character, Integer> frequency = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null){
                for (int i = 0; i < line.length(); i++){
                    char c = line.charAt(i);                // 문자열의 문자들을 차례대로 읽는다.
                    if (frequency.containsKey(c)) { 
                        frequency.put(c, frequency.get(c) + 1); // HashMap에 그 문자가 이미 저장되어 있다면 빈도수를 1 더한다.
                    } else {
                        frequency.put(c, 1);                    // HashMap에 그 문자가 저장되어 있지 않다면 그 문자와 빈도수 1을 추가한다.
                    }
                }
                if(frequency.get('\n') == null) {               // 문자열 중 줄바꿈이 있다면 줄바꿈 역시 출현 빈도수를 저장한다.
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
```

**문자와 빈도수가 저장되어 있는 HashMap을 이용해 노드를 만들고 우선 순위 큐에 삽입한다.**
```
public Node makeTree(HashMap<Character, Integer> freq){ // 허프만 트리 생성
        Iterator<Character> keys = freq.keySet().iterator();
        PriorityQueue<Node> queue = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {    // 가장 작은 빈도수를 제거하기 위한 우선 순위 큐
                return o1.frequency - o2.frequency;
            }
        });
        while (keys.hasNext()){
            char key = keys.next();
            Node node = new Node(key, freq.get(key), null, null);   // HashMap에서 문자와 그 문자의 빈도수를 가져와 노드를 만든다.
            queue.add(node);                                        // 만든 노드를 우선 순위 큐에 넣는다.
        }
        while (queue.size() > 1){
            Node left = queue.remove();                             // 만들어진 우선 순위 큐에서 가장 작은 두 개의 노드를 꺼내 그 노드를 자식으로 갖는 부모 노드를 하나 만든다.
            Node right = queue.remove();                            
            Node parent = new Node('\0', left.frequency + right.frequency, left, right);
            queue.add(parent);                                      // 그 노드를 다시 큐에 넣고 큐에 한 개의 노드만 남을 때 까지 이 과정을 반복한다.
        }
        Node root = queue.remove();                                 // 최종적으로 만들어진 루트 노드를 가져와 리턴한다.

        return root;
    }
```
