package com.example.lux.mapsapp;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
	static int id;
	static int Ktop;
	static String category;
	
	public Client(int id, int Ktop, String category) {
		this.id = id;
		this.Ktop = Ktop;
		this.category = category;
	}
	
	public static void main(String args[]) {
		Scanner scan = new Scanner(System.in);
		//com.example.lux.mapsapp.Client simply enters his ID and the amount of POIs he wants in return
		System.out.print("Insert User ID: ");
		int x = scan.nextInt();
		System.out.print("Insert Number of Requested POIs: ");
		int y = scan.nextInt();
		System.out.print("Insert Category: ");
		scan.nextLine();
		String z = scan.nextLine();
		Client client = new Client(x,y,z);
		System.out.print("Insert com.example.lux.mapsapp.Server IP: ");
		String connectToIP = scan.nextLine();
		System.out.print("Insert com.example.lux.mapsapp.Server Port: ");
		int port = scan.nextInt();
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		Socket requestSocket = null;
		try {
			//com.example.lux.mapsapp.Client then has to connect to the server
			requestSocket = new Socket(connectToIP,port);

			out = new ObjectOutputStream(requestSocket.getOutputStream());
			in = new ObjectInputStream(requestSocket.getInputStream());
			//com.example.lux.mapsapp.Client sends his id, K and category
			out.writeInt(id);
			out.flush();
			out.writeInt(Ktop);
			out.flush();

			//Read the amount of POIs the server is about to return

			System.out.println("Results:");
			//Finally he receives the results from the server
			for(int i=0;i<Ktop;i++) {
				System.out.println((i+1)+")");
				//Print the returned com.example.lux.mapsapp.POI's data
				try {
					((POI)in.readObject()).print();
				} catch(ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
				requestSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
}