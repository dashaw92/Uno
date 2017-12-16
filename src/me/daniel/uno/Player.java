package me.daniel.uno;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import me.daniel.uno.objects.Card;
import me.daniel.uno.objects.Deck;
import me.daniel.uno.objects.GameAction;
import me.daniel.uno.util.Formatter;

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
		
		sendString("&cWelcome to Uno, programmed by Daniel Shaw!\n\r");
		sendString("&cTo play, you will select one of four options (&pPLAY&c, &pDRAW&c, &pUNO&c, or &pSKIP&c)\n\r");
		sendString("&cTo play a card, just type &pplay <suit> <type>&c. For example: &pplay red two&c.\n\r");
		sendString("&cYou can also send chat messages by using \"&p/&c\" as a prefix.\n\r&cHave fun!&w\n\r\n\r");
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
	
	@SuppressWarnings("resource")
	public GameAction requestInput(String last, boolean show_status) {
		while(true) {
			if(show_status) {
				sendString(String.format("\n\rLast move: %s\n\r", last == null? "&cNone" : last));
				sendString("It's your move. Valid options: &cPLAY&w, &cUNO&w, &cDRAW&w, or &cSKIP&w\n\r");
				sendString(String.format("&w&cYour hand: %s\n\r", hand));
				show_status = false;
			}
			Scanner reader = null;
			sendString("What will you do? &p");
			try {
				//Do not close this scanner, it will also close the client input stream.
				reader = new Scanner(client.getInputStream());
			} catch (IOException e) { continue; }
			String input = reader.nextLine();
			sendString("&w");
			if(input.startsWith("/") && input.length() > 1) {
				GameAction.Type chat = GameAction.Type.CHAT;
				chat.setMsg(String.format("&y[CHAT] &p%s&w: &w%s\n\r", nick, input.substring(1)));
				return new GameAction(chat);
			}
			input = input.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().trim();
			switch(input.split(" ")[0]) {
				case "uno":
					if(hand.getCardCount() > 1) {
						sendString("&cYou cannot declare Uno, you have more than one card still!&w\n\r");
						continue;
					}
					return new GameAction(GameAction.Type.UNO);
				case "draw":
					return new GameAction(GameAction.Type.DRAW);
				case "play":
					if(input.split(" ").length < 2) {
						sendString("&cCorrect usage: &pplay suit type&w\n\r&cExample: &pplay blue eight&w\n\r");
						continue;
					}
					try {
						Card.Suit suit = Card.Suit.valueOf(input.split(" ")[1].toUpperCase());
						String type_args = input.substring(5 + suit.toString().length()).trim().replace(' ', '_');
						Card.Type type = Card.Type.valueOf(type_args.toUpperCase());
						if(!hand.containsCard(type, suit)) {
							sendString("&cYou do not have this card in your hand.&w\n\r");
							continue;
						}
						return new GameAction(GameAction.Type.PLAY, hand.getFirstCard(type, suit));
					} catch(Exception e) {
						sendString("&cInvalid card suit or type.&w\n\r");
						continue;
					}
				case "skip":
					return new GameAction(GameAction.Type.SKIP);
			}
		}
	}
	
	public void sendString(String msg) {
		msg = Formatter.format(msg);
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