package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Query {
	private ArrayList<String> tokens;
	private Stemmer stemmer = new Stemmer();
	
	public Query() {
		this.tokens = new ArrayList<String>();
	}
	
	public void query(String word) {
		tokenize(word);
	}
	
	// sets token to lower case and stems them. 
	private String normalize(String token) {
		String normalized = token.replaceAll("\"", "").toLowerCase();
		if (token.contains(" ")) {
			String[] normalizedTokens = normalized.split("\\s+");
			StringBuilder sb = new StringBuilder();
			for (String each : normalizedTokens) {
				sb.append(stemToken(each));
				sb.append(" ");
			}
			return sb.toString();
		}
		return stemToken(normalized);
		
	}
	
	private String stemToken(String token) {
		stemmer.add(token.toCharArray(), token.length());
		stemmer.stem();
		return stemmer.toString();
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
		for (String s : tokens) {
			System.out.println(s);
		}
	}
	
	public static void main(String[] args) {
		Query q = new Query();
		String token = "shakes               \"Harry         Juice\"";
		q.query(token);
	}
}
