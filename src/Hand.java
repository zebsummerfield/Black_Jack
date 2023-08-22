
import java.util.*;

public class Hand {
	
	ArrayList<Card> cards;
	int total;
	boolean soft;
	
	public Hand() {
		cards = new ArrayList<Card>();
		soft = false;
	}
	
	
	public static void reshuffle(ArrayList<Card> deck, ArrayList<Card> dump) {
		Random rand = new Random();
		int runLength = dump.size();
		for (int i = 0; i < runLength; i++) {
			int n = rand.nextInt(dump.size());
			deck.add(dump.get(n));
			dump.remove(n);
		}
		for (Card card  : deck) {
			if (card.rank.equals("A")) card.value = 11;
		}
	}
	
		
	public void hit(ArrayList<Card> deck, ArrayList<Card> dump) {
		
		//if the deck is empty re-shuffle the dump pile
		if (deck.size() < 1) {
			reshuffle(deck, dump);
		}
		
		//adds a card from the top of the deck to the hand and adds its value to the total
		cards.add(deck.get(0));
		total += (deck.get(0)).value;
		if (deck.get(0).rank.equals("A")) {
			soft = true; 
		}
		deck.remove(0);
		
	}
	
	
	public void newHand(ArrayList<Card> deck, ArrayList<Card> dump) {
		
		soft = false;
		
		//moves the cards in the hand to the dump and redraws 2 new cards
		for (Card card : cards) {
			dump.add(card);
		}
		cards.clear();
		
		//if the deck is empty re-shuffle the dump pile
		if (deck.size() < 2) {
			reshuffle(deck, dump);
		}
		
		//adds two cards from the top of the deck to the hand and adds their value to the total
		cards.add(deck.get(0));
		if (deck.get(0).rank.equals("A")) {
			soft = true;
		}
		deck.remove(0);
		
		cards.add(deck.get(0));
		if (deck.get(0).rank.equals("A")) {
			soft = true; 
		}
		deck.remove(0);
		
		total = (cards.get(0)).value + (cards.get(1)).value;

	}
	
	
	public void aceCheck() {
		if (total > 21 && soft) {
			for (Card card : cards) {
				if (card.value == 11) {
					card.value = 1;
					total -= 10;
					soft = false;
					break;
				}	
			}
			
			for (Card card : cards) {
				if (card.value == 11) {
					soft = true;
				}
			}
		}
	}
	
	
}
