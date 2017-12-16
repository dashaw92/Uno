package me.daniel.uno.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.daniel.uno.objects.Card.Suit;
import me.daniel.uno.objects.Card.Type;

public class Deck {
	
	private List<Card> cards;
	
	public Deck(List<Card> cards) {
		this.cards = cards;
	}
	
	public Deck() {
		this.cards = new ArrayList<>();
	}
	
	public void addCard(Card c) {
		cards.add(c);
	}
	
	//Returns the first matching card, removing it from the deck
	public Card getFirstCard(Type type, Suit suit) {
		for(int i = 0; i < cards.size(); i++) {
			Card card = cards.get(i);
			if(card.type == type && card.suit == suit) {
				return cards.remove(i);
			}
		}
		return null;
	}
	
	public boolean containsCard(Type type, Suit suit) {
		for(Card c : cards) {
			if(c.type == type && c.suit == suit) {
				return true;
			}
		}
		return false;
	}

	public Card getCardAt(int i) {
		if(cards.size() < i || cards.isEmpty()) {
			return null;
		}
		return cards.remove(i);
	}
	
	public Card draw() {
		return getCardAt(0);
	}
	
	public Card peekCardAt(int i) {
		if(cards.size() < i || cards.isEmpty()) {
			return null;
		}
		return cards.get(i - 1);
	}
	
	public int getCardCount() {
		return cards.size();
	}
	
	public void empty() {
		cards.clear();
	}
	
	public void moveCardsFromDeck(Deck d) {
		for(int i = 0; i < d.getCardCount(); i++) {
			addCard(d.getCardAt(i));
		}
		d.empty();
		Collections.shuffle(cards);
	}
	
	public List<Card> getCards() {
		return cards;
	}
	
	/*
	 * The deck consists of 108 total cards. 19 numeric cards + 2 skips + 2 reverses + 2 draw twos per suit.
	 * There is also 4 wild cards and 4 draw four cards.
	 */
	public static Deck generateDeck() {
		List<Card> deck = new ArrayList<>();
		deck.add(new Card(Type.getNumeric(0), Suit.RED));
		deck.add(new Card(Type.getNumeric(0), Suit.GREEN));
		deck.add(new Card(Type.getNumeric(0), Suit.YELLOW));
		deck.add(new Card(Type.getNumeric(0), Suit.BLUE));
		for(int j = 0; j < 2; j++) {
			deck.add(new Card(Type.REVERSE, Suit.RED));
			deck.add(new Card(Type.REVERSE, Suit.GREEN));
			deck.add(new Card(Type.REVERSE, Suit.YELLOW));
			deck.add(new Card(Type.REVERSE, Suit.BLUE));
			
			deck.add(new Card(Type.SKIP, Suit.RED));
			deck.add(new Card(Type.SKIP, Suit.GREEN));
			deck.add(new Card(Type.SKIP, Suit.YELLOW));
			deck.add(new Card(Type.SKIP, Suit.BLUE));
			
			deck.add(new Card(Type.DRAW_TWO, Suit.RED));
			deck.add(new Card(Type.DRAW_TWO, Suit.GREEN));
			deck.add(new Card(Type.DRAW_TWO, Suit.YELLOW));
			deck.add(new Card(Type.DRAW_TWO, Suit.BLUE));
			for(int i = 1; i < 9; i++) {
				deck.add(new Card(Type.getNumeric(i), Suit.RED));
				deck.add(new Card(Type.getNumeric(i), Suit.GREEN));
				deck.add(new Card(Type.getNumeric(i), Suit.YELLOW));
				deck.add(new Card(Type.getNumeric(i), Suit.BLUE));
			}
		}
		for(int i = 0; i < 4; i++) {
			deck.add(new Card(Type.DRAW_FOUR, Suit.WILD));
			deck.add(new Card(Type.WILD, Suit.WILD));
		}
		Collections.shuffle(deck);
		return new Deck(deck);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Card c : cards) {
			sb.append(c.toString());
		}
		return sb.toString();
	}
	
}