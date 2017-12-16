package me.daniel.uno.objects;

import java.util.List;

import me.daniel.uno.Player;
import me.daniel.uno.Server;

public class Game {
	private Deck drawing = Deck.generateDeck();
	private Deck discard = new Deck();
	private int turn = 0;
	private int turncount = 0;
	private int direction = 1;
	private boolean action_done = false;
	private List<Player> players;
	private boolean playing = false;
	
	public Game(List<Player> players) {
		this.players = players;
		for(int i = 0; i < players.size(); i++) {
			for(int j = 0; j < 7; j++) {
				players.get(i).giveCard(drawing.draw());
			}
		}
		Card c = drawing.draw();
		while(c.suit == Card.Suit.WILD || c.type == Card.Type.DRAW_TWO || c.type == Card.Type.REVERSE || c.type == Card.Type.SKIP) {
			drawing.addCard(c);
			c = drawing.draw();
		}
		discard.addCard(c);
		playing = true;
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	private Card getTopDiscarded() {
		return discard.peekCardAt(discard.getCardCount());
	}
	
	public void turn() {
		turncount += 1;
		Player current = players.get(turn % players.size());
		Server.broadcast(String.format("\n\rTurn %d - It's %s's turn.\n\r", turncount, current.getNick()));
		if(discard.getCardCount() > 0) { 
			switch(getTopDiscarded().type) {
				case SKIP:
					if(action_done) {
						action_done = false;
					} else {
						Server.broadcast(String.format("%s was skipped this turn.\n\r", current.getNick()));
						action_done = true;
						turn += direction;
						if(turn < 0) {
							turn = players.size();
						}
						return;
					}
					break;
				case DRAW_FOUR:
					if(!action_done) {
						Server.broadcast(String.format("%s had to draw four.\n\r", current.getNick()));
						action_done = true;
						for(int i = 0; i < 4; i++) {
							Card c = drawing.draw();
							current.sendString(String.format("You drew %s\n\r", c));
							current.giveCard(c);
						}
						turn += direction;
						if(turn < 0) {
							turn = players.size();
						}
						return;
					} else {
						action_done = false;
					}
					break;
				case DRAW_TWO:
					if(!action_done) {
						Server.broadcast(String.format("%s had to draw two.\n\r", current.getNick()));
						action_done = true;
						for(int i = 0; i < 2; i++) {
							Card c = drawing.draw();
							current.sendString(String.format("You drew %s\n\r", c));
							current.giveCard(c);
						}
						turn += direction;
						if(turn < 0) {
							turn = players.size();
						}
						return;
					} else {
						action_done = false;
					}
					break;
				default:
					break;
					
			}
		}
		boolean in_turn = true;
		boolean drew = false;
		Card last = getTopDiscarded();
		current.sendString(String.format("Last card: %s\n\r", last == null? "None" : last));
		while(in_turn) {
			GameAction action = current.requestInput();
			switch(action.type) {
				case SKIP:
					boolean can_move = false;
					for(Card c: current.getCards()) {
						if(isLegalMove(c)) {
							current.sendString("You are able to play a card this round, and cannot skip.\n\r");
							can_move = true;
							break;
						}
					}
					if(!can_move) {
						Server.broadcast(String.format("%s has skipped their turn.\n\r", current.getNick()));
						turn += direction;
						if(turn < 0) {
							turn = players.size();
						}
						return;
					}
					break;
				case UNO:
					Server.broadcast(String.format("\n%s has declared Uno!\n\rThe game lasted %d turns.\n\r", current.getNick(), turncount));
					playing = false;
					return;
				case DRAW:
					if(drew) {
						current.sendString("You already drew a card this turn!\n\r");
						continue;
					}
					if(drawing.getCardCount() == 0) {
						drawing.moveCardsFromDeck(discard);
					}
					current.giveCard(drawing.draw());
					Server.broadcast(String.format("%s drew a card.\n\r", current.getNick()));
					drew = true;
					continue;
				case PLAY:
					Card toPlay = action.card;
					if(!isLegalMove(toPlay)) {
						current.giveCard(toPlay);
						current.sendString("That is not a legal card to play.\n\r");
						continue;
					}
					discard.addCard(toPlay);
					Server.broadcast(String.format("%s played %S %S.\n\r", current.getNick(), toPlay.suit, toPlay.type));
					switch(toPlay.type) {
						case REVERSE:
							direction = direction == 1? -1 : 1;
							if(players.size() == 2) 
								turn += direction;
							break;
						default:
							break;
					}
					in_turn = false;
					break;
			}
		}
		
		turn += direction;
		if(turn < 0) {
			turn = players.size() - 1;
		}
	}
	
	public boolean isLegalMove(Card c) {
		return c.suit == Card.Suit.WILD 
				|| getTopDiscarded() == null 
				|| c.suit == getTopDiscarded().suit 
				|| c.type == getTopDiscarded().type
				|| getTopDiscarded().suit == Card.Suit.WILD;
	}
}