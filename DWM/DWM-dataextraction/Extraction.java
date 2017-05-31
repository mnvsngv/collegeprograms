/*
	Java Program for Extraction of data from SQL Database, Microsoft Access, Microsoft Excel and Text File
	Author: Manav Sanghavi		Author Link: https://www.facebook.com/manav.sanghavi 
	www.pracspedia.com
	
	Prerequisite data:
	
	MAKE SURE YOU HAVE MADE ODBC DATA SOURCES OF ALL 4 TYPES!
	
	1.) Database Extraction SQL Queries:
	
	CREATE TABLE extraction(first_name varchar(50), last_name varchar(50));
	
	INSERT INTO extraction VALUES('Manav', 'Sanghavi');
	INSERT INTO extraction VALUES('Tony', 'Stark');
	INSERT INTO extraction VALUES('Steve', 'Rogers');
	INSERT INTO extraction VALUES('Bruce', 'Banner');
	INSERT INTO extraction VALUES('Nick', 'Fury');
	
	2.) Text in db.txt:
	
	1, "Manav", "Sanghavi"
	2, "Tony", "Stark"
	3, "Steve", "Rogers"
	4, "Bruce", "Banner"
	5, "Nick", "Fury"
	
	NOTE: make sure filename of text file is not an SQL keyword.
	
	3.) Excel ODBC does not return first row, as it interprets that as the column names. So make sure that the first row of your excel sheet contains column names.
	
	4.) Make the Access (.mdb) file via the ODBC screen, if you get errors in creating the Data Source.

*/

import java.sql.*;

class Extraction {
	public static void main(String args[]) throws Exception {
		databaseExtraction();
		textExtraction();
		excelExtraction();
		accessExtraction();
	}
	
	static void databaseExtraction() throws Exception {
		System.out.println("-+= DATABASE EXTRACTION =+-");
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		Connection con = DriverManager.getConnection("jdbc:odbc:DWM");
		Statement s = con.createStatement();
		String query = "SELECT * FROM extraction";
		ResultSet rs = s.executeQuery(query);
		while(rs.next()) {
			System.out.println(rs.getString(1) + " \t" + rs.getString(2));
		}
		System.out.println();
	}
	
	static void textExtraction() throws Exception {
		System.out.println("-+= TEXT EXTRACTION =+-");
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		Connection con = DriverManager.getConnection("jdbc:odbc:TextDatabase");
		Statement s = con.createStatement();
		String query = "SELECT * FROM db.txt";
		ResultSet rs = s.executeQuery(query);
		while(rs.next()) {
			System.out.println(rs.getString(2) + "\t" + rs.getString(3));
		}
		System.out.println();
	}
	
	static void excelExtraction() throws Exception {
		System.out.println("-+= EXCEL EXTRACTION =+-");
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		Connection con = DriverManager.getConnection("jdbc:odbc:ExcelDatabase");
		Statement s = con.createStatement();
		String query = "SELECT * FROM [sheet1$]";
		ResultSet rs = s.executeQuery(query);
		while(rs.next()) {
			System.out.println(rs.getString(1) + " \t" + rs.getString(2));
		}
		System.out.println();
	}
	
	static void accessExtraction() throws Exception {
		System.out.println("-+= ACCESS EXTRACTION =+-");
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		Connection con = DriverManager.getConnection("jdbc:odbc:AccessDatabase");
		Statement s = con.createStatement();
		String query = "SELECT * FROM extraction";
		ResultSet rs = s.executeQuery(query);
		while(rs.next()) {
			System.out.println(rs.getString(1) + " \t" + rs.getString(2));
		}
		System.out.println();
	}	
}

/*

OUTPUT:

-+= DATABASE EXTRACTION =+-
Manav   Sanghavi
Tony    Stark
Steve   Rogers
Bruce   Banner
Nick    Fury

-+= TEXT EXTRACTION =+-
Manav   Sanghavi
Tony    Stark
Steve   Rogers
Bruce   Banner
Nick    Fury

-+= EXCEL EXTRACTION =+-
Manav   Sanghavi
Tony    Stark
Steve   Rogers
Bruce   Banner
Nick    Fury

-+= ACCESS EXTRACTION =+-
Manav   Sanghavi
Tony    Stark
Steve   Rogers
Bruce   Banner
Nick    Fury

*/