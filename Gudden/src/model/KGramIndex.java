package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * K-gram index object that holds k-grams and their term mappings.
 * 
 * @author crystalchun
 *
 */
public class KGramIndex {

	/** All of the k-grams and their mappings to words that match it */
	private HashMap<String, PriorityQueue<String>> index;

	public KGramIndex() {
		this.index = new HashMap<String, PriorityQueue<String>>();
	}

	public void addToken(String token) {
		String specialToken = encapsulateToken(token);
		for (int k = 1; k <= 3; k++) {
			addKGrams(k, specialToken);
		}
	}

	public PriorityQueue<String> getPostings(String token) {
		return this.index.get(token);
	}

	public void resetIndex() {
		this.index = new HashMap<String, PriorityQueue<String>>();
	}

	public String[] getGrams() {
		SortedSet<String> grams = new TreeSet<String>(this.index.keySet());
		return grams.toArray(new String[grams.size()]);
	}

	public List<String> generateKGrams(int k, String token) {
		List<String> grams = new LinkedList<String>();
		for (int i = 0; i <= token.length() - k; i++) {
			String kGram = token.substring(i, i + k);
			grams.add(kGram);
		}
		return grams;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String gram : getGrams()) {
			sb.append(gram);
			sb.append(" --> ");
			sb.append(String.join(" --> ", getPostings(gram)));
			sb.append("\n");
		}
		return sb.toString();
	}

	public static PriorityQueue<String> intersect(PriorityQueue<String> p1, PriorityQueue<String> p2) {
		if (p1 == null || p2 == null)
			return null;
		PriorityQueue<String> result = new PriorityQueue<String>();
		PriorityQueue<String> temp1 = new PriorityQueue<String>();
		PriorityQueue<String> temp2 = new PriorityQueue<String>();
		int sizeP1 = p1.size(), sizeP2 = p2.size();
		String p1Token = p1.poll(), p2Token = p2.poll();
		for (int i = 0, j = 0; i < sizeP1 && j < sizeP2;) {
			if (p1Token.compareTo(p2Token) == 0) {
				result.add(p1Token);
				temp1.add(p1Token);
				temp2.add(p2Token);
				i++;
				j++;
				if (!(i < sizeP1 && j < sizeP2))
					break;
				p1Token = p1.poll();
				p2Token = p2.poll();
			} else if (p1Token.compareTo(p2Token) > 0) {
				p2Token = p2.poll();
				temp1.add(p1Token);
				j++;
			} else {
				p1Token = p1.poll();
				temp2.add(p2Token);
				i++;
			}

		}
		while (!temp1.isEmpty())
			p1.add(temp1.poll());
		while (!temp2.isEmpty())
			p2.add(temp2.poll());
		return result;
	}

	public static String encapsulateToken(String token) {
		StringBuilder sb = new StringBuilder();
		sb.append('$');
		sb.append(token);
		sb.append('$');
		return sb.toString();
	}

	public static LinkedList<String> postFilter(PriorityQueue<String> terms, String originalQuery) {
		LinkedList<String> tokens = new LinkedList<String>();
		for (String term : terms) {
			if (term.matches(originalQuery)) {
				tokens.add(term);
			}
		}
		return tokens;
	}

	private void addToken(String kGram, String token) {
		PriorityQueue<String> postings = getPostings(kGram);
		if (!postings.contains(token))
			postings.add(token);
	}

	/**
	 * Splits the term into grams of k-size, and adds each term into the index.
	 * 
	 * @param k
	 *            The size of the gram
	 * @param term
	 *            The term
	 */
	private void addKGrams(int k, String token) {
		List<String> grams = generateKGrams(k, token);
		for (String gram : grams) {
			if (!gram.equals("$")) {
				if (!containsTerm(gram))
					createTerm(gram);
				addToken(gram, token.replaceAll("\\$", ""));
			}
		}
	}

	private void createTerm(String token) {
		this.index.put(token, new PriorityQueue<String>());
	}

	private boolean containsTerm(String token) {
		return this.index.containsKey(token);
	}
}
