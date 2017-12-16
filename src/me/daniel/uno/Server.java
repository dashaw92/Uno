package me.daniel.uno;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import me.daniel.uno.objects.Game;

public class Server implements Runnable {
	
	private static int port = 1312; //because 13 - 12 is Uno!
	private static int slots = 4;
	private static List<Player> players = new ArrayList<>();
	
	public static void main(String[] args) {
		if(args.length > 1) {
			try {
				port = Integer.parseInt(args[0].trim());
			} catch(NumberFormatException e) {
				System.err.printf("Invalid port argument, using default port %d.\n", port);
			}
		}
		Scanner scanner = new Scanner(System.in);
		System.out.print("Please enter the number of players who will play (2 to 10): ");
		slots = scanner.nextInt();
		scanner.close();
		if(slots > 10)
			slots = 10;
		else if(slots < 2)
			slots = 2;
		System.out.printf("Starting server for %d players...\n", slots);
		new Thread(new Server()).start();
	}
	
	@Override
	public void run() {
		try(ServerSocket server = new ServerSocket(port)) {
			while(players.size() < slots) {
				Socket client = server.accept();
				handle(client);
			}
		} catch (IOException e) {
			System.err.println("An error occurred during the main loop.");
			e.printStackTrace();
		}
		broadcast("Enough players have joined, let's play!\n\r");
		Game game = new Game(players);
		while(game.isPlaying()) {
			game.turn();
		}
		broadcast("Thanks for playing! Good bye!\n\r");
		players.forEach(n -> n.disconnect());
	}
	
	public static void broadcast(String msg) {
		System.out.println(msg);
		players.forEach(n -> n.sendString(msg));
	}
	
	private void handle(Socket client) {
		byte[] nick_buf = new byte[16];
		try {
			client.getInputStream().read(nick_buf, 0, 16);
		} catch (IOException e) {}
		String nick = new String(nick_buf).replaceAll("[^a-zA-Z0-9]", "").trim();
		if(nick.length() > 16) {
			nick = nick.substring(0, 15);
		} else if(nick.isEmpty()) {
			nick = "Player" + (int)(Math.random() * 100);
		}
		players.add(new Player(client, nick, players.size() + 1));
		broadcast(String.format("Player %s has joined the game.\n\r", nick));
	}
}