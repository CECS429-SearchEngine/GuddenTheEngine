package model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Query class that contains a list of tokens
 * from a query.
 */
public class Query {
	
	/**The list of tokens from a query*/
	private List<String> tokens;
	
	/**
	 * Constructs the query class with the specified query.
	 * @param word The query
	 */
	public Query(String word) {
		this.tokens = new ArrayList<String>();
		tokenize(word);
	}
	
	/**
	 * Gets the list of tokens in this query.
	 * @return The list of tokens in this query.
	 */
	public List<String> getTokens() {
		return this.tokens;
	}
	
	/**
	 * Gets the string representation of this query by joining
	 * all of the tokens into a single string separated by commas.
	 * @return The string representation of this query.
	 */
	@Override
	public String toString() {
		return String.join(", ", tokens);
	}
	
	
	/**
	 * Sets the token to lowercase and stems all of them.
	 * @param token A token
	 * @return The normalized form of the token.
	 */
	private String normalize(String token) {
		// sets token to lower case and stems them. 
		String normalized = (Normalizer
				             .removeApostrophe(Normalizer.trimNonAlphanumeric(token))
						     .replaceAll("-",  "").toLowerCase());
		// Indicates phrase query, so each word is normalized and then the token is rejoined
		if (token.contains(" ")) {
			String[] normalizedTokens = normalized.split("\\s+");
			for (int i = 0; i < normalizedTokens.length; i++) {
				normalizedTokens[i] = Normalizer.stemToken(normalizedTokens[i]);
			}
			String normalizedToken = String.join(" ", normalizedTokens);
			return normalizedToken;
		} 
		if (token.charAt(token.length() - 1) == '*') {
			normalized += '*';
		}
		if (token.charAt(0) == '*') {
			normalized = '*' + normalized;
		}
		return Normalizer.stemToken(normalized);
	}
	
	/**
	 * Tokenizes the query.
	 * @param query The query
	 */
	private void tokenize(String query) {
		// Anything encased in () refers to capture everything inside ()
		// [^\"] capture a character that is not a quotation mark.
		// \\S* capture zero or more non white-space character.
		// \".+?\" match one or more characters that are in quotation marks.
		// \\s match zero or more white spaced
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(query); 
		
		// gets words that are either separated by space or ecased in quotes.
		while (m.find()) {
			String token = normalize(m.group(1));
			this.tokens.add(token);
		}
	}
	
}
