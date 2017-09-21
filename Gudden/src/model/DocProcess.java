package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for processing documents to build tokens
 * @author crystalchun
 *
 */
public class DocProcess 
{
	
	public DocProcess()
	{
		
	}
	
	public List<String> process(Document doc) // Replace with Document object 
	{		
		ArrayList <String> terms = new ArrayList<String> ();

		String [] vocab = doc.getBody().split(" ");
		for(String term: vocab)
		{
			// Removes apostrophes, non-alphanumeric characters at beginning and end, and converts to lowercase
			term = term.replaceAll("^\\W*", "");
			term = term.replaceAll("\\W*$", "");
			term = term.replaceAll("\'", "");
			term = term.toLowerCase();
			
			// Splits term on hyphen and removes hyphen
			if(term.contains("-"))
			{
				String [] words = term.split("-");
				String wordNoHyphen = term.replaceAll("-", "");
				terms.add(wordNoHyphen);
				// Return the array and var and this word 
				for(String word: words)
				{
					terms.add(word);
				}	
			}
			terms.add(term);
		}
		return terms;
	}
}
