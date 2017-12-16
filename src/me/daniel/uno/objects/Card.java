package me.daniel.uno.objects;

public class Card {
	
	Type type;
	Suit suit;
	
	public Card(Type type, Suit suit) {
		this.type = type;
		this.suit = suit;
	}
	
	public String toString() {
		return String.format("%S %S, ", suit, type).replace('_', ' '); 
	}
	
	public enum Type {
		DRAW_FOUR(-1), DRAW_TWO(-2), WILD(-3), SKIP(-4), REVERSE(-5), ZERO(0), 
		ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), 
		NINE(9);
		
		private int numeric;
		
		private Type(int numeric) {
			this.numeric = numeric;
		}
		
		public static Type getNumeric(int number) {
			if(number < 0 || number > 9) {
				return null;
			}
			for(Type t: Type.values()) {
				if(t.numeric == number)
					return t;
			}
			return null;
		}
	}
	
	public enum Suit {
		WILD, RED, GREEN, BLUE, YELLOW;
	}
}