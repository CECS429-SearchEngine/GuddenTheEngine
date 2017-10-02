package model;

import java.util.LinkedList;
import java.util.List;

public class PositionalPosting {

	private int docId;
	private List<Integer> pos = new LinkedList<Integer>();

	public PositionalPosting(int docId) {
		this.docId = docId;
	}

	public int getDocId() {
		return docId;
	}

	public void addPosition(int position) {
		pos.add(position);
	}

	public List<Integer> getPositions() {
		return pos;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof PositionalPosting ? comparePositions((PositionalPosting)other) : false;
	}
	
	private boolean comparePositions(PositionalPosting other) {
		List<Integer> thisPos = pos, otherPos = other.getPositions();
		if (other.getPositions().size() != other.getPositions().size()) return false;
		for (int i = 0; i < otherPos.size(); i++) {
			if (otherPos.get(i) != thisPos.get(i)) return false;
		}
		return true;
	}
}