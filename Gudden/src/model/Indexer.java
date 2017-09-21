package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Indexer {
	
	private HashMap<String, List<PositionalPosting>> mIndex;
	   
	   
	   public Indexer() {
	      mIndex = new HashMap<String, List<PositionalPosting>>();
	   }
	   
	   public void addTerm(String term, int documentID, int position) {
	      if(!mIndex.containsKey(term))
	    	  mIndex.put(term, new ArrayList<PositionalPosting>());
	      List<PositionalPosting> docList = mIndex.get(term);
	      if((docList.size()==0) || (docList.get(docList.size()-1).getDocId() < documentID) )
	    	  mIndex.get(term).add(new PositionalPosting(documentID));
	      mIndex.get(term).get(mIndex.get(term).size()-1).addPosition(position);
	   }
	   
	   public List<PositionalPosting> getPostings(String term) {
	      return mIndex.get(term);
	   }
	   
	   public int getTermCount() {
		  return mIndex.size();
	   }
	   
	   public String[] getDictionary() {
	      String test[] = mIndex.keySet().toArray(new String[mIndex.size()]);
		  Arrays.sort(test);
		   
	      return test;
	   }
}
