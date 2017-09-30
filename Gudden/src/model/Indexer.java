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

	public void addPosition(String token, int docId, int position) {

		if (!containsTerm(token))
			createTerm(token);

		if (!containsDocId(token, docId))
			addDocId(token, docId);

		PositionalPosting posting = getRecentPosting(getPostings(token));
		posting.addPosition(position);
	}
	
	public List<PositionalPosting> getPostings(String token) {
		return this.index.get(token);
	}

	public int getTermCount() {
		return this.index.size();
	}

	public String[] getDictionary() {
		SortedSet<String> terms = new TreeSet<String>(this.index.keySet());
		return terms.toArray(new String[terms.size()]);
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
	
	public static List<PositionalPosting> positionalIntersect(List<PositionalPosting> p1, List<PositionalPosting> p2,
			int k) {
		List<PositionalPosting> result = new ArrayList<PositionalPosting>();
		int i = 0, j = 0;
		while(i < p1.size() && j < p2.size()) {
			int p1ID = p1.get(i).getDocId();
			int p2ID = p2.get(j).getDocId();
			if (p1ID == p2ID) {
				List<Integer> positions = new ArrayList<Integer>();
				List<Integer> posList1 = p1.get(i).getPositions();
				List<Integer> posList2 = p2.get(j).getPositions();
				int ii = 0, postingPositionOne = Integer.MIN_VALUE;
				while (ii < posList1.size()) {
					int jj = 0;
					while (jj < posList2.size()) {
						postingPositionOne = posList1.get(ii);
						int postingPositionTwo = posList2.get(jj);
						int distance = postingPositionTwo - postingPositionOne;
						if (0 < distance && distance <= k) {
							positions.add(postingPositionTwo);
						} else if (postingPositionTwo > postingPositionOne){
							break;
						}
						jj++;
					}
					
					while (!positions.isEmpty() && Math.abs(positions.get(0) - postingPositionOne) > k)
						positions.remove(0);
					
					if (!positions.isEmpty()) {
						PositionalPosting posAnswer = new PositionalPosting(p1ID);
						posAnswer.addPosition(postingPositionOne);
						for (int pos : positions) {
							posAnswer.addPosition(pos);
						}
						result.add(posAnswer);
					}
					ii++;
				}
				i++;
				j++;
			} else if (p1ID < p2ID) {
				i++;
			} else {
				j++;
			}
		}
		return result;
	}

	public static List<PositionalPosting> intersect(List<PositionalPosting> p1, List<PositionalPosting> p2) {
		List<PositionalPosting> result = new ArrayList<PositionalPosting>();
		for (int i = 0, j = 0; i < p1.size() && j < p2.size();) {
			int p1ID = p1.get(i).getDocId();
			int p2ID = p2.get(j).getDocId();
			if (p1ID == p2ID) {
				result.add(new PositionalPosting(p1ID));
				i++;
				j++;
			} else if (p1ID < p2ID) {
				i++;
			} else {
				j++;
			}
		}
		return result;
	}
	
	public static List<PositionalPosting> union(List<PositionalPosting> p1, List<PositionalPosting> p2) {
		List<PositionalPosting> result = new ArrayList<PositionalPosting>();
		int i, j;
		for (i = 0, j = 0; i < p1.size() && j < p2.size();) {
			int p1ID = p1.get(i).getDocId();
			int p2ID = p2.get(j).getDocId();
			if (p1ID == p2ID) {
				result.add(new PositionalPosting(p1ID));
				result.add(new PositionalPosting(p2ID));
				i++;
				j++;
			} else if (j < p2.size() && p1ID > p2ID) {
				result.add(new PositionalPosting(p2ID));
				j++;
			} else if (i < p1.size() && p2ID > p1ID) {
				result.add(new PositionalPosting(p1ID));
				i++;
			}
		}
		while (i < p1.size()) {
			int p1ID = p1.get(i++).getDocId();
			result.add(new PositionalPosting(p1ID));
		}
		while (j < p2.size()) {
			int p2ID = p2.get(j++).getDocId();
			result.add(new PositionalPosting(p2ID));
			
		}
		List<PositionalPosting> andResult = intersect(p1, p2);
		for (i = 0, j = 0; i < andResult.size();) {
			int p1ID = andResult.get(i).getDocId();
			int p2ID = result.get(j).getDocId();
			if (p1ID == p2ID) {
				result.remove(j);
				i++;
			} else if (p1ID > p2ID) {
				j++;
			} else {
				i++;
			}
		}
		return result;
	}

	private boolean containsDocId(String term, int docId) {
		List<PositionalPosting> postingsList = getPostings(term);
		int lastIndex = postingsList.size() - 1;
		return !postingsList.isEmpty() && postingsList.get(lastIndex).getDocId() >= docId;
	}

	private void addDocId(String term, int docId) {
		List<PositionalPosting> postingsList = getPostings(term);
		postingsList.add(new PositionalPosting(docId));
	}

	private PositionalPosting getRecentPosting(List<PositionalPosting> postingsList) {
		int lastIndex = postingsList.size() - 1;
		return postingsList.get(lastIndex);
	}

	private void createTerm(String term) {
		this.index.put(term, new ArrayList<PositionalPosting>());
	}

	private boolean containsTerm(String term) {
		return this.index.containsKey(term);
	}
	
}
