package me.daniel.uno.util;

public class Formatter {
	
	public static String format(String input) {
		return input
				.replace("&rev", "&w\033[07m")
				.replace("&r", "\033[91m")
				.replace("&g", "\033[92m")
				.replace("&y", "\033[93m")
				.replace("&b", "\033[94m")
				.replace("&p", "\033[95m")
				.replace("&c", "\033[96m")
				.replace("&w", "\033[00m")
				.replace("&u", "\033[04m");
	}
	
	public static String clear() {
		return "\033[2J\033[1;1H";
	}
}