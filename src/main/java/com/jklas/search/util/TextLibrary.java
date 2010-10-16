package com.jklas.search.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jklas.search.index.Term;
import com.jklas.search.util.ApplicationParams.ParamType;

public class TextLibrary {

	private static Pattern wordPattern = Pattern.compile("[\\w]+"); 
	
	private static Map<String,Set<String>> stopWords;
	
	static {
		buildStopWordList();
	}
	
	public static void main(String[] args) {
		
		for (Map.Entry<String, Set<String>> e : stopWords.entrySet()) {
			String lang = e.getKey();
			Set<String> nw = e.getValue();
			
			System.out.println(lang);
			for (String word : nw) {
				System.out.println("-- "+word);
			}
		}
		System.out.println("OK");
	}

	/**
	 * Parte el documento en elementos individuales
	 */
	public static List<Term> tokenize(String text) {
		return tokenize(text," ");
	}
	
	public static List<Term> tokenize(String text, String delimiters) {
		StringTokenizer tokenizer = new StringTokenizer(text,delimiters);

		ArrayList<Term> tokens = new ArrayList<Term>();
		while(tokenizer.hasMoreElements()) {
			tokens.add(new Term(tokenizer.nextToken()));
		}

		return tokens;
	}

	public static String cleanSymbols(StringBuilder text) {
		for (int i = text.length()-1; i>=0; i--) {
			char letter = text.charAt(i);
			if( (letter < 'A' || letter > 'Z') && (letter < 'a' || letter > 'z') &&
				(letter < '0' || letter > '9') ) {
				if(i!=text.length()-1 && text.charAt(i+1)==' ') text.deleteCharAt(i);
				else text.setCharAt(i, ' ');
			}
		}	
		return text.toString().trim();
	}
	
	public static String cleanSymbols(String text, String[] exceptions) {
		StringBuilder copy = new StringBuilder(text);
		return cleanSymbols(copy, exceptions);
	}

	public static String cleanSymbols(StringBuilder text, String[] exceptions) {
		
		int textLength = text.length();
		for (int i = text.length()-1; i>=0; i--) {
			char letter = text.charAt(i);
			if( (letter < 'A' || letter > 'Z') && (letter < 'a' || letter > 'z') &&
				(letter < '0' || letter > '9')) {
				
				boolean confirmed = true;
				for (int j = 0; j < exceptions.length && confirmed; j++) {
					String exception = exceptions[j];
					int exceptionLength = exception.length();
					
					// the exception doesn't fits
					if(textLength<exceptionLength+i) continue;
					
					if( exception.equals(text.substring(i, i+exceptionLength))) confirmed = false;
				}
				
				if(confirmed) {
					if(i!=text.length()-1 && text.charAt(i+1)==' ') {
						text.deleteCharAt(i);
						textLength--;
					} else {
						text.setCharAt(i, ' ');
					}
				}
			}
		}	
		return text.toString().trim();
	}

	public static String translate(String text, char[][] mapping) {		
		return translate(new StringBuilder(text), mapping[0], mapping[1]);
	}
	
	public static String translate(String text, char[] from, char[] to) {		
		return translate(new StringBuilder(text), from, to);
	}
	
	public static String translate(StringBuilder text, char[] from, char[] to) {		
		if(text == null) throw new IllegalArgumentException("Can't work with null text");
		if(from.length!=to.length) throw new IllegalArgumentException("Invalid conversion table, the array lengths doesn't matches");
		
		for (int i = 0; i<text.length(); i++) {
			char original = text.charAt(i);
			for (int j = 0; j < from.length; j++) {
				if(from[j] == original) {
					text.setCharAt(i, to[j]);
					break;
				}
			}			
		}	
		return text.toString();
	}
	
	public static String cleanSymbols(StringBuffer text) {
		for (int i = text.length()-1; i>=0; i--) {
			char letter = text.charAt(i);
			if( (letter < 'A' || letter > 'Z') && (letter < 'a' || letter > 'z') &&
				(letter < '0' || letter > '9') ) {
				if(i!=text.length()-1 && text.charAt(i+1)==' ') text.deleteCharAt(i);
				else text.setCharAt(i, ' ');
			}
		}	
		return text.toString().trim();
	}

	public static String cleanSymbols(String text) {
		StringBuilder copy = new StringBuilder(text);
		return cleanSymbols(copy);
	}
		
	public static void cleanStopWords(StringBuffer words, String lang) {
		Set<String> sw = stopWords.get(lang.toUpperCase());
		
		if(sw == null) {
			sw=stopWords.get("default");
			if(sw == null) return;
		}
		
		List<Pair<String,Integer>> tokens = tokenizeWithPositions(words);
		
		for (Iterator<Pair<String,Integer>> iterator = tokens.iterator(); iterator.hasNext();) {
			Pair<String,Integer> token = iterator.next();
			if(sw.contains(token.getFirst())) {
				words.delete(token.getSecond(), token.getSecond()+token.getFirst().length());				
			}
		}
		
		for (int i = words.length()-2; i >= 0 ; i--) {
			if(words.charAt(i)==' ' && words.charAt(i+1)==' ') words.deleteCharAt(i+1);
		}
		if(words.length()>0 && words.charAt(0)==' ') words.deleteCharAt(0);
	}
	
	public static List<Pair<String, Integer>> tokenizeWithPositions(StringBuffer document) {
		Matcher matcher = wordPattern.matcher(document);
		
		List<Pair<String,Integer>> tokens = new ArrayList<Pair<String,Integer>>();
		
		while(matcher.find()) {
			tokens.add(new Pair<String,Integer>(matcher.group(),matcher.start()));
		}
		
		return tokens;
	}

	public static void cleanStopWords(List<Term> tokens, String lang) {
		Set<String> sw = stopWords.get(lang);
		
		if(sw == null) {
			sw=stopWords.get("default");
			if(sw == null) return;
		}
		
		for (Iterator<Term> iterator = tokens.iterator(); iterator.hasNext();) {
			Term token = (Term) iterator.next();
			if(sw.contains(token.getValue())) iterator.remove();
		}
	}

	private static void buildStopWordList() {
		stopWords = new HashMap<String,Set<String>>();
		
		String dir = (String)ApplicationParams.getParameter(ParamType.SEARCH_PROPERTY, "NO_WORD_DIR");
		
		if(dir==null) return;
		
		File noWordDir = new File(dir);
		for (String langfile : noWordDir.list()) {
			Set<String> noWords = new HashSet<String>();
			
			int i = langfile.lastIndexOf(File.separatorChar);
			if(i==-1) i=0;
			int j = langfile.lastIndexOf(".");
			if(j==-1) j= langfile.length();
			
			String lang = langfile.substring(i, j);
			stopWords.put(lang.toUpperCase(), noWords);
			
			BufferedReader reader=null;
			try {
				reader = new BufferedReader(new FileReader(noWordDir.getAbsolutePath()+File.separatorChar+langfile));
				String currentLine;
				while((currentLine=reader.readLine())!=null) {
					noWords.add(currentLine.toUpperCase().trim());
				}
			} catch (IOException e) {	
				continue;
			} finally {
				if(reader!=null)
					try {reader.close();} catch (IOException e) {}
			}
		}
	}

}
