package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Kgram {

	/**All of the k-grams and their mappings to words that match it*/
	private HashMap <String, List<String>> k_gram;
	
	public Kgram() {
		k_gram = new HashMap<String, List<String>> ();
	}
	
	public void add(String term) {
		// Add the dollar signs
		term = "$" + term + "$";
		
		for(int i = 1; i <= 3; i++) {
			split(i,term);
		}
	}
	
	public HashMap<String, List<String>> getKGram() {
		return k_gram;
	}
	
	/**
	 * Splits the term into grams of k-size, and adds each term into the index.
	 * @param k The size of the gram
	 * @param term The term
	 */
	private void split(int k, String term) {
		int i = 0;

		// Split into grams and add into list then return list
		while(i + k < term.length()) {
			String key = "";
			key = term.substring(i, i + k);
			
			addKey(key, term);
			i++;
		}
	}
	
	/**
	 * Adds the gram and the term associated with it to the k-gram index.
	 * @param key The key for this term.
	 * @param term The term this key maps to.
	 */
	private void addKey(String key, String term) {
		
		// Then check current k_gram hashmap to see if gram already exists
		if(k_gram.containsKey(key)) {
			// Check if k_gram's list contains term
			if(!(k_gram.get(key)).contains(term)) {
				(k_gram.get(key)).add(term);
			}
		}
		else {
			// Create a new list for k_gram and insert term into it
			ArrayList <String> list = new ArrayList <String> ();
			
			list.add(term);
			k_gram.put(key, list);
		}
	}
	
	public List<String> getTerms(String key) {
		return k_gram.get(key);
	}
}