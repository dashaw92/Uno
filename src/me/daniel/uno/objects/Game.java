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
	private boolean action = false;
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
		if(turn <= 0) turn = players.size();
		Player current = players.get(turn % players.size());
		Server.broadcast("&u                                                        &w\r\n");
		Server.broadcast(String.format("\n\r&cTurn %d - It's &p%s's&c turn.&w\n\r", turncount, current.getNick()));
		if(discard.getCardCount() > 0 && !action) { 
			switch(getTopDiscarded().type) {
				case SKIP:
					Server.broadcast(String.format("%s was skipped this turn.\n\r", current.getNick()));
					action = true;
					turnInc();
					return;
				case DRAW_FOUR:
					Server.broadcast(String.format("%s had to draw four.\n\r", current.getNick()));
					action = true;
					for(int i = 0; i < 4; i++) {
						Card c = drawing.draw();
						current.sendString(String.format("You drew %s\n\r", c));
						current.giveCard(c);
					}
					turnInc();
					return;
				case DRAW_TWO:
					Server.broadcast(String.format("%s had to draw two.\n\r", current.getNick()));
					action = true;
					for(int i = 0; i < 2; i++) {
						Card c = drawing.draw();
						current.sendString(String.format("You drew %s\n\r", c));
						current.giveCard(c);
					}
					turnInc();
					return;
				default:
					break;
					
			}
		}
		boolean in_turn = true;
		boolean drew = false;
		boolean show_info = true;
		Card last = getTopDiscarded();
		while(in_turn) {
			GameAction gaction = current.requestInput(last.toString(), show_info);
			switch(gaction.type) {
				case CHAT:
					Server.broadcast(gaction.type.getMsg());
					gaction.type.setMsg("");
					show_info = false;
					continue;
				case SKIP:
					boolean can_move = false;
					if(!drew) {
						current.sendString("You have not drawn a card yet, and cannot skip.\n\r");
						show_info = false;
						break;
					}
					for(Card c: current.getCards()) {
						if(isLegalMove(c)) {
							current.sendString("You are able to play a card this round, and cannot skip.\n\r");
							show_info = false;
							can_move = true;
							break;
						}
					}
					if(!can_move) {
						Server.broadcast(String.format("%s has skipped their turn.\n\r", current.getNick()));
						turnInc();
						return;
					}
					break;
				case UNO:
					Server.broadcast(String.format("%s has declared Uno!\n\rThe game lasted %d turns.\n\r", current.getNick(), turncount));
					playing = false;
					return;
				case DRAW:
					show_info = false;
					if(drew) {
						current.sendString("You already drew a card this turn!\n\r");
						continue;
					}
					if(drawing.getCardCount() == 0) {
						Server.broadcast("&cThe discard pile is being shuffled.&w\n\r");
						drawing.moveCardsFromDeck(discard);
						discard.addCard(drawing.draw());
					}
					Card next = drawing.draw();
					current.sendString(String.format("You drew %s\n\r", next));
					current.giveCard(next);
					Server.broadcast(String.format("%s drew a card.\n\r", current.getNick()));
					drew = true;
					continue;
				case PLAY:
					Card toPlay = gaction.card;
					if(!isLegalMove(toPlay)) {
						show_info = false;
						current.giveCard(toPlay);
						current.sendString("&cThat is not a legal card to play.&w\n\r");
						continue;
					}
					discard.addCard(toPlay);
					Server.broadcast(String.format("%s played %s%S %S&w.\n\r", current.getNick(), toPlay.suit.code, toPlay.suit, toPlay.type));
					action = false;
					switch(toPlay.type) {
						case REVERSE:
							direction = direction == 1? -1 : 1;
							turn += direction;
							break;
						default:
							break;
					}
					in_turn = false;
					break;
			}
		}
		
		turnInc();
	}
	
	public boolean isLegalMove(Card c) {
		return c.suit == Card.Suit.WILD 
				|| getTopDiscarded() == null 
				|| c.suit == getTopDiscarded().suit 
				|| c.type == getTopDiscarded().type
				|| getTopDiscarded().suit == Card.Suit.WILD;
	}
	
	private void turnInc() {
		turn += direction;
		if(turn < 0) {
			turn = players.size();
		}
	}
}
