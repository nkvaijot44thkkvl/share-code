package kadai_026;

public class JyankenExec_Chapter26 {

	public static void main(String[] args) {
		//じゃんけんクラスのインスタンス作成
		Jyanken_Chapter26 hoge = new Jyanken_Chapter26();

		//自分のじゃんけんの手を入力
		hoge.getMyChoice();

		//対戦相手のじゃんけんがランダムで選ばれる
		hoge.getRandom();
		
		//じゃんけんの結果を出力する
		hoge.playGame(aite);

	}

}
