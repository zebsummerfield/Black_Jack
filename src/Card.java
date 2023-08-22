
public class Card {
	
	String rank;
	int value;
	String suit;
	
	public Card(String name, String type) {
		
		rank = name;
		suit = type;
		
		//assigns the card a value depending on its rank
		if (name.equals("A")) {
			value = 11;
			
		} else if(name.equals("J") || name.equals("Q") || name.equals("K")) {
			value = 10;
			
		} else {
			value = Integer.parseInt(rank);
		}
		
	}
	
	public String toString() {
		return rank + suit;
	}
		
}
