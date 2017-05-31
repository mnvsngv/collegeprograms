/* 
	Data Transformation - Decoding of Data from Excel - Java Program
	Author: Manav Sanghavi		Author Link: https://www.facebook.com/manav.sanghavi 
	www.pracspedia.com
	
	MAKE SURE YOU HAVE MADE THE EXCEL ODBC DATA SOURCE!
*/
import java.util.*;
import java.sql.*;

class Decoding {
	public static void main(String args[]) throws Exception {
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		Connection con = DriverManager.getConnection("jdbc:odbc:ExcelDatabase");
		Statement st = con.createStatement();
		String query = "SELECT * FROM [sheet1$]";
		ResultSet rs = st.executeQuery(query);
		System.out.println("Before Decoding:");
		List<String> l = new LinkedList<>();
		String s;
		String s1 =  new String();
		String s2 =  new String();
		String s3 =  new String();
		String s4 =  new String();
		String s5 =  new String();
		while(rs.next()) {
			s1 = rs.getString(1);
			s2 = rs.getString(2);
			s3 = rs.getString(3);
			s4 = rs.getString(4);
			s5 = rs.getString(5);
			s = s1 + "\t" + s2 + "\t" + s3 + "\t" + s4 + "\t" + s5;
			System.out.println(s);
			s = s1 + "\t" + s2 + "\t" + decode(s3) + "\t" + s4 + "\t" + s5;
			l.add(s);
		}
		System.out.println("After Decoding:");
		for(String i : l) {
			System.out.println(i);
		}
	}
	
	static String decode(String s) {
		switch(s) {
			case "Mon":
				return "MONDAY";
				
			case "Tues":
				return "TUESDAY";
				
			case "Wed":
				return "WEDNESDAY";
				
			case "Thurs":
				return "THURSDAY";
				
			case "Fri":
				return "FRIDAY";
				
			case "Sat":
				return "SATURDAY";
				
			case "Sun":
				return "SUNDAY";
		}
		return null;
	}
}

/*

OUTPUT:

Before Decoding:
101.0   19.0    Mon             May     1993.0
102.0   20.0    Tues            Jan     1990.0
103.0   25.0    Wed             Oct     2005.0
104.0   30.0    Sat             Sept    2001.0
105.0   17.0    Sun             Aug     2014.0
After Decoding:
101.0   19.0    MONDAY          May     1993.0
102.0   20.0    TUESDAY         Jan     1990.0
103.0   25.0    WEDNESDAY       Oct     2005.0
104.0   30.0    SATURDAY        Sept    2001.0
105.0   17.0    SUNDAY          Aug     2014.0

*/