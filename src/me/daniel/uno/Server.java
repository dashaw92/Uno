package me.daniel.uno;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import me.daniel.uno.objects.Game;
import me.daniel.uno.util.Formatter;

public class Server implements Runnable {
	
	private static int port = 1312; //because 13 - 12 is Uno!
	private static int slots = 4;
	private static List<Player> players = new ArrayList<>();
	private static List<Socket> spectators = new ArrayList<>();
	
	public static void main(String[] args) {
		if(args.length >= 1) {
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
		new Thread(new Runnable() {

			@Override
			public void run() {
				try(ServerSocket server = new ServerSocket(port)) {
					while(game.isPlaying()) {
						Socket spect = server.accept();
						spect.getOutputStream().write(Formatter.format("The server is full, so you will only be able to spectate.\r\n&u                         &w\r\n").getBytes());
						spectators.add(spect);
					}
				} catch (IOException e) {}
			}
			
		}).start();
		while(game.isPlaying()) {
			game.turn();
		}
		broadcast("Thanks for playing! Good bye!\n\r");
		players.forEach(n -> n.disconnect());
		spectators.forEach(n -> {
			try {
				n.close();
			} catch(IOException e) {}
		});
		System.exit(0);
	}
	
	public static void broadcast(String msg) {
		System.out.print(Formatter.format(msg));
		players.forEach(n -> n.sendString(msg));
		spectators.forEach(n -> {
			try {
				n.getOutputStream().write(Formatter.format(msg).getBytes());
			} catch(IOException e) {
				return;
			}
		});
	}
	
	private void handle(Socket client) {
		try {
			client.setSoTimeout(10_000);
		} catch (SocketException e) {}
		byte[] nick_buf = new byte[16];
		try {
			client.getOutputStream().write("Please enter a nickname: ".getBytes());
			client.getInputStream().read(nick_buf, 0, 16);
		} catch (IOException e) {
			try {
				client.getOutputStream().write("\n\rYou have been disconnected for idling.\n\r".getBytes());
				client.close();
			} catch (IOException e1) {}
			return;
		}
		String nick = new String(nick_buf).replaceAll("[^a-zA-Z0-9]", "").trim();
		if(nick.length() > 16) {
			nick = nick.substring(0, 15);
		} else if(nick.isEmpty()) {
			nick = "Player" + (int)(Math.random() * 100);
		}
		try {
			client.setSoTimeout(0);
		} catch(IOException e) {}
		players.add(new Player(client, nick, players.size() + 1));
		broadcast(String.format("&yPlayer %s has joined the game.&w\n\r", nick));
	}
}