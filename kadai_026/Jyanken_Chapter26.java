package kadai_026;

import java.util.HashMap;

public class Jyanken_Chapter26 {
	HashMap<String,String> jyanken = new HashMap<String,String>;
	public String myChoice;
	public String yourChoice;

	public Jyanken_Chapter26() {
		jyanken.put("r", "グー");
		jyanken.put("s", "チョキ");
		jyanken.put("p", "パー");

		myChoice = "";
		yourChoice = "";
	}
	
	public void playGame(String myChoice, String yourChoice) {
		switch (myChoice) {
		case "r" -> {
										
									}
		case "s" -> {}
		case "p" -> {}
		}
	}

  public String getRandom(String deme) {
                //対戦相手のじゃんけんがランダムで選ばれる
                String aite;
                int deme = Math.random()*3;
                deme = deme * 10;
                swtch(deme) {
                case 1 -> aite = "r";
                case 2 -> aite = "p";
                else -> aite = "s";
	}

	public String getMyChoice(){
                Scanner fuga = new Scanner(System.in);
                fuga.next();
                fuga.close();

	} 
}
