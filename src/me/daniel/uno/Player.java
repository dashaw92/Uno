package me.daniel.uno;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import me.daniel.uno.objects.Card;
import me.daniel.uno.objects.Deck;
import me.daniel.uno.objects.GameAction;

public class Player {
	
	private Socket client;
	private String nick;
	private int uid;
	private Deck hand;
	
	public Player(Socket client, String nick, int uid) {
		this.client = client;
		this.nick = nick;
		this.uid = uid;
		this.hand = new Deck();
	}
	
	public int getUid() {
		return uid;
	}
	
	public String getNick() {
		return nick;
	}
	
	public void giveCard(Card c) {
		hand.addCard(c);
	}
	
	public List<Card> getCards() {
		return hand.getCards();
	}
	
	public GameAction requestInput() {
		
		while(true) {
			sendString("It's your move. Valid options: PLAY, UNO, DRAW, or SKIP\n\r");
			sendString(String.format("Your hand: %s\n\r", hand));
			Scanner reader = null;
			try {
				//Do not close this scanner, it will also close the client input stream.
				reader = new Scanner(client.getInputStream());
			} catch (IOException e) { continue; }
			String input = reader.nextLine().replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().trim();
			switch(input.split(" ")[0]) {
				case "uno":
					if(hand.getCardCount() > 1) {
						sendString("You cannot declare Uno, you have more than one card still!\n\r");
						continue;
					}
					return new GameAction(GameAction.Type.UNO);
				case "draw":
					return new GameAction(GameAction.Type.DRAW);
				case "play":
					if(input.split(" ").length < 2) {
						sendString("Correct usage: play suit type\n\rExample: play blue eight\n\r");
						continue;
					}
					try {
						Card.Suit suit = Card.Suit.valueOf(input.split(" ")[1].toUpperCase());
						String type_args = input.substring(5 + suit.toString().length()).trim().replace(' ', '_');
						Card.Type type = Card.Type.valueOf(type_args.toUpperCase());
						if(!hand.containsCard(type, suit)) {
							sendString("You do not have this card in your hand.\n\r");
							continue;
						}
						return new GameAction(GameAction.Type.PLAY, hand.getFirstCard(type, suit));
					} catch(Exception e) {
						sendString("Invalid card suit or type.\n\r");
						continue;
					}
				case "skip":
					return new GameAction(GameAction.Type.SKIP);
			}
		}
	}
	
	public void sendString(String msg) {
		try {
			client.getOutputStream().write(msg.getBytes());
		} catch (IOException e) {
			//System.err.printf("Failed to write message to client %s, uid %d\n", nick, uid);
		}
	}
	
	public void disconnect() {
		try {
			client.close();
		} catch (IOException e) {
			//nah I don't care, take your verbosity from whence it came!
		}
	}
}