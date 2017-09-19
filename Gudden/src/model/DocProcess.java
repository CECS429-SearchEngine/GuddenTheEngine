package model;

import java.util.ArrayList;
import java.util.List;

//import java.util.regex.Pattern;

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
	
	public List<String> process(String word /*Document doc*/) // Replace with Document object 
	{
		System.out.println("Word: " + word);
		
		ArrayList <String> terms = new ArrayList<String> ();
		// Uncomment when have document element
		/*for(String term: doc.terms()) // Change this method
		{
			// Removes apostrophes, non-alphanumeric characters at beginning and end, and converts to lowercase
			term = term.replaceAll("^\\W*", "");
			term = term.replaceAll("\\W*$", "");
			term = term.replaceAll("\'", "");
			term = term.toLower();
			
			// Splits term on hyphen and removes hyphen
			if(term.contains("-"))
			{
				String [] words = word.split("-");
				String wordNoHyphen = word.replaceAll("-", "");
				terms.add(wordNoHyphen);
				// Return the array and var and this word 
				for(String word: words)
				{
					terms.add(word);
				}	
			}
			terms.add(term);
		}*/
		// Remove non-alphanumeric chars from begin and end
		System.out.println("Replacing non-alphanumeric characters in beginning of word . . .");
		word = word.replaceAll("^\\W*", "");
		System.out.println("Replaced non-alphanumeric characters in beginning: " + word);
		System.out.println("Replacing non-alphanumeric characters at end of word . . .");
		word = word.replaceAll("\\W*$", "");
		System.out.println("Replaced non-alphanumeric characters in end: " + word);
		
		// Remove all apostrophes
		System.out.println("Removing apostrophes");
		word = word.replaceAll("\'", "");
		System.out.println("Final word: " + word);
		
		if(word.contains("-"))
		{
			String [] words = word.split("-");
			String wordNoHyphen = word.replaceAll("-", "");
			// Return the array and var and this word 
			
		}
		
		return terms;
	}
}
