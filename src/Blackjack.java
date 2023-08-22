
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class Blackjack {
	
	public static ArrayList<Card> initialise(){
		
		String[] suits = {"C", "D", "H", "S"};
		String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
		
		//creates an ordered deck of cards in an array
		Card[] fullDeck = new Card[52];
		int counter = 0;
		for (String rank : ranks) {
			for (String suit : suits) {
				Card createdCard = new Card(rank, suit);
				fullDeck[counter] = createdCard;
				counter++;
			}	
		}
		
		//creates a copy of the ordered deck in an arraylist
		ArrayList<Card> unshuffledDeck = new ArrayList<Card>();
		for (Card card : fullDeck) {
			unshuffledDeck.add(card);
		}
		
		//randomly moves the cards in the arraylist to a new one to create a shuffled deck
		Random rand = new Random(); 
		ArrayList<Card> shuffledDeck = new ArrayList<Card>(); 
		for (int i = 0; i < 52; i++) {
			int n = rand.nextInt(unshuffledDeck.size());
			shuffledDeck.add(unshuffledDeck.get(n));
			unshuffledDeck.remove(n);
		}	
		
		//System.out.println(shuffledDeck.toString());
		//System.out.println(shuffledDeck.size() + "\n");
		return shuffledDeck;
		
	}
	
	
	public static boolean[] round(ArrayList<Card> deck, ArrayList<Card> dump, 
			Player challenger, Player dealer, double pot, boolean splitDone) {
		
		Scanner keyboard = new Scanner(System.in);
		challenger.hand.aceCheck();
		
		//generates and prints out yours and the dealers hands and scores
		if (!challenger.hand.soft) {
			System.out.println("\nYour hand : " + challenger.hand.cards.toString() 
			+ ";	Your total : " + challenger.hand.total); 
		} else {
			System.out.println("\nYour hand : " + challenger.hand.cards.toString() 
			+ ";	Your total : soft " + challenger.hand.total); 
		}
		System.out.println("The dealer's hand : ["
		+ dealer.hand.cards.get(0).toString() + ", XX]"
		+ ";	The dealer's visible total : " + dealer.hand.cards.get(0).value);
		
		//allows you to hit, double down, split or stay depending on the input and conditions
		boolean over = false;
		boolean down = false;
		boolean split = false;
		if (challenger.hand.total < 21) {
			if (challenger.hand.cards.size() == 2) {
				if (challenger.hand.cards.get(0).value == challenger.hand.cards.get(1).value && !splitDone) {
					System.out.println("Do you want to hit, h; double down, d; split, /; or stay, s.");
				} else {
					System.out.println("Do you want to hit, h; double down, d; or stay, s.");
				}
			} else System.out.println("Do you want to hit, h; or stay, s.");
			String choice = keyboard.nextLine();
					
			if (choice.equals("h")) {
				System.out.println("Hit!");
				challenger.hand.hit(deck, dump);
				challenger.hand.aceCheck();
			
			} else if (choice.equals("d") && challenger.hand.cards.size() == 2 ) {
				System.out.println("Double down!");
				challenger.balance -= pot * 0.5;
				dealer.balance -= pot * 0.5;
				pot = pot * 2;
				challenger.hand.hit(deck, dump);
				challenger.hand.aceCheck();
				System.out.println("Your hand: " + challenger.hand.cards.toString());
				down = true;
				over = true;
				
			} else if (choice.equals("/") && challenger.hand.cards.size() == 2 && !splitDone) {
				if (challenger.hand.cards.get(0).value == challenger.hand.cards.get(1).value) {
					System.out.println("Split!");
					split = true;
					over = true;
				}
				
			} else if (choice.equals("s")) {
				System.out.println("Stay.");
				over = true;
			}
			
		} else {
			over = true;	
		}
		
		boolean[] boolList = {over, down, split};
		return boolList;

	}
	
	
	public static String check(Player challenger, Player dealer, 
			ArrayList<Card> deck, ArrayList<Card> dump, double pot) {
		
		//checks to see the conditions at the end of the round and who won	
		String end;
		if (challenger.hand.total == 21 && challenger.hand.cards.size() == 2) {
			if(dealer.hand.total == 21 && dealer.hand.cards.size() == 2) {
				System.out.println("\nThe Dealer's hand : " + dealer.hand.cards.toString());
				System.out.println("\n" + challenger.hand.total + " : " + dealer.hand.total);
				end = "draw";
			} else {
				System.out.println("\nThe Dealer's hand : " + dealer.hand.cards.toString());
				System.out.println("\n" + challenger.hand.total + " : " + dealer.hand.total);
				System.out.println("Blackjack!");
				end = "win";
				challenger.balance += pot * 0.25;
				dealer.balance -= pot * 0.25;
			}
			
		} else if (challenger.hand.total > 21) {
			System.out.println("\n" + challenger.hand.total + " : " + dealer.hand.total);
			System.out.println("Bust!");
			end = "lose";
			
		} else {
			System.out.println("\nDealer's turn...");
			dealer.automatic(deck, dump, challenger);
			System.out.println("\nThe Dealer's hand : " + dealer.hand.cards.toString());
			System.out.println("\n" + challenger.hand.total + " : " + dealer.hand.total);
			
			if (dealer.hand.total == 21 && dealer.hand.cards.size() == 2) {
				System.out.println("The dealer has blackjack!");
				end = "lose";
			} else if (dealer.hand.total > 21) {
			System.out.println("The dealer went bust!");
			end = "win";
				
			} else if (challenger.hand.total > dealer.hand.total) {
				end = "win";
			} else if(challenger.hand.total == dealer.hand.total) {
				end = "draw";
			} else {
				end = "lose";
			}
		
		}
		
		return end;
	
	}
	
	
	public static void winner(Player challenger, Player dealer, double pot, String result) {
		//checks at the end to see who won and makes the according changes to the balances
		if (result.equals("win")) {
			System.out.println("You win!");
			challenger.balance += pot;
		} else if (result.equals("draw")) {
			System.out.println("You broke even.");
			challenger.balance += pot * 0.5;
			dealer.balance += pot * 0.5;
		} else {
			System.out.println("You lose.");
			dealer.balance += pot;
		}
	}
		
	
	public static void main(String[] args) {
		
		//retrieving the saved balance
		double oldBalance;
		try {
			File cash = new File("src/cash.txt");
			Scanner bank = new Scanner(cash);
			oldBalance = bank.nextDouble();
			bank.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("could not open file.");
			oldBalance = 500;
		}
		
		//sets up the game
		ArrayList<Card> deck = initialise();
		ArrayList<Card> dump = new ArrayList<Card>();
		Player dealer = new Player(10000);
		Player challenger;
		double pot = 0;
		DecimalFormat df = new DecimalFormat("#.00");
		
		//checks to see if the player wants to continue with their past balance
		System.out.println("Welcome to Blackjack java edition!");
		Scanner keyboard = new Scanner(System.in); 
		while (true) {
			
			System.out.println("\nWould you like to continue with your previous balance? y  or n");
			String response = keyboard.nextLine();
			
			if (response.equals("n")) {
				challenger = new Player(500);
				break;
				
			} else if (response.equals("y")) {
				challenger = new Player(oldBalance);
				break;
			}
		}
		System.out.println("Your balance is £" + df.format(challenger.balance));
		
		while (true) {
			
			System.out.println("Do you want to play a round of blackjack? y or n.");
			String answer = keyboard.nextLine();
			
			if (answer.equals("n")) {
				break;
				
			} else if (answer.equals("y")) {
				
				boolean goodBet = false;
				while(!goodBet) {
					//takes in a bet and makes sure it is to 2dp
					System.out.println("How much do you want to bet?");
					String input = keyboard.nextLine();
					try {
						double bet = Double.parseDouble(input);
						double betInPence = ((int)(bet * 100));
						bet = betInPence / 100;
						
						//checks to see if the player and the dealer can afford the bet
						if (bet <= challenger.balance && bet <= dealer.balance) {
							pot += bet * 2;
							challenger.balance -= bet;
							dealer.balance -= bet;
							goodBet = true;
						} else if (bet > challenger.balance) {
							System.out.println("I am sorry "
							+ "but you do not have sufficient funds for that bet.");
							System.out.println("Your balance is £" + df.format(challenger.balance));
						} else {
							System.out.println("I am sorry "
									+ "but the dealer does not have sufficient funds for that bet.");
							System.out.println("The dealer's balance is £" 
							+ df.format(dealer.balance));
							System.out.print("\nIf the dealer does not have sufficient funds to warant playing,"
							+ " \nplease finish this round of blackjack by betting a small ammount"
							+ " that the dealer can afford then say no to playing another round."
							+ " \nYou can return at a later date once the dealer's funds have been restocked.\n");
						}
							
					} catch (Exception e) {
					}
				}
				
				//generates the hands and starts the round
				System.out.println("The dealer is dealing the cards...");
				challenger.hand.newHand(deck, dump);
				dealer.hand.newHand(deck, dump);
				boolean splitDone = false;
				
				//runs the whole round of blackjack
				boolean finished = false;
				boolean down = false;
				boolean split = false;
				while (!finished) {
					boolean[] boolList = round(deck, dump, challenger, dealer, pot, splitDone);
					finished = boolList[0];
					down = boolList[1];
					split = boolList[2];
					if (down) {
						pot *= 2;
					}
				}
				
				if (!split) {
					String result = check(challenger, dealer, deck, dump, pot);
					winner(challenger, dealer, pot, result);
					
				// if the player has split a round of blackjack is played with each card as a new deck
				} else {
					
					splitDone = true;
					challenger.balance -= pot * 0.5;
					dealer.balance -= pot * 0.5;
					ArrayList<Card> secondCards = new ArrayList<Card>();
					secondCards.add(challenger.hand.cards.get(1));
					challenger.hand.cards.remove(1);
					if (challenger.hand.cards.get(0).rank.equals("A")) {
						challenger.hand.cards.get(0).value = 11;
						challenger.hand.soft = true;
					}
					challenger.hand.total = challenger.hand.cards.get(0).value;
					
					finished = false;
					down = false;
					while (!finished) {
						boolean[] boolList = round(deck, dump, challenger, dealer, pot, splitDone);
						finished = boolList[0];
						down = boolList[1];
						if (down) {
							pot *= 2;
						}
					}
					
					ArrayList<Card> firstCards = new ArrayList<Card>();
					firstCards = challenger.hand.cards;
					challenger.hand.cards = secondCards;
					challenger.hand.total = challenger.hand.cards.get(0).value;
					
					finished = false;
					down = false;
					while (!finished) {
						boolean[] boolList = round(deck, dump, challenger, dealer, pot, splitDone);
						finished = boolList[0];
						down = boolList[1];
						if (down) {
							pot *= 2;
						}
					}
					
					secondCards = challenger.hand.cards;
					challenger.hand.cards = firstCards;
					for (Card card : challenger.hand.cards) {
						dump.add(card);
					}
					
					challenger.hand.total = 0;
					for (Card card : challenger.hand.cards) {
						challenger.hand.total += card.value;
					}
					String result = check(challenger, dealer, deck, dump, pot);
					winner(challenger, dealer, pot, result);
					
					challenger.hand.cards = secondCards;
					challenger.hand.total = 0;
					for (Card card : challenger.hand.cards) {
						challenger.hand.total += card.value;
					}
					result = check(challenger, dealer, deck, dump, pot);
					winner(challenger, dealer, pot, result);
				}
				
				pot = 0;
				System.out.println("\nYour balance is £" +df.format(challenger.balance));
				
			}
			
		}
		
		System.out.println("Your final balance is £" + df.format(challenger.balance));
		System.out.println("Thank you for playing Blackjack java edition.");
		
		try {
			//writes the player's balance to a file so it can be saved
			File cash = new File("src/cash.txt");
			PrintWriter writer = new PrintWriter(cash);
			writer.println(Double.toString(challenger.balance));
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("could not open file.");
		}
	}

}
