# 허프만 압축

고예진, 윤동관, 조준희

## 허프만 압축이란?
어느 파일 속의 각 문자들이 아스키코드(ASCII)로 저장되어 있다면, 그 파일은 문자 수 * 8 bit의 크기를 갖게 된다. 이 파일을 저장과 전송에 효율적으로 사용하기 위해, 필요 시 크기를 압축하고 다시 복구할 수 있다면, 파일의 저장과 전송에 용이할 것이다. 허프만 트리를 이용해 각 문자에 프리픽스 값을 할당해 파일을 압축하는 알고리즘을 허프만 압축 알고리즘이라고 한다.
