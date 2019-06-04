package com.example.lux.mapsapp;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

//Thread that execute the recommendation part of the server
public class ServerClientThread extends Thread {
	ObjectInputStream in;
    ObjectOutputStream out;
	//Connection between server and the client
	Socket connection;
	//The matrices for X and Y
	RealMatrix L;
	RealMatrix R;
	POI[] pois;
	int rowsUsers, columnsPOIs, K;

	private int[][] matrix;
	final String filename = "F:\\Users\\Lux\\Desktop\\Ergn\\src\\main\\java\\data\\input_matrix_non_zeros.csv"; //Set it manually!


	public ServerClientThread(Socket connection, RealMatrix L, RealMatrix R, POI[] pois, int rowsUsers, int columnsPOIs, int K) {
		this.connection = connection;
		this.L = L;
		this.R = R;
		this.pois = pois;
		this.rowsUsers = rowsUsers;
		this.columnsPOIs = columnsPOIs;
		readDataSetMatrix();
		this.K = K;
		try {
			out = new ObjectOutputStream(connection.getOutputStream());
			in = new ObjectInputStream(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			//The thread gets the user's id and how many POIs he wants
            int userID = in.readInt();
            int Ktop = in.readInt();
            String category = "";
			try {
				category = (String)in.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			System.out.println("User #"+userID+" requested the top "+Ktop+" POIs for the category "+category);
            //The thread uses the function to calculate the result
            ArrayList<Integer> results = recommend(userID,Ktop,category);
            //In case we don't have the required amount we only return the ones that were found
            Ktop = results.size();
            System.out.println("The new array has size->"+Ktop);
            out.writeInt(Ktop);
            out.flush();
            //Finally the thread returns the K top POIs that the user requested
            for(int i=0;i<Ktop;i++) {
                out.writeObject(pois[results.get(i)]);
			    out.flush();
            }
			System.out.println("User #"+userID+" has received the results");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
	
	//Recommends the num most suitable places for the user to visit
	public ArrayList<Integer> recommend(int user, int num, String category) {
		double[] P = new double[columnsPOIs];
		RealMatrix Xu = MatrixUtils.createRealMatrix(getXu(user));
		for(int j=0;j<columnsPOIs;j++) {
			RealMatrix Yi = MatrixUtils.createRealMatrix(getYi(j));
			P[j] = (Xu.multiply(Yi.transpose())).getEntry(0,0);
		}
		ArrayList<Integer> recommendations = new ArrayList<Integer>();
		double max = -999;
		int pointer = -1;
		for(int i=0;i<num;i++) {
			for(int j=0;j<columnsPOIs;j++) {
				if(P[j]>max) {
					if(category.equals("") && (!hasVisited(user,j))) {
						max = P[j];
						pointer = j;
					}
					else if(category.equals(pois[j].getCategory()) && (!hasVisited(user,j)))
					{
						max = P[j];
						pointer=j;
					}
				}
			}
			if(max==-999) break;
			recommendations.add(pointer);
			P[pointer] = -1;
			pointer = -1;
			max = -999;
		}
		return recommendations;
	}

	//Method taken from com.example.lux.mapsapp.Server.java
	//Returns Xu
	public double[][] getXu(int i) {
		double[][] arr = new double[1][K];
		for (int j=0;j<K;j++) {
			arr[0][j] = R.getEntry(i,j);
		}
		return arr;
	}

	//Method taken from com.example.lux.mapsapp.Server.java
	//Returns Yi
	public double[][] getYi(int i) {
		double[][] arr = new double[1][K];
		for (int j=0;j<K;j++) {
			arr[0][j] = L.getEntry(i,j);
		}
		return arr;
	}

	//Method to read the data set from the csv file
	public void readDataSetMatrix() {
		matrix=new int[rowsUsers][columnsPOIs];
		for(int i=0;i<rowsUsers;i++)
		{
			for(int j=0;j<columnsPOIs;j++)
			{
				matrix[i][j]=0;
			}
		}
		String line;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			while((line = br.readLine()) != null) {
				String[] words = line.split(", ");
				matrix[Integer.parseInt(words[0])][Integer.parseInt(words[1])] = Integer.parseInt(words[2]);
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean hasVisited(int user,int poi)
	{
		if(matrix[user][poi]!=0) return true;
		else return false;
	}
}