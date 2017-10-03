package model;

import java.util.HashSet;
import java.util.Set;

public class Normalizer {
	
	public static String trimNonAlphanumeric(String token) {
		return token.replaceAll("^\\W*", "").replaceAll("\\W*$", "");
	}
	
	public static String removeApostrophe(String token) {
		return token.replaceAll("\'", "");
	}
	
	public static Set<String> splitHypenWords(String token) {
		Set<String> tokenSet = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		String[] tokens = token.split("-");
		for (String each : tokens) {
			tokenSet.add(each);
			sb.append(each);
		}
		tokenSet.add(sb.toString());
		return tokenSet;
	}
	
	public static String stemToken(String token) {
		Stemmer stemmer = new Stemmer();
		stemmer.add(token.toCharArray(), token.length());
		stemmer.stem();
		return stemmer.toString();
	}
	
}
