package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Kgram {

	/**All of the k-grams and their mappings to words that match it*/
	private HashMap<String, List<String>> k_gram;
	
	public Kgram()
	{
		k_gram = new HashMap<String, List<String>> ();
	}
	
	public void add(String term)
	{
		// First prepare the term to be a k_gram index
		// Add the dollar signs
		term = "$" + term + "$";
		split(term);
	}
	
	public HashMap<String, List<String>> getKGram()
	{
		return k_gram;
	}
	
	private void split(String term)
	{	
		int i = 0;

// FIX:
		// Split into grams and add into list then return list
		while(i < term.length())
		{
			String key = "";
			for(int j = i; j < i + 3 && j < term.length(); j++)
			{
				if(term.charAt(j) != '*')
				{
					key += term.charAt(j);
				}
				else
				{
					break;
				}
			}
			addKey(key, term);
			i++;
		}
		
	}
	
	private void addKey(String key, String term)
	{
		// Then check current k_gram hashmap to see if gram already exists
		if(k_gram.containsKey(key))
		{
			// Check if k_gram's list contains term
			if(!(k_gram.get(key)).contains(term))
			{
				(k_gram.get(key)).add(term);
			}
		}
		else
		{
			// Create a new list for k_gram and insert term into it
			ArrayList <String> list = new ArrayList <String> ();
			list.add(term);
			k_gram.put(key, list);
		}
	}
}
