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
	File file;
	
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
	
	private void populateTerms(String token, List<String> terms) {
		if (token.contains("-")) {
			Set<String> tokens = Normalizer.splitHypenWords(token);
			for (String each : tokens) {
				terms.add(each);
			}
		} else {
			terms.add(token);
		}
	}
	
	private void normalizeTerms(List<String> terms) {
		String normalizedToken;
		for (int i = 0; i < terms.size(); i++) {
			normalizedToken = normalizeToken(terms.get(i));
			terms.set(i, normalizedToken);
			if (normalizedToken.length() == 0) {
				terms.remove(i--);
			}
		}
	}
	
	public DocProcessor(File file) {
		
		this.file = file;
	}
	
	public List<String> process() { // Replace with Document object
		Document doc = getDocument(this.file);
		Scanner sc = new Scanner(doc.getBody());
		List<String> terms = new ArrayList<String>();
		while (sc.hasNext()) {
			populateTerms(sc.next(), terms);
		}
		normalizeTerms(terms);
		return terms;
	}
}
