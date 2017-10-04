package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * Class for processing documents to build tokens
 * 
 * @author crystalchun
 *
 */
public class DocProcessor {
	private File file;
	private Scanner sc;

	public DocProcessor(File file) {
		this.file = file;
		this.sc = new Scanner(process().getBody());
	}

	public DocProcessor(String token) {
		this.sc = new Scanner(token);
	}

	public boolean hasNextToken() {
		return this.sc.hasNext();
	}

	public List<String> nextToken() {
		if (!hasNextToken())
			return null;
		List<String> tokens = new ArrayList<String>();
		populateTokens(sc.next(), tokens);
		normalizeTokens(tokens);
		return tokens;
	}

	private Document getDocument(File file) {
		Gson gson = new Gson();
		JsonObject json = null;

		try {
			json = parseToJsonObject(new FileInputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return gson.fromJson(json, Document.class);
	}

	private JsonObject parseToJsonObject(InputStream in) throws IOException {
		JsonParser parser = new JsonParser();
		return parser.parse(new JsonReader(new InputStreamReader(in, "UTF-8"))).getAsJsonObject();
	}

	private String normalizeToken(String token) {
		token = Normalizer.trimNonAlphanumeric(token);
		token = Normalizer.removeApostrophe(token);
		token = Normalizer.stemToken(token);
		return token.toLowerCase();
	}

	private void populateTokens(String token, List<String> tokens) {
		if (token.contains("-")) {
			Set<String> tokenSet = Normalizer.splitHypenWords(token);
			for (String each : tokenSet) {
				tokens.add(each);
			}
		} else {
			tokens.add(token);
		}
	}

	private void normalizeTokens(List<String> tokens) {
		String normalizedToken;
		for (int i = 0; i < tokens.size(); i++) {
			normalizedToken = normalizeToken(tokens.get(i));
			tokens.set(i, normalizedToken);
			if (normalizedToken.length() == 0) {
				tokens.remove(i--);
			}
		}
	}

	private Document process() {
		Document doc = getDocument(this.file);
		Scanner sc = new Scanner(doc.getBody());
		return doc;
	}
}
