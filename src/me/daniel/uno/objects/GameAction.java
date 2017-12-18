package me.daniel.uno.objects;

import me.daniel.uno.util.Formatter;

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
		UNO, DRAW, PLAY, SKIP, CHAT, DISCONNECT;
		
		String[] message = new String[1];
		
		public void setMsg(String msg) {
			if(this.equals(CHAT)) {
				message[0] = Formatter.format(msg);
			}
		}
		
		public String getMsg() {
			if(this.equals(CHAT)) {
				return message[0];
			} else {
				return "";
			}
		}
	}
	
}