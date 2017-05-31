/* 
	Data Transformation - De-duplication of Data from Access Java Program
	Author: Manav Sanghavi		Author Link: https://www.facebook.com/manav.sanghavi 
	www.pracspedia.com
	
	MAKE SURE YOU HAVE MADE THE ACCESS ODBC DATA SOURCE!
*/
import java.util.*;
import java.sql.*;

class Deduplication {
	public static void main(String args[]) throws Exception {
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		Connection con = DriverManager.getConnection("jdbc:odbc:AccessDatabase");
		Statement s = con.createStatement();
		String query = "SELECT * FROM deduplication";
		ResultSet rs = s.executeQuery(query);
		SortedMap<String, String> m = new TreeMap<>();
		String s1 = new String();
		String s2 = new String();
		System.out.println("Before deduplication:");
		while(rs.next()) {
			s1 = rs.getString(1);
			s2 = rs.getString(2);
			System.out.println(s1 + "\t" + s2);
			if(!m.containsValue(s2)) {
				m.put(s1, s2);
			}
		}
		System.out.println("After deduplication:");
		Iterator<String> itr = m.keySet().iterator();
		while(itr.hasNext()) {
			String x = itr.next();
			System.out.println(x + "\t" + m.get(x));
		}
	}
}

/*

OUTPUT:

Before deduplication:
1       Manav
2       Tony
3       Steve
4       Bruce
5       Nick
6       Manav
7       Steve
8       Nick
After deduplication:
1       Manav
2       Tony
3       Steve
4       Bruce
5       Nick

*/