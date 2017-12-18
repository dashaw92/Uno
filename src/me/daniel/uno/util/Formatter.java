package me.daniel.uno.util;

public class Formatter {
	
	public static String format(String input) {
//		return input
//				.replace("&rev", "&w\033[07m")
//				.replace("&r", "\033[91m")
//				.replace("&g", "\033[92m")
//				.replace("&y", "\033[93m")
//				.replace("&b", "\033[94m")
//				.replace("&p", "\033[95m")
//				.replace("&c", "\033[96m")
//				.replace("&w", "\033[00m")
//				.replace("&u", "\033[04m");
		return input
				.replace("&rev", "&w\033[07m")
				.replace("&R", "&w\033[41;37;01m")
				.replace("&r", "\033[31m")
				.replace("&g", "\033[32m")
				.replace("&y", "\033[33m")
				.replace("&b", "\033[34m")
				.replace("&p", "\033[35m")
				.replace("&c", "\033[36m")
				.replace("&u", "\033[04m")
				.replace("&w", "\033[00;00;00m");
	}
	
	public static String clear() {
		return "\033[2J\033[1;1H";
	}
}