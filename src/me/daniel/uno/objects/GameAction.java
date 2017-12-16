package me.daniel.uno.objects;

public class GameAction {
	
	Type type;
	Card card;
	
	public GameAction(Type type, Card... card) {
		this.type = type;
		if(type == Type.PLAY) {
			this.card = card[0];
		}
	}
	
	public enum Type {
		UNO, DRAW, PLAY, SKIP
	}
	
}