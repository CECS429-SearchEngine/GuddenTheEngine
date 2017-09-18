package model;

import java.util.LinkedList;
import java.util.List;

public class PositionalPosting {

	private int docId;
	
	public int getDocId() { return docId; }
	private List<Integer> pos = new LinkedList<Integer>();
	
	public PositionalPosting(int docId) {
		this.docId = docId;
	}
	
	public void addPosition(int position) {
		pos.add(position);
	}
	
	public List<Integer> getPositions(){
		return pos;
	}
}
