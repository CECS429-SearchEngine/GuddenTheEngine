package model;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for normalizing tokens
 */
public class Normalizer {
	
	/**
	 * Removes all non-alphanumeric characters from the beginning and end of a string.
	 * @param token The string 
	 * @return The string with the non-alphanumeric characters removed from the beginning
	 * and end.
	 */
	public static String trimNonAlphanumeric(String token) {
		return token.replaceAll("^\\W*", "").replaceAll("\\W*$", "");
	}
	
	/**
	 * Removes all apostrophes from a string.
	 * @param token The string
	 * @return The string without any apostrophes
	 */
	public static String removeApostrophe(String token) {
		return token.replaceAll("\'", "");
	}
	
	/**
	 * Splits words with hyphens in them and stores both the separated words, and the
	 * original hyphenated word without the hyphens in between the words.
	 * @param token The string
	 * @return A set of all strings from the hyphenated word
	 */
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
	
	/**
	 * Stems a string using a stemming algorithm.
	 * @param token The string
	 * @return The stemmed string
	 */
	public static String stemToken(String token) {
		Stemmer stemmer = new Stemmer();
		stemmer.add(token.toCharArray(), token.length());
		stemmer.stem();
		return stemmer.toString();
	}
}
