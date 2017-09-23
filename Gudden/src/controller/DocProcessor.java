package controller;
import java.util.ArrayList;
import java.util.List;

import model.Document;

/**
 * Class for processing documents to build tokens
 * @author crystalchun
 *
 */
public class DocProcessor 
{
	
	public DocProcessor()
	{
		
	}
	
	public List<String> process(Document doc) // Replace with Document object 
	{		
		
		ArrayList <String> terms = new ArrayList<String> ();

		String [] vocab = doc.getBody().split(" ");
		for(String term: vocab)
		{
			
			term = normalize(term);
			
			// Splits term on hyphen and removes hyphen
			if(term.contains("-"))
			{
				String [] words = term.split("-");
				String wordNoHyphen = term.replaceAll("-", "");
// Do we stem here too?
				wordNoHyphen = stem(wordNoHyphen);
				terms.add(wordNoHyphen);
				
				// Return the array and var and this word 
				for(String word: words)
				{
					word = stem(word);
					terms.add(word);
				}	
			}
			
			
			term = stem(term);
			terms.add(term);
		}
		return terms;
	}
// Should we take care of hyphens in here? but then we need to return all of the split terms	
	public String normalize(String term)
	{
		// Removes apostrophes, non-alphanumeric characters at beginning and end, and converts to lowercase
		term = term.replaceAll("^\\W*", "");
		term = term.replaceAll("\\W*$", "");
		term = term.replaceAll("\'", "");
		term = term.toLowerCase();
		return term;
		
	}
	
	public String stem(String term)
	{
		Stemmer stem = new Stemmer();
		
		for(int i = 0; i < term.length(); i++)
		{
			stem.add(term.charAt(i));
		}
		stem.stem();
		String stemmedTerm = "";
		for(char c : stem.getResultBuffer())
		{
			stemmedTerm += c;
		}
		return stemmedTerm;
	}
}
