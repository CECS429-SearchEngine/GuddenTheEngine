package model;

import java.util.LinkedList;
import java.util.List;

/**
 * A Positional Posting object that contains the document ID
 * of a term as well as a list of the positions of the term 
 * in the specified document ID.
 */
public class PositionalPosting {

	/**The document that contains the term associated with this PositionalPosting*/
	private int docId;
	/**The list of positions of the term associated with this PositionalPosting in the specified document*/
	private List<Integer> pos = new LinkedList<Integer>();
	
	/**
	 * Constructs a PositionalPosting object
	 * @param docId The document ID
	 */
	public PositionalPosting(int docId) {
		this.docId = docId;
	}

	/**
	 * Gets the document ID of this PositionalPosting.
	 * @return The document ID
	 */
	public int getDocId() {
		return docId;
	}

	/**
	 * Adds a position to this PositionalPosting's position list.
	 * @param position The position to be added to the list.
	 */
	public void addPosition(int position) {
		pos.add(position);
	}
	
	/**
	 * Gets the the position list.
	 * @return The position list
	 */
	public List<Integer> getPositions() {
		return pos;
	}

	/**
	 * Tests if an object is equal to this PositionalPosting object by checking if 
	 * the object is a PositionalPosting object and if all the postings match.
	 * @param other The other object
	 * @return True if the other object is equal to this PositionalPosting object.
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof PositionalPosting ? comparePositions((PositionalPosting)other) : false;
	}
	
	/**
	 * Compares the positions of this PositionalPosting object to the positions of
	 * the other PositionalPosting object.
	 * @param other The other PositionalPosting object
	 * @return True if all of their postings match.
	 */
	private boolean comparePositions(PositionalPosting other) {
		List<Integer> thisPos = pos, otherPos = other.getPositions();
		if (other.getPositions().size() != other.getPositions().size()) return false;
		for (int i = 0; i < otherPos.size(); i++) {
			if (otherPos.get(i) != thisPos.get(i)) return false;
		}
		return true;
	}
}