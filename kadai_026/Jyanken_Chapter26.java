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

	}

	public String getMyChoice(){

	} 
}
