package model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Query {
	private List<String> tokens;
	
	public Query(String word) {
		this.tokens = new ArrayList<String>();
		tokenize(word);
	}
	
	public List<String> getTokens() {
		return this.tokens;
	}
	
	public String toString() {
		return String.join(", ", tokens);
	}
	
	// sets token to lower case and stems them. 
	private String normalize(String token) {
		String normalized = Normalizer.trimNonAlphanumeric(token).toLowerCase();
		if (token.contains(" ")) {
			String[] normalizedTokens = normalized.split("\\s+");
			for (int i = 0; i < normalizedTokens.length; i++) {
				normalizedTokens[i] = Normalizer.stemToken(normalizedTokens[i]);
			}
			String normalizedToken = String.join(" ", normalizedTokens);
			return normalizedToken;
		}
		return Normalizer.stemToken(normalized);
	}
	
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
