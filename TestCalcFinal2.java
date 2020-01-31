package com.idealgen.solution.calculator;

import java.util.ArrayList;
import java.util.List;
import java.lang.Character;

public class TestCalcFinal2 {

	public static void main(String[] args) {
		System.out.println(calculate("(23 - 16.8 + (10+5 + (11 + 6)) + 17 + (1+2)) + (3+4 * 4) * 6 + (11/ 11) / 7"));//172.34285714285713	
		System.out.println(calculate("23 - 16.8"));
		System.out.println(calculate("1 + 1"));
		System.out.println(calculate("2 * 2"));
		System.out.println(calculate("1 + 2 + 3"));
		System.out.println(calculate("6/2"));
		System.out.println(calculate("11+23"));
		System.out.println(calculate("11.1 + 23"));
		System.out.println(calculate("1+1*3"));
		System.out.println(calculate("(11 + 11)  / (11 + 11) "));
		System.out.println(calculate("(11.5+15.4)+10.1"));
		System.out.println(calculate("23 - ( 29.3 - 12.5 )"));
	}

	public static double calculate(String sum) {

		int bracketCounter = 0;
		sum = sum.replace(" ",""); // Remove white spaces
		List<Token> tokenList = new ArrayList<Token>();//Stores Numeric And bracketed Tokens
		List<Character> characterList = new ArrayList<Character>();//Stores Operator tokens
		
		String expression = "";
		Token currentToken = null;
		String tempDigit = "";

		//Seperating Tokens from String. Any Brackated Value is a token. Numerics are also
		//Tokens. Operators involving outer most value is stored in different Array
		for (Character ch:sum.toCharArray()) {
			if (bracketCounter == 0) {
				expression = "";
				currentToken = null;
			}
			if (ch.equals('(')) {
				Token token = new Token();
				token.setExpression("");
				
				if (bracketCounter > 0) {
					if (!tokenList.contains(currentToken)) {
						currentToken.setId(tokenList.size());
						tokenList.add(currentToken);
					}
					token.setId(tokenList.size());
					token.setParentId(currentToken.getId());
					expression = expression + "^" + token.getId() + "^";
					currentToken.setExpression(expression);
					currentToken = token;
					expression = "";
				} else {
					currentToken = token;
				}
				bracketCounter++;
			} else {
				if (bracketCounter > 0) {
					if (ch.equals(')')) {
						bracketCounter--;
						currentToken.setExpression(expression);
						if (!tokenList.contains(currentToken)) {
							currentToken.setId(tokenList.size());
							tokenList.add(currentToken);
						}
						if (bracketCounter > 0) {
							currentToken = tokenList.get(currentToken.getParentId());
							expression = currentToken.getExpression();
						}
					} else {
						expression = expression + ch;
					}

				} else {
					if (Character.isDigit(ch) || ch == '.') {
						tempDigit = tempDigit + ch;
					} else {
						if (!tempDigit.equals("")) {
							Token token = new Token();
							token.setExpression(tempDigit);
							token.setId(tokenList.size());
							tokenList.add(token);
							tempDigit = "";
						}
							characterList.add(ch);
					}
				} 
			}
		}
		if (!tempDigit.equals("")) {
			Token token = new Token();
			token.setExpression(tempDigit);
			token.setId(tokenList.size());
			tokenList.add(token);
		}
		
		
		//Start Processing the inner most Bracket Values and then Propagating to upper most Bracket Values
		ArrayList<Integer> processedList = new ArrayList<Integer>();
		
		for (Token token:tokenList) {
			if (!token.getExpression().contains("^") ) {
				processedList.add(token.getId());
			}
		}
		while (true) {
			ArrayList<Integer> processedList2 = new ArrayList<Integer>();
			for (int i =0;i<processedList.size();i++) {
				Token token = tokenList.get(processedList.get(i));
				String evaluated = evaluate(token.getExpression());
				token.setExpression(evaluated);
				if (token.getParentId() != -1) {
					Token tokenParent = tokenList.get(token.getParentId());
					String evaluated2 = tokenParent.getExpression();
					String replaceString = "^" + token.getId() +"^";
					
					String replaced = evaluated2.replace(replaceString, evaluated);
					
					if (!replaced.contains("^")) {
						processedList2.add(token.getParentId());
					}
					tokenParent.setExpression(replaced);
				}
			}
			if (processedList2.size() == 0) {
				break;
			} else {
				processedList = new ArrayList<Integer>();
				processedList.addAll(processedList2);
			}
		}
		
		
		//Once Value propagated up. Just take the "outer most" token and perform arithmatic operations
		List<Double> doubleList = new ArrayList<Double>();	
		for (Token t: tokenList) {
			if (t.getParentId() == -1) {
				doubleList.add(Double.valueOf(t.getExpression()));
			}
		}
		
		return processList(doubleList, characterList);
	}
	
	/*
	 *  Produces a Double Value from an expression. Expression only contains
	 *  numerical values and arithmetic operators
	 */
	private static String evaluate(String expression) {
		ArrayList<Character> characterList = new ArrayList<Character>();
		ArrayList<Double> doubleList = new ArrayList<Double>();
		String tempDigit = "";
		for (Character ch:expression.toCharArray()) { 
			if (Character.isDigit(ch) || ch == '.') {
				tempDigit = tempDigit + ch;
			} else {
				if (!tempDigit.equals("")) {
					doubleList.add(Double.valueOf(tempDigit));
					tempDigit = "";
				}
					characterList.add(ch);
			}
		}
		
		if (!tempDigit.equals("")) {
			doubleList.add(Double.valueOf(tempDigit));
		}
		
		
		
		return String.valueOf(processList(doubleList, characterList));
	}
	
	/*
	 * Process multiplier and divider first. Then process other operators later
	*/
	private static Double processList(List<Double> doubleList, List<Character> characterList) {
		int index = 0;
		
		while (characterList.contains('*') ) {
			index = characterList.lastIndexOf('*');
			characterList.remove(index);
			doubleList.set(index, doubleList.get(index) * doubleList.get(index + 1));
			doubleList.remove(doubleList.get(index + 1));
		}
		
		while (characterList.contains('/') ) {
			index = characterList.lastIndexOf('/');
			characterList.remove(index);
			doubleList.set(index, doubleList.get(index) / doubleList.get(index + 1));
			doubleList.remove(doubleList.get(index + 1));
		}

		Double returnDouble = doubleList.get(0);
		for (int i =0;i<characterList.size();i++) {
			if (characterList.get(i) == '+') {
				returnDouble = returnDouble + doubleList.get(i + 1);
			} else {
				returnDouble = returnDouble - doubleList.get(i + 1);
			}
			
		}
		return returnDouble;
	}
}
