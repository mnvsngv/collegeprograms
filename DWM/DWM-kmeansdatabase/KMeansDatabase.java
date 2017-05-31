/*
	Program for K-Means Clustering using Postgres Database in Java
	Author: Manav Sanghavi	Author Link: https://www.facebook.com/manav.sanghavi
	www.pracspedia.com
	
	To run the file:
	javac -classpath postgresql.jar KMeansDatabase.java
	java -classpath postgresql.jar KMeansDatabase
*/

import java.util.*;
import java.sql.*;

class DataContainer{
	int key, value;
	String name;
}

class KMeansDatabase {
	static List<List <DataContainer>> cluster;
	static double mean[];
	static double prev_mean[];
	
	public static void main(String args[]) {
		Scanner scan = new Scanner(System.in);
		try {
			Class.forName("org.postgresql.Driver");
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Enter the name of the database to connect to:");
		String database = scan.nextLine();
		System.out.println("Enter the username:");
		String username = scan.nextLine();
		System.out.println("Enter the password:");
		String password = scan.nextLine();
		
		Connection con = null;
		Statement s = null;
		try {
			con = DriverManager.getConnection("jdbc:postgresql:" + database,
												username, password);
			s = con.createStatement();
		} catch(SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		ResultSet rs = null;
		String query = "SELECT id, price, name FROM kmeans";
		List<DataContainer> data = new LinkedList<>();
		try {
			rs = s.executeQuery(query);
			while(rs.next()) {
				DataContainer dc = new DataContainer();
				dc.key = Integer.parseInt(rs.getString(1));
				dc.value = Integer.parseInt(rs.getString(2));
				dc.name = rs.getString(3);
				data.add(dc);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("Enter the number of clusters:");
		int k = scan.nextInt();
		mean = new double[k];
		prev_mean = new double[k];
		cluster = new LinkedList<>();
		for(int i=0 ; i<k ; i++) {
			cluster.add(new LinkedList<DataContainer>());
			mean[i] = (double) data.get(i).value;
		}
		
		System.out.println("Initial Mean Values:");
		for(double i : mean) {
			System.out.println(i);
		}
		System.out.println("+++ START OF CLUSTERING +++\n\n");
		
		int iter = 0;
		do {
			iter++;
			System.out.println("\nITERATION " + iter + "\n");
			System.arraycopy(mean, 0, prev_mean, 0, mean.length);
			for(List<DataContainer> i : cluster) {
				i.clear();
			}
			for(DataContainer i : data) {
				cluster.get(getListNumber(i.value)).add(i);
			}
			calculateNewMeans();
			displayClusters();
		} while(!equalMeans());
		
		System.out.println("\n\n+++ FINAL ANSWER +++");
		int listno = 0;
		for(List<DataContainer> l1 : cluster) {
			System.out.println("Cluster no.: " + (listno+1));
			System.out.print("{ ");
			for(DataContainer i : l1) {
				System.out.print(i.name + " ");
			}
			System.out.println("}");
			listno++;
		}
	}
	
	static int getListNumber(int element) {
		// Here we calculate the Euclidean distance of 'element' from all 
		// values in 'mean' array.
		double least_dist = Double.MAX_VALUE;
		double new_dist = 0;
		int listno = 0;
		for(int i=0 ; i<mean.length ; i++) {
			new_dist = Math.abs(mean[i] - ((double) element));
			if(new_dist < least_dist) {
				least_dist = new_dist;
				listno = i;
			}
		}
		return listno;
	}
	
	static void calculateNewMeans() {
		// Here we update the 'mean' array to reflect the changes in the
		// clusters.
		int ctr = 0;
		for(List<DataContainer> l : cluster) {
			mean[ctr] = 0;
			for(DataContainer i : l) {
				mean[ctr] += i.value;
			}
			mean[ctr] /= l.size();
			ctr++;
		}
	}
	
	static boolean equalMeans() {
		// Here we compare the 'prev_mean' and 'mean' arrays to see if they
		// are equal or not.
		int j = 0;
		for(double i : mean) {
			if(prev_mean[j] != i) {
				return false;
			}
			j++;
		}
		return true;
	}
	
	static void displayClusters() {
		int listno = 0;
		for(List<DataContainer> l1 : cluster) {
			System.out.println("Cluster no.: " + (listno+1));
			System.out.print("{ ");
			for(DataContainer i : l1) {
				System.out.print(i.value + " ");
			}
			System.out.println("}");
			System.out.println("--- Cluster Mean = " + mean[listno] + " ---");
			listno++;
		}
	}
}

/*
SQL Queries for Database creation:

CREATE TABLE kmeans(id int primary key, name varchar(50), price int);

INSERT INTO kmeans VALUES(1, 'Manav', 53);
INSERT INTO kmeans VALUES(2, 'Mahek', 68);
INSERT INTO kmeans VALUES(3, 'Kunal', 15);
INSERT INTO kmeans VALUES(4, 'Ojus', 65);
INSERT INTO kmeans VALUES(5, 'Madhur', 38);
INSERT INTO kmeans VALUES(6, 'Nishant', 62);

*/