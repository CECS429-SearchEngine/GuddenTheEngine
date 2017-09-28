package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
		SortedSet<String> terms = new TreeSet<String>(this.index.keySet());
		return terms.toArray(new String[terms.size()]);
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
	
	public void resetIndex() {
		this.index = new HashMap<String, List<PositionalPosting>>();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String[] dictionary = getDictionary();
		for(int j = 0; j < dictionary.length; j++) {
			List<PositionalPosting> ppList = getPostings(dictionary[j]);
			sb.append(dictionary[j] + ":" + "\n");
			for(PositionalPosting e : ppList) {
				sb.append("Document ID " + e.getDocId() + ": ");
				for(int l : e.getPositions())
					sb.append(l + " ");
				sb.append("\n");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		Indexer indexer = new Indexer();
		String[] exDoc = {"the","fox","jumped","over","the","fox"};
		for(int x = 0; x < exDoc.length; x++) {
			indexer.addPosition(exDoc[x], 0, x);
		}
		String[] dictionary = indexer.getDictionary();
		for(int j = 0; j < dictionary.length; j++) {
			List<PositionalPosting> ppList = indexer.getPostings(dictionary[j]);
			System.out.print(dictionary[j] + " ");
			for(PositionalPosting e : ppList) {
				System.out.print(e.getDocId() + " ");
				for(int l : e.getPositions())
					System.out.print(l + " ");
				System.out.println();
			}
		}
	}
}
