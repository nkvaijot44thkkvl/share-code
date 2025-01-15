package kadai_026;

public class JyankenExec_Chapter26 {

	public static void main(String[] args) {
		//じゃんけんクラスのインスタンス作成
		Jyanken_Chapter26 hoge = new Jyanken_Chapter26();

		//自分のじゃんけんの手を入力
		Scanner fuga = new Scanner(System.in);
		fuga.next();
		fuga.close();

		//対戦相手のじゃんけんがランダムで選ばれる
		String aite;
		int deme = Math.random()*3;
		deme = deme * 10;
		swtch(deme) {
		case 1 -> aite = "r";
		case 2 -> aite = "p";
		else -> aite = "s";
		
		//じゃんけんの結果を出力する
		

	}

}
