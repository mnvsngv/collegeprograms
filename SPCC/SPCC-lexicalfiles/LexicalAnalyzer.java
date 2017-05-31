/*

Lexical Analyzer to identify a C program's:
keywords
functions
special symbols
operators

*/

import java.util.*;
import java.io.*;

class LexicalAnalyzer {
	static List<String> keywords;
	static List<String> functions;
	static List<String> special_symbols;
	static List<String> operators;
	static List<String> identifiers;
	static List<Integer> literals;
	static PrintWriter pw;

	public static void main(String args[]) throws Exception {
		initializeTables();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("input.txt"))));
		pw = new PrintWriter(new FileOutputStream(new File("output.txt")), true);
		String s = new String();
		while((s = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(s, " ,+=-*%/#<>;", true);
			while(st.hasMoreTokens()) {
				printToken(st.nextToken());
			}
			pw.println();
		}
		
		pw = new PrintWriter(new FileOutputStream(new File("output_identifier.txt")), true);
		for(String i : identifiers) {
			pw.println(i);
		}
		pw = new PrintWriter(new FileOutputStream(new File("output_literal.txt")), true);
		for(Integer i : literals) {
			pw.println(i);
		}
	}

	static void printToken(String s) {
		if(!s.equals(" ")) {
			String x = getTokenType(s);
			pw.print(x+" ");
		}
	}

	static String getTokenType(String s) {
		int index = 0;
		if((index = Collections.binarySearch(keywords, s)) >= 0) {
			return ("KEY#" + index);
		}
		if((index = Collections.binarySearch(functions, s)) >= 0) {
			return ("FUN#" + index);
		}
		if((index = Collections.binarySearch(operators, s)) >= 0) {
			return ("OPR#" + index);
		}
		if((index = Collections.binarySearch(special_symbols, s)) >= 0) {
			return ("SPE#" + index);
		}
		int ctr = 0;
		for(String i : identifiers) {
			if(i.equalsIgnoreCase(s)) {
				return ("SYM#" + ctr);
			}
			ctr++;
		}
		
		try {
			int lit = Integer.parseInt(s);
			literals.add(lit);
			return ("LIT#"+(literals.size()-1));
		} catch(NumberFormatException e) {
			identifiers.add(s);
		}
		
		return ("SYM#"+(identifiers.size() - 1));
	}
	
	static void initializeTables() throws Exception {
		keywords = new LinkedList<>();
		functions = new LinkedList<>();
		operators = new LinkedList<>();
		special_symbols = new LinkedList<>();
		identifiers = new LinkedList<>();
		literals = new LinkedList<>();
		
		BufferedReader br;
		br = new BufferedReader(new InputStreamReader(new FileInputStream("keywords.txt")));
		System.out.println("Keywords:");
		readIntoList(keywords, br);
		br = new BufferedReader(new InputStreamReader(new FileInputStream("functions.txt")));
		System.out.println("\nFunctions:");
		readIntoList(functions, br);
		br = new BufferedReader(new InputStreamReader(new FileInputStream("operators.txt")));
		System.out.println("\nOperators:");
		readIntoList(operators, br);
		br = new BufferedReader(new InputStreamReader(new FileInputStream("special_symbols.txt")));
		System.out.println("\nSpecial Symbols:");
		readIntoList(special_symbols, br);
	}
	
	static void readIntoList(List<String> l, BufferedReader br) throws Exception {
		String s;
		while((s = br.readLine()) != null) {
			l.add(s);
		}
		Collections.sort(l);
		int ctr = 0;
		for(String i : l) {
			System.out.println((ctr++) + ".) " + i);
		}
	}
}
