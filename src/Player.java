
import java.util.*;

public class Player {
	
	double balance;
	Hand hand;
	
	public Player(double cash) {
		balance = cash;
		hand = new Hand();
	}
	
	public void automatic(ArrayList<Card> deck, ArrayList<Card> dump,
			Player challenger) {
		while (hand.total < 17 && challenger.hand.total >= hand.total) {
			hand.hit(deck, dump);
			hand.aceCheck();
		}
	}
	
}
