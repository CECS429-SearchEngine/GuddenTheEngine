package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Indexer {

	private HashMap<String, List<PositionalPosting>> index;

	public Indexer() {
		this.index = new HashMap<String, List<PositionalPosting>>();
	}

	public void addPosition(String term, int docId, int position) {

		if (!containsTerm(term))
			createTerm(term);

		if (!containsDocId(term, docId))
			addDocId(term, docId);

		PositionalPosting posting = getRecentPosting(getPostings(term));
		posting.addPosition(position);
	}

	public List<PositionalPosting> getPostings(String term) {
		return this.index.get(term);
	}

	public int getTermCount() {
		return this.index.size();
	}

	public String[] getDictionary() {
		String test[] = index.keySet().toArray(new String[index.size()]);
		Arrays.sort(test);

		return test;
	}

	private boolean containsTerm(String term) {
		return this.index.containsKey(term);
	}

	private boolean containsDocId(String term, int docId) {
		List<PositionalPosting> postingsList = getPostings(term);
		int lastIndex = postingsList.size() - 1;
		return !postingsList.isEmpty() && postingsList.get(lastIndex).getDocId() >= docId;
	}

	private void createTerm(String term) {
		this.index.put(term, new ArrayList<PositionalPosting>());
	}

	private void addDocId(String term, int docId) {
		List<PositionalPosting> postingsList = getPostings(term);
		postingsList.add(new PositionalPosting(docId));
	}

	private PositionalPosting getRecentPosting(List<PositionalPosting> postingsList) {
		int lastIndex = postingsList.size() - 1;
		return postingsList.get(lastIndex);
	}
}
