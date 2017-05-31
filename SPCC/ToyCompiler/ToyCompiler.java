import java.util.*;
import java.util.regex.*;
import java.io.*;

class ToyCompiler {
	static String input;
	static List<String> tokenList;
	static String[] tokens;
	static int ptr;
	static List<String> floatVariables;
	static List<String> intVariables;
	static Map<Integer, String> variableMap;
	static int expressionType;
	static int ctr;
	
	public static void main(String args[]) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt")));
		floatVariables = new LinkedList<>();
		intVariables = new LinkedList<>();
		variableMap = new HashMap<>();
		ctr = 0;
		while((input = br.readLine()) != null) {
			System.out.println(input);
			lexicalAnalysis();
			syntacticAnalysis();
			semanticAnalysis();
		}
	}
	
	static void lexicalAnalysis() {
		/*
		tokenList:
		KEY :: keywords (int, float)
		IDE :: identifier/variable of type float or int
		OPR :: operator (=,+,-,*,/)
		SYM :: symbols ('(', ')', ';', '{', '}', ',')
		CON :: numeric constant
		*/
		tokenList = new LinkedList<>();
		StringTokenizer st = new StringTokenizer(input, " =+-*/(){},;", true);
		String s = new String();
		while(st.hasMoreTokens()) {
			s = st.nextToken();
			if(s.equals(" ")) {
				continue;
			}
			if(!isKeyword(s)) {
				if(!isOperator(s)) {
					if(!isSymbol(s)) {
						try {
							// Check if token is a constant:
							int x = Integer.parseInt(s);
							tokenList.add("CON1");
						} catch(NumberFormatException e1) {
							try {
								float y = Float.parseFloat(s);
								tokenList.add("CON2");
							} catch(NumberFormatException e2) {
								if(Pattern.matches("[a-zA-Z]+", s)) {
									// valid identifier
									if(variableMap.containsValue(s)) {
										Iterator<Integer> iterator = variableMap.keySet().iterator();
										while(iterator.hasNext()) {
											int x = iterator.next();
											if(variableMap.get(x).equals(s)) {
												tokenList.add("IDE" + x);
											}
										}
									} else {
										tokenList.add("IDE" + (++ctr));
										variableMap.put(ctr, s);
									}
								} else {
									System.out.println("Invalid token: '" + s + "'");
									System.exit(0);
								}
							}
						}
					}
				}
			}
		}
	}
	
	static boolean isKeyword(String s) {
		if(s.equals("int")) {
			tokenList.add("KEY1");
			return true;
		}
		if(s.equals("float")) {
			tokenList.add("KEY2");
			return true;
		}
		return false;
	}
	
	static boolean isOperator(String s) {
		if(s.equals("=")) {
			tokenList.add("OPR1");
			return true;
		}
		if(s.equals("+")) {
			tokenList.add("OPR2");
			return true;
		}
		if(s.equals("-")) {
			tokenList.add("OPR3");
			return true;
		}
		if(s.equals("*")) {
			tokenList.add("OPR4");
			return true;
		}
		if(s.equals("/")) {
			tokenList.add("OPR5");
			return true;
		}
		return false;
	}
	
	static boolean isSymbol(String s) {
		if(s.equals("(")) {
			tokenList.add("SYM1");
			return true;
		}
		if(s.equals(")")) {
			tokenList.add("SYM2");
			return true;
		}
		if(s.equals(";")) {
			tokenList.add("SYM3");
			return true;
		}
		if(s.equals("{")) {
			tokenList.add("SYM4");
			return true;
		}
		if(s.equals("}")) {
			tokenList.add("SYM5");
			return true;
		}
		if(s.equals(",")) {
			tokenList.add("SYM6");
			return true;
		}
		return false;
	}
	
	static void syntacticAnalysis() {
		/*
		Grammar:
		line -> SYM4 declaration SYM5
		line -> KEY1 IDE OPR1 expression SYM3
		line -> KEY2 IDE OPR1 expression SYM3
		declaration -> KEY1 identifiers
		declaration -> KEY2 identifiers
		identifiers -> IDE
		identifiers -> IDE SYM6 identifiers
		expression -> IDE
		expression -> CON1
		expression -> CON2
		expression -> expression OPR2 expression
		expression -> expression OPR3 expression
		expression -> expression OPR4 expression
		expression -> expression OPR5 expression
		expression -> SYM1 expression SYM2
		expression -> SYM1 expression SYM2 OPR2 expression
		expression -> SYM1 expression SYM2 OPR3 expression
		expression -> SYM1 expression SYM2 OPR4 expression
		expression -> SYM1 expression SYM2 OPR5 expression
		terminal -> IDE
		terminal -> CON1
		terminal -> CON2
		*/
		ptr = 0;
		tokens = tokenList.toArray(new String[0]);
		if(line() == true) {
			System.out.println("Valid syntax.");
		} else {
			System.out.println("Invalid syntax.");
			System.exit(0);
		}
	}
	
	static boolean line() {
		/*
		Grammar:
		line -> SYM4 declaration SYM5
		line -> KEY1 IDE OPR1 expression SYM3
		line -> KEY2 IDE OPR1 expression SYM3
		*/
		expressionType = 0;
		if((!tokens[ptr].equals("KEY1")) && (!tokens[ptr].equals("KEY2"))) {
			if(!tokens[ptr].equals("SYM4")) {
				return false;
			} else {
				ptr++;
				if(declaration() == false) {
					return false;
				}
				if(!tokens[ptr++].equals("SYM5")) {
					return false;
				}
				return true;
			}
		}
		ptr++;
		if(!tokens[ptr++].contains("IDE")) {
			return false;
		}
		if(!tokens[ptr++].equals("OPR1")) {
			return false;
		}
		if(expression() == false) {
			return false;
		}
		try {
			if(!tokens[ptr++].equals("SYM3")) {
				return false;
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Missing semicolon.");
			return false;
		}
		return true;
	}
	
	static boolean declaration() {
		/*
		Grammar:
		declaration -> KEY1 identifiers
		declaration -> KEY2 identifiers
		*/
		
		if((!tokens[ptr].equals("KEY1")) && (!tokens[ptr].equals("KEY2"))) {
			return false;
		}
		ptr++;
		if(identifiers(Integer.parseInt(tokens[ptr-1].substring(tokens[ptr].length()-1, tokens[ptr].length()))) == false) {
			return false;
		}
		return true;
	}
	
	static boolean identifiers(int type) {
		/*
		Grammar:
		identifiers -> IDE
		identifiers -> IDE SYM6 identifiers
		*/
		if(tokens[ptr].contains("IDE")) {
			if(type == 1) {
				intVariables.add(variableMap.get(Integer.parseInt(tokens[ptr].substring(tokens[ptr].length()-1, tokens[ptr].length()))));
			} else {
				floatVariables.add(variableMap.get(Integer.parseInt(tokens[ptr].substring(tokens[ptr].length()-1, tokens[ptr].length()))));
				
			}
			ptr++;
			int fallback = ptr;
			if(tokens[ptr++].equals("SYM6")) {
				if(identifiers(type) == true) {
					return true;
				}
			} else {
				ptr = fallback;
			}
			return true;
		}
		return false;
	}
	
	static boolean expression() {
		/*
		Grammar:
		expression -> terminal
		expression -> terminal OPR2 expression
		expression -> terminal OPR3 expression
		expression -> terminal OPR4 expression
		expression -> terminal OPR5 expression
		expression -> SYM1 expression SYM2
		expression -> SYM1 expression SYM2 OPR2 expression
		expression -> SYM1 expression SYM2 OPR3 expression
		expression -> SYM1 expression SYM2 OPR4 expression
		expression -> SYM1 expression SYM2 OPR5 expression
		*/
		int fallback = ptr;
		if(terminal()) {
			if(arithmeticOperator()) {
				if(expression()) {
					return true;
				}
			}
			return true;
		} else {
			if(tokens[ptr++].equals("SYM1")) {
				if(expression()) {
					if(tokens[ptr++].equals("SYM2")) {
						if(arithmeticOperator()) {
							if(expression()) {
								return true;
							}
						}
						return true;
					}
				}
			} else {
				ptr = fallback;
			}
		}
		return false;
	}
	
	static boolean terminal() {
		/*
		Grammar:
		terminal -> IDE
		terminal -> CON1
		terminal -> CON2
		*/
		if(tokens[ptr].contains("IDE")) {
			String variable = variableMap.get(Integer.parseInt(tokens[ptr].substring(tokens[ptr].length()-1, tokens[ptr].length())));
			if((intVariables.contains(variable)) && (expressionType <= 1)) {
				expressionType = 1;
			} else {
				if(expressionType == 1) {
					System.out.println("Type casting to float.");
				}
				expressionType = 2;
			}
			ptr++;
			return true;
		} else if(tokens[ptr].equals("CON1")) {
			ptr++;
			if(expressionType < 1) {
				expressionType = 1;
			}
			return true;
		} else if(tokens[ptr].equals("CON2")) {
			ptr++;
			if(expressionType == 1) {
				System.out.println("Type casting to float.");
			}
			expressionType = 2;
			return true;
		}
		return false;
	}
	
	static boolean arithmeticOperator() {
		if(tokens[ptr].equals("OPR2")) {
			ptr++;
			return true;
		}
		if(tokens[ptr].equals("OPR3")) {
			ptr++;
			return true;
		}
		if(tokens[ptr].equals("OPR4")) {
			ptr++;
			return true;
		}
		if(tokens[ptr].equals("OPR5")) {
			ptr++;
			return true;
		}
		return false;
	}
	
	static void semanticAnalysis() {
		if(tokens[0].contains("KEY")) {
			int type = Integer.parseInt(tokens[0].substring(tokens[0].length()-1, tokens[0].length()));
			String s1, s2;
			if(type == 1) {
				s1 = "int";
			} else {
				s1 = "float";
			}
			if(expressionType == 1) {
				s2 = "int";
			} else {
				s2 = "float";
			}
			System.out.println("We are trying to assign " + s2 + " to " + s1 + ".");
			if(type == expressionType) {
				System.out.println("This is valid.");
			} else if(type < expressionType) {
				System.out.println("This is invalid.");
			} else {
				System.out.println("We will typecast " + s2 + " to " + s1 + ".");
			}
		}
	}
}

/*
OUTPUT:

{ int a,b }
Valid syntax.
int x = (a/10.0)+(b/10);
Type casting to float.
Valid syntax.
We are trying to assign float to int.
This is invalid.

*/