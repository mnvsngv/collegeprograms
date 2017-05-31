/*
  	Data Transformation - Key-Restructuring from Text File - Java Program
  	Author: Manav Sanghavi		Author Link: https://www.facebook.com/manav.sanghavi 
	www.pracspedia.com
  
	MAKE SURE YOU HAVE MADE THE TEXT ODBC DATA SOURCE!
*/

import java.util.*;
import java.sql.*;

class KeyRestructuring {
	public static void main(String args[]) throws Exception {
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		Connection con = DriverManager.getConnection("jdbc:odbc:TextDatabase");
		Statement s = con.createStatement();
		String query = "SELECT * FROM db.txt";
		ResultSet rs = s.executeQuery(query);
		String x;
		String s1 = new String();
		String s2 = new String();
		String s3 = new String();
		List<String> l = new LinkedList<>();
		System.out.println("Before Key Restructuring:");
		while(rs.next()) {
			s1 = rs.getString(1);
			s2 = rs.getString(2);
			s3 = rs.getString(3);
			x = s2 + "\t" + s3;
			System.out.println(x);
			x = (Integer.parseInt(s1) + 1000) + "\t" + s2 + "\t" + s3;
			l.add(x);
		}
		System.out.println("After Key Restructuring:");
		for(String i : l) {
			System.out.println(i);
		}
	}
}

/*

OUTPUT:

Before Key Restructuring:
Manav   Sanghavi
Tony    Stark
Steve   Rogers
Bruce   Banner
Nick    Fury
After Key Restructuring:
1001    Manav   Sanghavi
1002    Tony    Stark
1003    Steve   Rogers
1004    Bruce   Banner
1005    Nick    Fury

*/