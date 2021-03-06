# 허프만 압축

팀원 : 고예진, 윤동관, 조준희  
  
  
**역할 분담**  
파일 적재 : 윤동관  
빈도수 체크 : 조준희, 윤동관  
허프만 트리 생성 및 인코딩 : 고예진, 윤동관

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
6. 루트 노드를 기준으로 왼쪽 자식으로 내려가면 0, 오른쪽 자식으로 내려가면 1을 추가하여 각 문자에 프리픽스 값을 할당한다.
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
```
**우선 노드 클래스는 위와 같다. 노드는 문자 값, 빈도수 값과 왼쪽 자식 노드, 오른쪽 자식 노드를 가진다.**
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
**루트 노드를 통해 각 문자의 노드에 방문하여 프리픽스 값을 할당한다.**
```
private void binaryEncode(Node n, String s) { // 문자들을 이진수로 변환
        if (n == null) {
            return;
        }

        binaryEncode(n.left, s + "0");        // 왼쪽 자식으로 이동하면 프리픽스에 0 추가
        binaryEncode(n.right, s + "1");       // 오른쪽 자식으로 이동하면 1 추가

        if(n.character != '\0') {            
            if(n.character == '\n'){
                System.out.println("\\n" + " : " + s);
            }else {
                System.out.println(n.character + " : " + s); // print
            }
            binaryCode.put(n.character, s);   // 전역변수로 선언된 HashMap에 문자와 할당된 프리픽스 값을 저장한다.
        }
    }
```
**저장된 프리픽스 값을 이용해 읽었던 문자열을 변환(압축)한다.**
```
public String encoding(String text, HashMap<Character, String> binaryCode){ // 텍스트 인코딩
        int i = 0;
        String encodedText = new String();
        while (i < text.length()){
            encodedText += binaryCode.get(text.charAt(i));
            i++;
        }
        return encodedText;
    }
```
**전체 코드**`
```
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

```

---
## 결과
<img src="https://github.com/YoonDongGwan/algorithm_team/blob/main/src/greedy/result1.png"/>  
<img src="https://github.com/YoonDongGwan/algorithm_team/blob/main/src/greedy/result2.png"/>  
<img src="https://github.com/YoonDongGwan/algorithm_team/blob/main/src/greedy/result3.png"/>



**최종적으로 Alice’s Adventures in Wonderland - CHAPTER I.txt 파일에 허프만 압축을 적용시킨 결과는 아래와 같다.**
<details markdown="1">
<summary>Alice’s Adventures in Wonderland - CHAPTER I.txt</summary>

CHAPTER I.
Down the Rabbit-Hole


Alice was beginning to get very tired of sitting by her sister on the
bank, and of having nothing to do: once or twice she had peeped into
the book her sister was reading, but it had no pictures or
conversations in it, “and what is the use of a book,” thought Alice
“without pictures or conversations?”

So she was considering in her own mind (as well as she could, for the
hot day made her feel very sleepy and stupid), whether the pleasure of
making a daisy-chain would be worth the trouble of getting up and
picking the daisies, when suddenly a White Rabbit with pink eyes ran
close by her.

There was nothing so _very_ remarkable in that; nor did Alice think it
so _very_ much out of the way to hear the Rabbit say to itself, “Oh
dear! Oh dear! I shall be late!” (when she thought it over afterwards,
it occurred to her that she ought to have wondered at this, but at the
time it all seemed quite natural); but when the Rabbit actually _took a
watch out of its waistcoat-pocket_, and looked at it, and then hurried
on, Alice started to her feet, for it flashed across her mind that she
had never before seen a rabbit with either a waistcoat-pocket, or a
watch to take out of it, and burning with curiosity, she ran across the
field after it, and fortunately was just in time to see it pop down a
large rabbit-hole under the hedge.

In another moment down went Alice after it, never once considering how
in the world she was to get out again.

The rabbit-hole went straight on like a tunnel for some way, and then
dipped suddenly down, so suddenly that Alice had not a moment to think
about stopping herself before she found herself falling down a very
deep well.

Either the well was very deep, or she fell very slowly, for she had
plenty of time as she went down to look about her and to wonder what
was going to happen next. First, she tried to look down and make out
what she was coming to, but it was too dark to see anything; then she
looked at the sides of the well, and noticed that they were filled with
cupboards and book-shelves; here and there she saw maps and pictures
hung upon pegs. She took down a jar from one of the shelves as she
passed; it was labelled “ORANGE MARMALADE”, but to her great
disappointment it was empty: she did not like to drop the jar for fear
of killing somebody underneath, so managed to put it into one of the
cupboards as she fell past it.

“Well!” thought Alice to herself, “after such a fall as this, I shall
think nothing of tumbling down stairs! How brave they’ll all think me
at home! Why, I wouldn’t say anything about it, even if I fell off the
top of the house!” (Which was very likely true.)

Down, down, down. Would the fall _never_ come to an end? “I wonder how
many miles I’ve fallen by this time?” she said aloud. “I must be
getting somewhere near the centre of the earth. Let me see: that would
be four thousand miles down, I think—” (for, you see, Alice had learnt
several things of this sort in her lessons in the schoolroom, and
though this was not a _very_ good opportunity for showing off her
knowledge, as there was no one to listen to her, still it was good
practice to say it over) “—yes, that’s about the right distance—but
then I wonder what Latitude or Longitude I’ve got to?” (Alice had no
idea what Latitude was, or Longitude either, but thought they were nice
grand words to say.)

Presently she began again. “I wonder if I shall fall right _through_
the earth! How funny it’ll seem to come out among the people that walk
with their heads downward! The Antipathies, I think—” (she was rather
glad there _was_ no one listening, this time, as it didn’t sound at all
the right word) “—but I shall have to ask them what the name of the
country is, you know. Please, Ma’am, is this New Zealand or Australia?”
(and she tried to curtsey as she spoke—fancy _curtseying_ as you’re
falling through the air! Do you think you could manage it?) “And what
an ignorant little girl she’ll think me for asking! No, it’ll never do
to ask: perhaps I shall see it written up somewhere.”

Down, down, down. There was nothing else to do, so Alice soon began
talking again. “Dinah’ll miss me very much to-night, I should think!”
(Dinah was the cat.) “I hope they’ll remember her saucer of milk at
tea-time. Dinah my dear! I wish you were down here with me! There are
no mice in the air, I’m afraid, but you might catch a bat, and that’s
very like a mouse, you know. But do cats eat bats, I wonder?” And here
Alice began to get rather sleepy, and went on saying to herself, in a
dreamy sort of way, “Do cats eat bats? Do cats eat bats?” and
sometimes, “Do bats eat cats?” for, you see, as she couldn’t answer
either question, it didn’t much matter which way she put it. She felt
that she was dozing off, and had just begun to dream that she was
walking hand in hand with Dinah, and saying to her very earnestly,
“Now, Dinah, tell me the truth: did you ever eat a bat?” when suddenly,
thump! thump! down she came upon a heap of sticks and dry leaves, and
the fall was over.

Alice was not a bit hurt, and she jumped up on to her feet in a moment:
she looked up, but it was all dark overhead; before her was another
long passage, and the White Rabbit was still in sight, hurrying down
it. There was not a moment to be lost: away went Alice like the wind,
and was just in time to hear it say, as it turned a corner, “Oh my ears
and whiskers, how late it’s getting!” She was close behind it when she
turned the corner, but the Rabbit was no longer to be seen: she found
herself in a long, low hall, which was lit up by a row of lamps hanging
from the roof.

There were doors all round the hall, but they were all locked; and when
Alice had been all the way down one side and up the other, trying every
door, she walked sadly down the middle, wondering how she was ever to
get out again.

Suddenly she came upon a little three-legged table, all made of solid
glass; there was nothing on it except a tiny golden key, and Alice’s
first thought was that it might belong to one of the doors of the hall;
but, alas! either the locks were too large, or the key was too small,
but at any rate it would not open any of them. However, on the second
time round, she came upon a low curtain she had not noticed before, and
behind it was a little door about fifteen inches high: she tried the
little golden key in the lock, and to her great delight it fitted!

Alice opened the door and found that it led into a small passage, not
much larger than a rat-hole: she knelt down and looked along the
passage into the loveliest garden you ever saw. How she longed to get
out of that dark hall, and wander about among those beds of bright
flowers and those cool fountains, but she could not even get her head
through the doorway; “and even if my head would go through,” thought
poor Alice, “it would be of very little use without my shoulders. Oh,
how I wish I could shut up like a telescope! I think I could, if I only
knew how to begin.” For, you see, so many out-of-the-way things had
happened lately, that Alice had begun to think that very few things
indeed were really impossible.

There seemed to be no use in waiting by the little door, so she went
back to the table, half hoping she might find another key on it, or at
any rate a book of rules for shutting people up like telescopes: this
time she found a little bottle on it, (“which certainly was not here
before,” said Alice,) and round the neck of the bottle was a paper
label, with the words “DRINK ME,” beautifully printed on it in large
letters.

It was all very well to say “Drink me,” but the wise little Alice was
not going to do _that_ in a hurry. “No, I’ll look first,” she said,
“and see whether it’s marked ‘_poison_’ or not”; for she had read
several nice little histories about children who had got burnt, and
eaten up by wild beasts and other unpleasant things, all because they
_would_ not remember the simple rules their friends had taught them:
such as, that a red-hot poker will burn you if you hold it too long;
and that if you cut your finger _very_ deeply with a knife, it usually
bleeds; and she had never forgotten that, if you drink much from a
bottle marked “poison,” it is almost certain to disagree with you,
sooner or later.

However, this bottle was _not_ marked “poison,” so Alice ventured to
taste it, and finding it very nice, (it had, in fact, a sort of mixed
flavour of cherry-tart, custard, pine-apple, roast turkey, toffee, and
hot buttered toast,) she very soon finished it off.

“What a curious feeling!” said Alice; “I must be shutting up like a
telescope.”

And so it was indeed: she was now only ten inches high, and her face
brightened up at the thought that she was now the right size for going
through the little door into that lovely garden. First, however, she
waited for a few minutes to see if she was going to shrink any further:
she felt a little nervous about this; “for it might end, you know,”
said Alice to herself, “in my going out altogether, like a candle. I
wonder what I should be like then?” And she tried to fancy what the
flame of a candle is like after the candle is blown out, for she could
not remember ever having seen such a thing.

After a while, finding that nothing more happened, she decided on going
into the garden at once; but, alas for poor Alice! when she got to the
door, she found she had forgotten the little golden key, and when she
went back to the table for it, she found she could not possibly reach
it: she could see it quite plainly through the glass, and she tried her
best to climb up one of the legs of the table, but it was too slippery;
and when she had tired herself out with trying, the poor little thing
sat down and cried.

“Come, there’s no use in crying like that!” said Alice to herself,
rather sharply; “I advise you to leave off this minute!” She generally
gave herself very good advice, (though she very seldom followed it),
and sometimes she scolded herself so severely as to bring tears into
her eyes; and once she remembered trying to box her own ears for having
cheated herself in a game of croquet she was playing against herself,
for this curious child was very fond of pretending to be two people.
“But it’s no use now,” thought poor Alice, “to pretend to be two
people! Why, there’s hardly enough of me left to make _one_ respectable
person!”

Soon her eye fell on a little glass box that was lying under the table:
she opened it, and found in it a very small cake, on which the words
“EAT ME” were beautifully marked in currants. “Well, I’ll eat it,” said
Alice, “and if it makes me grow larger, I can reach the key; and if it
makes me grow smaller, I can creep under the door; so either way I’ll
get into the garden, and I don’t care which happens!”

She ate a little bit, and said anxiously to herself, “Which way? Which
way?”, holding her hand on the top of her head to feel which way it was
growing, and she was quite surprised to find that she remained the same
size: to be sure, this generally happens when one eats cake, but Alice
had got so much into the way of expecting nothing but out-of-the-way
things to happen, that it seemed quite dull and stupid for life to go
on in the common way.

So she set to work, and very soon finished off the cake.

</details>

<details markdown="1">
<summary>프리픽스 테이블</summary>

u : 00000  
p : 000010  
D : 000011000  
z : 00001100100  
‘ : 0000110010100  
G : 0000110010101  
Z : 0000110010110  
K : 0000110010111  
S : 0000110011  
: : 000011010  
C : 000011011000  
B : 000011011001  
O : 00001101101  
R : 0000110111  
v : 0000111  
i : 0001  
e : 001  
n : 0100  
h : 0101  
b : 011000  
m : 011001  
” : 01101000  
“ : 01101001  
I : 01101010  
A : 01101011  
y : 011011  
a : 0111  
o : 1000  
c : 100100  
&#45; : 100101000  
T : 1001010010  
W : 1001010011  
; : 100101010  
( : 1001010110  
) : 1001010111  
. : 10010110  
M : 10010111000  
j : 10010111001  
— : 10010111010  
L : 10010111011  
q : 10010111100  
N : 10010111101  
F : 100101111100  
P : 100101111101  
x : 10010111111  
, : 100110  
f : 100111  
d : 10100  
g : 101010  
\n : 101011  
l : 10110  
r : 10111  
t : 1100  
k : 1101000  
! : 110100100  
’ : 110100101  
_ : 110100110  
? : 1101001110  
E : 11010011110  
H : 11010011111  
w : 110101  
s : 11011  
&nbsp; : 111

</details>

<details markdown="1">
<summary>인코딩 후</summary>

000011011000110100111110110101110010111110110010100101101001111000001101111110110101010010110101011000011000100011010101001111100010100111100001101110111011000011000000111001001010001101001111110001011000110101110101110101101101011101100001100100001111110101011111011111011000001101010000101000100000101001010101111100100011110101000111001110000111001101110110111111100000110111001101001111000100111111110110001110011000001010010101011101100001101111101010011011111111011000111011110000110111111100001001111100010100110101101100001110100110100010011011101110100101001111000100111111010101110000111000101001010101110100100011000101000101001010101111100100011110100100000001101011110000100100100001111100010111111110011010100011001000011111101101010011110101011110100111000010001001000010001101001110001010011001000101011110001010011110110001000100011010001110101001101111111101100011101111000011011111111010101111101111110111001011110100000101001010101001101110110000000011001110001110011101010111101001110100100011100001000011001001100000001011100111011111100010111101011100100100001000000111001101111101101111100000110000100110111110001010011100011100100110111011010010111010010100111110101010101111100111000111011111110001010011110000011011001111100010011111101111110110001000100011010001001100110100011111000101100000000101010010111001110110101110110000110010000110101101101001110101000111000101100000000110011100001000011001001100000001011100111011111100010111111100100100001000000111001101111101101111100000110000100110111101001110011010001010111010110000110011100011111011010100111111010101111101111110010010000100110110001101000011011100010100101010111000101001110101001101111111000110101010011101100100010100101001111001010110011111011111110101001101101011011101111101111111011010100111110010010000000010110101001001101111001111000101111111100010100110101101011000110011110100011101101111101100101111010000111101010011011111110011100100110110111000011100110111011011111110111011000100100001001101111101110100101001111101111000000000001000011010010010101111001101111101010101001110001010011011111111000101001111000010101100010111110110000010111001111100010011110101101100101111101000000101001010101110111111101000111000111011011011100101000100100010101110001010011111010110000000010110101001110110000011111101011000101111100010111111000101001111110010111100000000011000101100011111000100111111101010001110011000001010010101011100000000010111011101001010010101100001000011001001101000000101001010101111100010100111110100011100011101100010011101110011011111010101010010100111110110000010100101000010100101100110111110111111100101001101010001110000111100001101110111011000011000000111001111101010001110001011110000100001010011010001110010110110011101111110111011101001010111001001011010001101100111101100001101111101010011011110010110101011101011100101001001010011011100111111010101111101111101001000110001010001010010101011111011100011111010011000001110011011101101111010011011110111001011001011110111110100001110110001011000111100010100111110001010111110010010101011101001000101111111010000011010011101101011101100001100100001111110001010001010011010001110001110010101111011100011111010011000001110011011101101111010011011101100100000100100010111110000000011001111000100111111110001010011111101010111011011111110010001110101001011110111111110001010011110000110111011101100001100000011100111110110111011011111110010001110001110011011001101101001111001101110110100100001101101010110101110100001011110111110100100111000011011010101111101000010111101111101001001110110101011111011010101111011010110111011000001111101100111110000111010010001101000111100101011011010101010010100111110110101001111110001011000000001010100101110011100011100111100000001110011011111101111001111100001101111101010111101111010011011100110101011000111001111000100100100100000001011110111001101001111100100011101010011011111111000101011111001111101101010011111000000001010100101110011111001000111010101110000111001111110101100001001010000110111001101001110111110011111000101000111011100110111011000000001100111011111001111100010100110101111000001011001001111000111001110111101101011011111011001001011001001101001111001011110000000000111000011110100011111000000010111011110110100101011110010101011101100000000110011111010101010010100111110001010011110000110111011101100001100000011100111011110010011000000001111011010110011011111110100110110010001000110100011101111010111101010111110010010001011111000000001100111100010011111100011100110111111101010111000111011110010010010000111110010010100000001010001001001101000001110011010011010011011101110100101001111011010001000110100000110100111011111001110001110010011011101110100101001111100010100101001110101000001011110111000100110100101011100001001001101110110101110110000110010000111111011110001111011111000011010011111001000111010100110111111100111001001110010011011110011110001011111100011100111100111101100111110110101001101001110111100100101111000110111101111101010011011111101100100010100101001111100010101111100111110110101001101011010101111010011101000010000111001101111110110000011001111000101110011111101100100101001110111111101110111011000011000000111001111101010001110001011110010001110001010011011111101111111101010111000111011110010010010000111110010010100000001010001001001101000001110010011011110001011111101111010111101010111110010010001011111100100011111000111110100000111110000000011001111000100111111000111001001101110111010010100111011000000001011101000001010010101011111010100011100010111110010000000101110001100011011000111000110111001101111101101010011111011101110100111011110010010111100011011110111111100010100110101110011100010011011010100111011110011111000011011111100011100100110111011101001010011110011110001011111000000001000111110000110110011011111110101011111011111100101110010000011011110011100010100111110000010110010011111100100011111011001001111000111001110000101000000010111101001000110101010011101111010111011001111011110101000111110111011101100001100000011100100101000010110001011000111100000010010100001101111111100010100111101010011010010101000110010110101011101011011010100100111011101001000110001010011011111101100110000110010010100110011110100100011010101001111101010010100110011101101011101100001100100001111011110011111000011011111100011100100110111010000100001110011011111110000100100100001111100100100001001101100011010000110111000101001010101110101100011010110101100010100111110001010011111101011000101111011010100111110110101001111110101011111011111110010001111010100011100111100000000110011101111010100111000101001001011010101110101110010100100101001111101110111011000011000000111001001010000101100010110001111110101001010011001111101111001011101110001101010010111001111000010011110110000111010000011110111111110000000010001000011011011110011110001011111111011100001100100111111010101110110111001101110111010010100111110001010010100101011101000001000010000010001101001111101100000101001010000101001011001101111110100100011010101001001101111101110001111101100000101001010000101001011001101111111000101011111001110110101110110000110010000111101010111101001110100100011001110111111011001100001100100101001100111110010001111100010100010100110100010101101110110001000000001100111110111100100000001000001000010100101010111010100110111110110011011010011111101100000110011110001011100111111011010100111110011110000000001001010011101010011011111011001101101001111111001110111101101011000010100101010111101001000110101010011101111110000111001101110110111010111010000100100001011111010100110110101101001011010101110101111010011110000111000101001101111111100010100111111010100110110101101111101010111110111110000111001101110110111111010000100100001010011011110001011111111011010100111110011100110110101101110000111001101110110111111101110110100011010110110011011100110111100111100010111111110110101001111010101111010010101100001010110001010011000110111111000100111111110000010110010011110111110111111101101010011111101010010100110011110100100011010101001111100100011110110100010001101000111011101100010000000011001110101001101111110111010010100111110010001111101011000010010100001101111111101010101011111001010111101010111110111111010101000000101001010101111100100011101010111000010000010001010011101000011001011111111001001011011110010111110000011011111011110010011011111011010100111111001011100010011010011111001000111101101000100011010001111010010001101010100111011101001010011101100101111101000001111100000000110010101111010101010111110011111011010100111111010101111101111110010010000110010001010010101011111001000100110111011000000001100111000111001111101010111110111111100100010001111010001111011111010001111100100011111011001001111011101000110111100010100010100101010100101010111110001010010100111110110101001101011101101000100011010000011010011101111100111110001010011111101100011010000111011111100010011111111000101001111110101001101101011010011011101110100101001110100100011000001100100001101001111100010101111100111110001010010110111111101010011011100111110011100011011010110001101001111101010001110001011010111001000000000001001100010000111101111010011011111011101001010011101100010001000110100010010100011011010100110110000011100111011100101010111010100110111001111011101001010011111000101001101110011111101101010011111101101111101011110110010111000010110111110111010010100111000010000110010011000000010111001110111010110101000000100101010111000000000101000010011100001000110101011011100101101110000110011010100111111001000100011010001111010010001101010100111011111110010111001011110111111100111101111000011001111100001000011111000100111111110001010011111101101010011011000001110011101111101111101111111011010100110101100001001111101111011001101001001010101110001110011111010101111101111110110011101100000110110101100011010011101101001000011011010000110111011010111001011110100001100101011101001111011110010111000011010110000110111100101110000110101110010111011011010110000110001101001111001101000100110111011000000001100111110010001110101001101111111010101011100101111100101011101000001110110111000010000010100000010100110001100100101001100111000111001111101010111110111110010110010000101100011011000011010111110110101001111101000001101001110100100011001111011000011101000001111110010001111010010111100000001011111000101001111100101110010111101111111001111000101111111001110010111101111010111000100111111110100000011011010110000101001010101111101110000110010010110001000101000110111110000001001010000110111010000101111100010110011011111011100011101100101110100011110101000110100111110010001110000100000011001110001110011100010100110010001111000010000111110001001111111100010100110101110010000000000010011000100001111011110100110111110111110111111101101010011111001110011011010110111000010011111011110011100011100100101101010111010110110100110010100110011011010110110100100011010001111100010110000000010101001011100111011010111011000011001000011111100100011101010011011111011001101101001111001101110110100101111001111100001101111111101100000100100010111101111111001110111101101011011101111101111111000101000111011100110111011010101111101101010111101101011010101111000101000101001101000111010010001100010100010100101010111100010011111111000000001100101100010110000101001010101111010010001101010100111110111100011100011011111011110100100111110100111111000110101111011000101110111000011100111111000101001011011110100101101101011011101111011010110111110001010001010011010001110110010011010110111110011101011000011001001110100100111100101001101010110111001101110110101011111010110000000010110101000100110100101110011111011011101101111101110100011011110001010001010010101011101110110001000000001100111000111001001101110010000111001010011100011001111110110101011110011100110110101101111000100111100111111110001010011010111100100000001011110001001111111100010100111101011000000001101100111010010001101000111100101011010010100110101000110010001011111101010111110111110000111001101110110111111011000011101000001101100110111111100101110000000110010110100101011110101110101100001100010001101010100100110111101001000110101010010011011110100100011010101001001011011110010100111000000001011010100111110001010011111001110111101101011011111010011001000010000111001101111101001101111001001000011001001111110010001110111010011100101001010011010011101110110100101101010111110101100001001010000110111111010110001101011010110110010111010001101111101100100011011000111011111011010101101001010000111001111100111011110110101100010100111011000011011111110001010001110111111100000101100100111010011100110100011111011010100111111011011100011010011101111011010000000010100100101101110110100101101010111011001000001101111001110110000011010111010100011100110000010100101010111110111000011001001110101010100110111001111010000101111011111111000101001111100100001010011001011100111110001001111111100010100111100101111011111000101100101101111001011101100111001110110010011111101100100100001101011111000101011111001111101011000000001011010100101011011000001111100111100000000101111111100010110000000011011011101001010011101100100011011000111011111101001000110101010010011011101101010111110001010001010011010001001011101001101000111100101011010011110001011110011011101101110000000011111011001001100110111011010111011000011001000011110101011110100111101100010111101110100110010101111011001000011100110111011110110111110001010001010010101011011111100010011111111000101000111011111110111000101111100111000101001110101001101111111011000111011110111000010011011111000101001111100010100111111011100100010110001000101101011110001000011001100110111011101001010010101111000101100000000101010010111111000101000111011111110101011111011111010010001100111011111111010011000001110011011101101111010011011110101010001000101001111000000010000010100010111110000000010000011100011011111100111100010111111110110101100011010100010100101010111100010011110011111101010011011110101111010000100100011010110110001101001010100011001101110111110111111100010100110111001111110101011111011111010010001111000010000111111001000111101100001110111100001010011111001000111010100110111100110111110111100000110110101101110001110011111010101111101111110101010001000101001010110000101011101111001001100000110010000111111001000111110110111011011111000111001111000000011100110111100101011111101101001100101110100110110011101110011011111000101011111001101001011101111101110110001000000001100111110001010011111011100011010100101110011110100000111011110001110100100100001100101110100110000000011001010111100010100101001110110101011111010110000100101000011011111111010101010111110011110010111011011111000001110000000101000011111000101111111001011101110000100101010000111000000010100001111011010101101001010000111001111101010100011001111100100011010011100110100011110010101100110101110110000110010000111101010111101001110100100010101100011010000101111111101010101011111001111001011101101111100000111000000010100001111110101011111011100110111100010111111100101110111000010010101000011100000001010000111100100011100010100110111100110111011000000001100111110001011000000001010100101110011111000101001011011111110101001101110011110100000110010000110101110101010111011101001010011111010110001011110100110111111100100011111011011101101110010110100101011110101110101110010111110110111001110110010100110010110011011111110110101001111011000001101010011101001110111101010011100010100100101101110110100101101010111110101100001001010000110111111000110011111101101010111110110101011110110101101111001110111101101011011110111000110101001011100111110100110110001011011110000000010101001011101001101010111100010100111100101111011111000101110100100111110100111111000110101111100111000000100010001101111100011100110100101101101011011111011001001011001111110010001111001001000011001001111100000000110011101110110011000010010101011111000101001111000010001100000001010110001111110001010111110011111010101111011011010001010111101010001110001011111100010100100011011111101010010111101001101111110100100011010101001101010111101111010011010010011110010100100101001111011010110100110000010000100111110001010001001110111001101110110101011111000101000101001101000100101110100110100011110010101101101101010011111101010111110111111011101111100010100110111101011101010101100111101001111100010100110111001111110100110110101011111011110100110111010010001111000010000111110110000111011110000101000001010010101010011011111000101000111011111110000010110010011001101110111110111110001110011110100000110100010011010010111001111101110000000001001010011101111100111011110110101101010111100010100111110111000110101001011100111110101100010111101001001010111111011010011001011101001100000000110011101101010111110110101011110110101101110101011100001110011111100100011101111101111010001111100010100101100111111010101010111110011111000101001111010001110110010011111000100111111110001010011010111001001000000000100110010111011011111000111011100110111011011100000000111110100001001000110101100101101111001011111011011000101111101100110011011110010111000011111010010101110110011001101110001110111111100010100011101111110010111101001110101111000011001011000101111011001110100101001111000101111110110101100000110111100101110111101100001011111010011100110100010101110010101100111010010100111110110101001111110010111000100110100111110010001111001000000010111110011011001011011111011111011111110110101001111110110000101000110100000110010111010100111011101001001000110111111101001101001000000010111110011011001011011000101001010101101001101110111110111110110111000000001101001011011100110101110011101111011010110000101001010101111100010110111100000000101010010111111000101001111011100011011111010010011100001100010001110110111000000001111100010100010100110100011101101110000000011110010010000000010110101001110110010111010001111010100011110001110011010011101001010111111011010010110101101001010011111010101010111110010101101110100111000110101001001000101110111010011001111011000011100110010110001111101010000110111101101111101101010011101001011011010110111110001010001010011010001110110010011111001111000101111110111110111101000000101001010101101001001111001011110110001001101110001110011010010110110101101110100001000011100110111111101001000101011110010001110111110111101000000011010111000010001101110101011100001011011111011010101111101101010111101101011011111011001001111000111001111101011011100011100110000101001110000000001011111011100001100100111010101010011011100110010110011010001010111010110000110001000110101010010011011110100100011010101001001101111010010001101010100100101101111001010010010100110111001111110101011111011111010010001100010100010100101010111001101101101100111111001000111101001000100110111110111000111011010111011000011001000011111101110001000010011101100000110101001110100101011110001111011011010000001010010101011101111010100111000101001001011011101101001000011000000101000111010111010010110110101101110110010001110111101111101100100111100001110011011101101111101100100000100100010111111001000100101000010000011010100101110010011011101101010111110110101100000000101101010011111000101000101001101000110100100011010001010111001010110000011000000101000111010111111010101111101111111000101001111100100011111001001011010010101111110110100101101010111010110000000100011111100010100101101111010010110110101101111011100101100100101100101100000110111111010100110111111110110111000001001000011011111110001001111110110010001101101101000111011111001010111100001011110010100011000001011001001100101101110000110000001010001110101111011001011011111101000010111101111101001001110110101011111010100011101101011110110111000000001111101010011011100111110100100011010101001110101001101110011111101010001110001011110110010011101001001111001010010010100110111001111011110111001101011010010001110110010001100100001111000101001111100010100111101110001101111001101110110101011010010101100111101111001111011101110001101001001101110110000000011001110110111000000001110110010001101010010111001111001000111110010010001011110111111011000011111001001101110111010010100111110001010111110011010010111011101011000011100110111011011111101100001110100000111101111110110011000000001101100110011011101101110000000011111010000100100011010110010110111000011011001000001100111101001000111100100011111001101111100101111100111011000011111001101110011011101101010111110101100001001010000110111110100111001101000111011010110100101001110101001101110011010110110101110110000110010000111101100000110101001110100111110010001111010100011100111101110111110001010011011111111011101100010010000100110111001101110111010010100111110101001010011001111000010011111011011101101100010100101010111110010001110101001101111101100110110100111100110111000101001110111101011101001011100101110110010110111111101110001011111001111000100111111110101011101101110011011101101001000011000100011110010001111100110111110010111110011101100001111100110111101001110111000011000100011110010001111100110111110010111110011101100001111100110111101001110011010001110111010010100101011110111000011001001110000010110010011101110011011101101001000011000100011101100001111100110111110010111110011110010001111100110111101001110011010001111001111000101111001101110110111000000001111101100100110011011101111101111111011010100111110010010000000010110101000100110100101110011101110100110111101010011011110101100100011100010100110111111100101111000000000111011110000011000010010011011100011100111101000001101000100110100101110011101100100000100100010111101100101111100110000110111111110101010100011001000101111110101011101101111111011010100111100001000000110011100011100100101101110000110011010100111110011100110110110010101111000101011111001111101101010011111101010111110111111010010000000110010000010100101010111100010011110011110011011101110100101001110101011110100111100101110010000011011110011101100000110101000000010011111001000111101001011100101110110011111100010101111100111110110101001111110101011111011101011110101011110110110100000010100101010111010101110100101001110001010011101010111010010100111110101000111000101111000011000000101000111010110011011101110100101001111101101110110110001010010101011111001000111010100110111111000011100110111011011111001011110111010000111011110010110011011100110101011011010011001011110110001101011001101110000110000001010001110101100110111110000110110101101110110010011111100010100111111001011100000110001010000110101111010000011010011101101110000000011100100001110011011111100101111100111011111101100001111100110100111001101000111110101010100101001111101100000101001010000101001011001101110011010101111000101000000110010000101101001001111100010100000011001000010110100100111101001000110101010011111011010100111110010001110110010011110000000001010000100111011111101010010111000010111100010011111111011110000011001001101000110111110111010010100111101001011101101111110110001011100001110011101110011011101110100101001010111100010100111110011101111011010110111110101011111011111100000001110011011110010110101011101011011010111011000011001000011111101010111110111110100100011001110111111011000000111001110101000001011111001001101110111010010100111110110101001111100101110010000001100100001000110100111000000000101111000010011111001000111010100110111111100111001001110011100010100111011111101100110000110010010100110000001101010101111011010100111110110100010001101000001101001110000000001010011011101100000000110011100011100111110101011111011111011110110101101111010001111011111010001111000000011100110111010100101111010010010101011101100000110011110001011100111101010011011111111010101111101111101110100100011000101001101111010111011010000100101010111000010011111011110110111101010001100110111011101001010011111000101001111100101001101010001110000111100001101110111011000011000000111001111101010111110111111101111000001101101011011100010100111110110001101010010111001001101110101000001011110111011011000101001010101111010010001101010100101011000111001001011011110010100100101001101110011111101010111110111110100100011001110111111011001100001100100101001100111110010001110110000011111011010001101111000000110101110111110101011101101111111010100101001100111011010111011000011001000011111011000011101000001111110001010011111101010001010010100100110101011011101001010011111010101111101111110010111001000001101111001110001010011111000001011001001111110010001110101001011110111111000111001111101101110110111001101110111110111110001110011111000000010111010000110100111011111110010010001011101000011011110011011101101001000011011010101111011001011011111001011110111110111010110111010010100111110101010100011101111010000011011111011100110111010110001101011111011001111100001111000111001101001011101111110101000111001100000101001010101101001000110100011100001100110101001111110101011111011111100100101101000110110011110110000010101000101001010011100011100111110101010100101001111101101010011010111100000001011101000011010011111000101001111100100100010111010000110111100110111011000000001100111110001010011110000110111011101100001100000011100111110101011111011111010010001111011010000100101010001101111111100100011101100000111111011001001010000001101011111011010100111110011110000000001001010010101101010011011111011001101101001111110001010011101111111011010000100101010100110111101101000110101111010101111011010110100110111110101010100011001000101111110101011111011111101100001110011100000000010111011000011011111011111110111100011010111110001001111111011001110110010000101101111101010111010010101000010100101010101011100111101111000011001111110001010011111011110001000100111100101101010111010111001010010010100110111001111110101001101110011111010010001000101111101111101111011010110111101111000000000100101001111100010100111101010111101101011010011011101100000000110011111000101001011011111110101001101110011110111101101011011110110100010010011010000011010010010101011101110100101001111101010101001010010101101101011101100001100100001111010101111010011101100000100101001110111101101011011111000101001111110101011101101111110100100011010101001111000010000111111011000110100001111011101001010011100000000010111110001010011111000110001010011011110011011111001011101101100010100101010111001000011100110111011011101011101001000100010111100110111110110101001111110101011110110110100000110100111110110111101001011001101111110100100011010101001111100010100111101100100011010010100101100011001101111101011000010010100001101110001010010101011101011000110101111110110101001111110101011111011111001000011100110111111110010001010111010100011100111100000000110011101111010100111000101001001011010101110101100001100110000010100101000010100101100110111111101101010011111001000111011001001111000000000101000010011101111111011000011100110010110001111110001011011100100110010100010110001101010101010001101001111100011101100010110001100110111011110110101101110110010111101000011111000100111111110111000101100001101001010111010101011001111101111011100101010111110001010011011100111111010101111101111101001000110001010001010010101011110000100111000111001110011001011111110010000100001011001110111111110000010100011011111101010100010110101000010100111110100000101101110011011101110100101001110110101110110000110010000111010010111011101011100111000110111110111100111110001011000000001010100101110011111010101111101111111000101011111001110001110011101100100011010100101110011101100000110110100001001010101111100100011110000100001111100010011111111000101001111101001000100010111110111111000100111111110001010011110101011110110101101001010101010110110000000011001001101110111101100111110111101001001110010001110001010011011111111000101001111101101000100100110100011011111110101001101110011111100100010001111011001111011110101000110011011110001011111111000101001111110100000101101111111010101111101111111001000100011111011011001011110110101101001101010110110000000011001110111110011101110100011011111101110111110000111100011100111110101100000000101101010011101001000110011110000000100010100111011101000110111111000100111111110001010010110011001011011111010011111100011010100100001110011011110011011110000100111110001010011111101100110010010000100101001010111100000101100100111110111100000000010010100100110111110110101001111100100011101100100111100000000010100001001110111111101101000110101111100100000001011111000111000101001111101101010011110101011110100111010010001100111010010001100000110010000110100111011000001100111100010111001100110111011101001010010101101100000101010001010010100111000111001111101010111110111110111111101100001110011001011000111110100100010001011111101110110001000000001100111100111000110011111000010010100111000101001001000101001110111110101000110101001010000110101111101101010011111100101110001001101001111100010100110101110110000111001100101100011111010101000101101010000101001111101000001011011111000101001111100010100111110110100010010011010001001101110111010010100111110010001110101001101111111010101011100101111100111101000011011000011010100101110011100011100111100111000111001100001101001101001001010111010110110101110110000110010000111110000000100010100001101001111100010100111110100100010001011111101110100101001111001111000000000100101001111100010101111100111000111001111011000110100111000101001100100011101111111101101100101111011010110111000010011111011110110111101010001100110111010010001100101011011001000001001000101111101100111101111010100011011111111000101011101001110111111101110111110010010100001011000101100010000110101111101101010011111101000010000110110110011110100100011010101001110111010010100111101101000100011010000011010011101111011010000100101010111110001010011010110000100111110111101101111010100011110001010011001000111110001010011111011010000000111001101100001001110111100111101010011110111101000010100111011011100000000111001000011100110111111110110111110101100101101111101001111110001101011111101101010011111011010000100101010001101001111100100011110101000111001010111000000001100111100010011111111000101011111001111010001111011111010001110101011110110101101001101110111010010100111110101011101001010000110111111011101100010000000011001110111011001100001001010101111100010110001101100111101100000110100110111111000100111111011000101110001101010010111001010111001111011010001101010011011111011111011101001010011111000101100011011001111100100100010001011011110011110000000001001100011100010100110111001101110110000000011001111101101010011111001001000000001011010100111010010001100111001000011100101001111010100011100111010100110111111010100101111010010101111000101101111000000001010100101111110001010011111010010001000101111101010111011011100101010111011010010111010010100111001000011100101001110001100111111011001011011111010100101111010011111010110000000010110101001111010101000111110001011011110000000010101001011001100110100011111000101100000000101010010111001010110000101000100010111111011010111011000011001000011001101110110100100011100111110101100000000101101010011101100000111110001001111110000111001101110110111111011000011100110010110001111000001101100111111010100011100010110000000011001110110010110111111101101011000000001011010100001101111101110010110111000011011010101100110101011010110001101011110110101011111010100011101101011110110101011110010010000000010110101001111101101010000011001110000000001011110110000111010000011110111111110000110110001110111001001000000010001110100100111011010101111100010100010100110100011101101010111100100100000000101101010010011011100011001111110110101011110000100101100110111010111101000010000111010111101011000110101111110010001110110000011010100001010010010110011010001111001011111001000101111001101110110111000000001111101100100110011011111011100011101100101110100011011111100000000110010010100010001001111001010001100010100110010100011010101110110111111100010100010100101010110111110101011110100101011010101110000100000100010100001101001111011001111100001101100110111001101111100010101111100111011010111011000011001000011110101011110100111011000001101010000000100111110010001111100010100010100110100011111000101011111001110000111001101110110111111001110011101011111100010100010100101010110111010110001010010100001001101001111101010011011100111110111001011110110101100110111110001011001000010100011011110110001011000101100011001011010101110101110010100100101001101110011111101100100101100100110100111110010001110110000011110100100011100000110110011110001010011111010101110001110000010100101010111011000011011111110001010011111011000011100110010110001111101001000100010111100110111110111000111110110101001111110101001010011001010110110000111100100110100011111001000111110001010011111100011101100010110001100110111010101111011010011111101011000000010000101001010101111101101010011110110010001101010010111001111001110001010010100111011101001000110001010011011111111010000010110111111000010011100011100100110111100010111111011111001010110111010001101111110111011111000011110111111011000100010001101000111100010011111110111000001011000111011111100111100010111111110110101000001100110000010100101010111000010001100000001010110001111000000000101111011000011101000001111110000110110001110111001001000000010001110110000110101111100010100011101110101111000001011001001111110110101001111100111100000000010010100111011111110110000111001100101100011110110001000110011001011000111110000100111000111001001101111001010110011010011101010101000110010001011111001000011011111000111000101001011001101111111010101111101111101001000110011101010011011100110101101100000110011110001011100110011001101000111110110111000110100111011010111011000011001000011001101001010111111011101001010011110111100000000010010100111110001010011110100001100100110100011110001001111111100010100111101100010001100110010110001111110101011111011111011111100001001110000100011011110101110110011101100000110110100110111110101000111000101111110001010011111101011000101111010011011111011010010000110000000110111011010101001011110100001100101111111001011100011010011110100110011010001110110000010111000001100000110011100000101101011001101111100001010111000101001100001101001111000010011100011100111000101001111011001111011110101000110101110110001110011000011011111011100101101010111010110110101011001111101010111110111110111101101011011100001110011011101101111111010100110110101101111100100011111011011101101111101101001000011000101110001010011010001110110010011001100110100011101100000000110011111000101001111110101000111011001111101100001110011001011000111101101011101100001100100001111110101011111011101011010010001100111101010100000010100101010111110010001111010010001111101001101100010101111100110100110111000101001110111111010100000101111011101101110010110111011010011001011110110001001101110110101011010010110110101101111011010001000110100011110011100011011111011110010011001101000111110110101001111110110111000110100100110101011011010010111010010100111110110010011111101010101001110001010011011111100011100110100101110111110110010111101111101000001101001110000110010100110100110000010100000011101110000100110100110110100101111100010111111010010001100011010001001010101111001111000101111111101101010011110101011110100111101110010111101001010111101100100001110011011101111011011101000001100100001111101100001110011001011000111101010001110111100100010111000100111011111011101100010000000011001111001000101000110110101001011100101001111101010101100011101010111101001111010101000110011101100000000101110100110010011011101110100101001010110010111110000101001110000000001011101100001101111111010100011011010100111011000001011111011110011011111011101001010011110001100010100110111111000000100000010101100010111110110111010011001111100010100010100101010110111001101110111101101011011101100000110010001110000011011001111110001010010110111010111101001101101011000000001011010100110100110111010010001100111101110010110010010110010110000011011111111000101001111110110001011001000010101100011111011100000101100011101111111000101001000110111111100111101110001001010010100110111110101011110100111110001110000010101001011100111110001010010110010000110101010111101100000100100010111101111101110011011111000101011111001110111111101110011010010010100001011000110011100001010001101000001101111111101010001101101011011101100000000101110100111011011100000000111000110011111101101110000000011101011000101101010011100011100111110010001000111101101000010010101010010101010101101110100101001111100010101111100111000110011111101101110000000011110010000000110011101101110000000010111111100111000101001010100011011111111010011000001110011011101101111010011011110100001001000010101100110111111101010001110001011110111111110100001000001100111001100110111000111001110000011011000000111101101011001101110101101100010110001001101001101110010101011101110100101001111101101010011110101011110100111010000100001110011011111110011110001011110101010001100110000101001111100010101111100100110111000110011111101101110000000011110100101110001010011010001110110010000010010001011111001111011110000110011110111101011011000100011001100101100011110110010111101111101000001101001110110100100001010000001110111000010010011001101000111000111001110001110111110111101100110011000110111100111100100001101111100011100010100111110010001111010000011101101111010101011100100111111010100011100010111101101110000000010011010101111011100010000100001101111111000101111111011001111100001101111001011010101110101111010011111100011010100100001110011011110011011111000101000111011111011000100011001100101100011111101010111110111111101001100100100011001101001101110110010111101111101000001101001110110100100001010000001110111000010010011001101000111110111000111011010111011000011001000011110000111001010011000000010111001101001111100100010101111000111110111100001111000111001001101110111010010100111100111000101001010000010100101010111000111001110000111001101110110111110100000110010000110011011110010101100001110011101010111101001001101110001010011110011101111001001100100110111011111111011100010111110011110001001111110110010001100101111110011010010101110011110110011100001111000000001011111110001001111111001000101001101111011101101110010100011000111101111100100110111100100000001101111000111101111010010011011100001000010100001100101000011100001000001010110001100110111101111000011111011110011111000000010111110100000101101110011011111001000100111100111001001100110111011101001010010101101011000110011101100000000110011000011011100110100111110010000111110111100100110100101011111111011010100111100001110011011101101111111011100010000100111100111000101000001110110101001101001110001110011110001001111001111001011010101110101101101001100101001101010111110011101111111001000000010111000110000000011011111100111001001101100001010010101011010010001101000111110110111000110100111011010111011000011001000011001010101110110100101101010111011001000001101111001110110000011111101101010000011001100000101001010101110000000001011110110000111010000011110111101011110000110110001110111001001000000010001100101100110100010101110101101101011010010100111110111000111000111001111101010111110111110001010010100001001101000000110101111101101010011111101010111110111110100100011010111110000100101100110111111100001010011100010100100100010100111011111010100011010100101100110111011101001010011101010011011111110011101111001000011010110110001011100011010100101110000101000011010011100000000010111011111001111100010100111111000101100000000101010010111001111100010101111100111110110101001111110101011111011111010010001101011111100010100111110111000110101001011100111110110001000011001000011111001111000101111111010101000000101001010101010111100010110111100000000101010010111111000101001111101100001110011001011000111110100100010001011111100010100110010001111100010101111100111101101000000011100110110011011111101010011110111101000010100100101101111001011111000001101111101111001001101110101100011010100100001110011011110011011111011010100110101111010101110001110000110100111100111100010111111011111110011100111010111101100100010100000001100001110111111100100011111011001001111000110011111111011010100111111010101111101111110101010000001010010101011111001000111110110101101110001010011010001110111010001101111110011100000101111100010100110111000011010101011110110101001111100111001101101100111011111110110000111001100101100011110100001101110000111100000000110111110111011000100000000110011111000101000111011100101010111011010011001111000101111110001110011101100100011010100101110011100101001010010011011101101110000000011111010000100100011010110011001101000101011110110111000110100111011010111011000011001000011111100100011101010011011111011001101101001111001101110110100100010100111011001011011111101010100000010100101010111100000000110011101111011011001000101010001110001010011011110011011110110000111010000011110111111100100011101001010010110001100101101110110101010101111010110000100101000011011111111010101010111110011101101010111110110101100000000101101010011101100000111110110000111010000011111100010100101001101001110011010001110110101101001010011111011010100111111001011100010011010011111001000111100111011101001001000110111111101010101011111001111100010100110101110011110110011101100100111110001001111110111111100100011101001010010110001111000111011111101100001110100000111101111001111100001101111111100010100111110010001110100101001011000111100011101111101100010110100011010101001111000000001100100110111100111100010111111110110101001111100100100000000101101010010101101001000110011110111001011001001011001011000001101111110010000111001101111110101011100001110001010010101011111011001001010011111011000001001000101111011111111000101000101001010101001011010101110101101101011100111110000110111111011111111010101010001101100011001101111001110001010010100000101001010101111100010101111100111010010001100010100010100101010111011001100010111001111010101110000100000100010100001101001001101111101101010011111010000110010000011010000110100111100001001111010101000000101001010101010110001010011001000111110001010011111010100111101111010000101001110111110011110000100100100001100101010111011000000001100100110111011110110011111011111100111100010111111000010100010001011111101101011101100001100100001110100100111110101010100101001111101101010011111010101000110011111001000111110001010011010111010010001000101111001101111101101010011111001111000000000100101001111101101010011110101011110100111100111100010111101010100011001100001010011111000101001111101100001110011001011000111110101010001011010100001010011111010000010110111001101110111010010100111110101010100101001111101101010011010111101010010100110011101100001111001001101000111110010001111100010100111111000111011000101100011111001111000101111110001110010011011111011010100111110011110000000001001010011111011010100111110010010000000010110101001110100100011001110000101000110111101100010110001011001101111110111001011110010001011010110001110000001101011111011010100111110010010000000010110101001111101100100111100011100111100101111000000000011100001111000010101100111000101001011001101111111000101101111000000001010100101111110001010011111010101011001111101111011100110111011101001010011111011010100111111001011100010011010011101010011011110101101100000111011110011111001000111100100101100001011001011000111000000000101111000010000111110001001111111100010100111110110001101010110111111000100111111110001010011111100011101100010110001100110111011000000001100111000111001111101010111110111111100100010001111101110110000100001000001000110111011011100101010101011011101001010011111010101010010100111110110101001111010101111010011111000001101110011010011101010011011111011001101101001111111000000001100111110101000111000101111110010111011011000101001010101001101111100010100111100001010001000101111111011000011100110010110001111110001010001010010101010101111011011111001111010010001101010100111011101001010011110010010111000100110100100101101010111010110110100100001101100010000110010011001101111100010100110111001110100101110111110100100011100000110110011110001010011110010010111011011000101001010101111011000011101000001111110001010111110011010010001101000111110110111000110100111011010111011000011001000011111100100011101010011011111011001101101001111001101010111011101111100010100110111111110110101011110111000010101100110111001010101110110100101101010111011110100000011100011101100111101101110000000011111001000111101100010111000011100111110001001111001111111100010100011101111101100100010100000001100001110100100011010001110000110011010100111110101000101000011011101111011010110011011101011101010011100001110011110101001101111101100110110100111111000011100110111011011111101010100010001010011101111010000001110001100100001100110111100101011011000101100000000101010010111111011010100111100001110011011101101111111011001101101010010000110011111001111000101101011010001101010011010011100011100100101011110011010101101110100101001111101110000110010011100000101100100111011111110110101001111110111001001000101101010000110100111010100110111110110011011010011111111011100011111011001000011100110111001101100110111110111110111111100100011101100010111000101001010101111100001011110111110111110001010011001000101011010100110111111001011011001110111001010101110111010010100111100001001001000011111101101010011111011100101100100101100101100000110111001101001111100101110110110001010010101011111001000111011000100010010111111111010100110111111100011010101001110010111101111101111110011110001011111101010111000011100010100101010101011100100010100101111100001101001110101001101111101100110110100111111000101001110111111101010011101100100111110001001111111001001011110001001011110000000001110011111011010100111111010101111101111100001010110011101101100010100101010111011110101001110001010011011110011101010011011111011001101101001111001101010111001111000101111111100010100011101111110010000000101110001100000000110111111001000101000110110101001111101010111110111110000111001101110110111111001111000010010100111100010011111100001010111001110000101001010000010100101010111110010001110110000011111100110101100011100001000110000000101011000110010110101011011010010000110110010000011001110001110011010010111011111010010001110000011011001111010010001101011001100110100011111000101100000000101010010111001110000101000100010111111011010111011000011001000011001101110110100111001000111000010101110011100001010010100111110010001110110000011111100110101100010101100001000110000000101011000111010010011110010100110101011011100110111110001010011011100111010010111011111010101111011110100101100110111110010100100000000101010010111110001001111110110010011111011000110011111001111100100011101100101111101000001111110100110100001000011101001101111011100111011000010001100100110001110110001011000110101100001000110111110111000010011010010001101000101011101011000011001110001000010011101010011011111100101101100111110011100110110101101111000010011101111111011000011100110010110001111101010101100111110111101111101100010001001011111111111000101011111001111101010111110111111011001101100010100101010111000000100101000011011111111000101001111110001110110001011000100001101010101111011010100111110000000100010100001101001110001110010011011101110100101001111001111000000000100101001110001010011100011100111011111100001110011011101101111111011011001011110110101101111001000111110100000110011011110000100111110101010100011001000101111110001010011111101011000101111010011011101011011010011101001111001101011100101001011110010111000110100111100110100011111010100110111001111011000001011100000110000011001110000010110101100110111110110010111101111101000001101001110001010011110010000000101111011101110100110011011100101101110110100110010100110011011010110100110111011010101101001011011010110111001011111001110001110010011001101000111110110111000110100101011011010111011000011001000011001101110110100101110100101001110001100111111000111001110110010111110100000111011111011001001111101010101111000110101111101100111101111010100011011110011011101101010111100100011101001111011100101111001000101111110001010011111101000001011011100101010111011101001010011100011001111110001110010101101100101111101000001110111110110010011111010101011110001101011111101101100101111011010110001101111001101110110101011110010001110100111100100101110010010000101110000001001010000110111111110001010011111010010001000101111001010101111101110001110010001110001010011011111111010101110110111110110101011010010110110101101010111010100011100111000101001100100011111000101001111101010011110111101000010100100110111011101001010011101101010111101001000010011010010111001111001000111101110011111101010101000110010001011110101011100001000001000101001101111010010001101000101011101011000011001101010011110111110000111101111111011000011100110010110001111011000000111001001101110111010010100111110110111000110100111011101001001011111100011000000001101110110011011111110010001110101001101111101100110110100111100110111011010011001010011010100011001000101111110101011101101111010011101111001010011010100011001000101101011110101011101101111010011100110100010011011101011000101101010000010100101010111010100110111111010101110100101001111000010011111000101001111110010000000101111000100111111010100110111111010100101111010011111001000111100111001001101101111101010101000110010001011111101010111011011111000111001111101010111110111010111010101011110001101010001010010101010011011101110100101001111101101010011111101010111110111111001011110000000000111000011111101100000101110000101011100011101100110100111110010001111001110001010010100111110001010111110011111011010100111110111001011001011100010100001101001111100010100111111011011101100100110101111011000100001100100001000011010111110010001110110000011111101100000101110011001101111100010100011101111110101000101000011011101111011010110011011111010101110000100000100010100110111111101010101001010011110000100001111001011111001101111110010001111101000001100110111011000000001100111011010111011000011001000011010110101011110100111101010100011001111101110001110110010000010010001011110001010011001000111110001010011111101010111011011111100010011111100110010111111000010001100100110000010100101010111010010001100010100010100101010111011000000001100111100000000110010010100010001001111001010001100010100110010100011010101110110111010111100010100010100101010110111111100100011101010111000010000010001010010011011111000101011111001110001110011111011001001011001001101001111001011110000000000111000011111010000000101101011011101110100101001111101111000000000001000011010011110011110001011111110110000110011100111111001000111101010100010101110000100111000101001111100010100111110010010000110010110011000010011111010101110110111001011010101110101100001100111000111110110101001111110110011100111110010001111101011000101111101000100110111011101001010011100001110011011101101111111011100010000100111100111000101000001110110101001101001111000100111100111111110001010011111001000111110100000110010110101011

</details>


기존 데이터 사이즈 : 92104Bit  
인코딩 데이터 사이즈 : 50667Bit

최종적으로 기존 사이즈에서 50667 / 92104 = 0.55 = 55% 정도로 압축되었다.
