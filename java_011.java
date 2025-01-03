package text.section_04;

public class Data_01 {

	public static void main(String[] args) {
    //論理型を宣言、初期化
		 boolean natural = true;
		 
     //int型の配列を宣言
		 int[] naturalArray;

     //素数か判定するための平方根の変数宣言
		 double squareRoot;
		
      //繰り返し処理で100までループ
		 for(int i = 1; i <= 100 ; i++ ) {

      //変数に自然数iの平方根を代入
			 squareRoot = Math.sqrt(i);

       //自然数判定　約数が2/3/5/7の時は素数ではないとする
			 if(squareRoot % 2 == 0) {
				 natural = false;
			 }else if(squareRoot % 3 == 0) {
				 natural = false;		 
			 }else if(squareRoot % 5 == 0) {
				 natural = false;
			 }else if(squareRoot % 7 == 0) {
				 natural = false;
			 }
			 
       /*素数フラグがtrueの時、int型配列に追加
       配列の要素数+1に追加することで、宣言時にインデックスを固定しない
       */
			 if(natural == true) {				
				 naturalArray(naturalArray.length) = i;
				 
			 }
		 }
		 
	}
}
