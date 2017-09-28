package model;

import java.util.LinkedList;
import java.util.List;

public class PositionalPosting {

	private int docId;
	private List<Integer> pos = new LinkedList<Integer>();
	
	public PositionalPosting(int docId) {
		this.docId = docId;
	}
	
	public int getDocId() { return docId; }
	
	public void addPosition(int position) {
		pos.add(position);
	}
	
	public List<Integer> getPositions(){
		return pos;
	}
	
	@Override
	public boolean equals(Object other)
	{
		boolean equal = true;
		PositionalPosting post;
		if(other instanceof PositionalPosting)
		{
			
			post = (PositionalPosting) other;
			
			if(post.getDocId() == this.docId && this.getPositions().size() == post.getPositions().size())
			{
				for(int i = 0; i < this.getPositions().size(); i++)
				{
					if(post.getPositions().get(i) != this.getPositions().get(i))
					{
						equal = false;
					}
				}
			}
			else
			{
				equal = false;
			}
		}
		else
		{
			equal = false;
		}
		
		return equal;
	}
}
