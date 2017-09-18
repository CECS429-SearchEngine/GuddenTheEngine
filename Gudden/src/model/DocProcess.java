package model;

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
	
	public void process(String word) // Replace with Document object 
	{
		System.out.println("Word: " + word);
		
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
	}
}
