import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import model.Kgram;

public class KGramTest {
	
	public String[] sortKGramToArray(Kgram grams) {
		SortedSet<String> KGramSet = new TreeSet<String>(grams.getKGram().keySet());
		String[] sortedKGram = KGramSet.toArray(new String[KGramSet.size()]);
		
		return sortedKGram;
	}
	
	public void sortKGram(Kgram grams) {
		SortedSet<String> KGramSet = new TreeSet<String>(grams.getKGram().keySet());
	}
	
	public void sortList(List<String> terms) {
		Collections.sort(terms);
	}
	
	public void addTerms(Kgram grams, List<String> terms) {
		for(int t = 0; t < terms.size(); t++) 
			grams.add(terms.get(t));
	}
	
	public void addTerms(Kgram grams, String term) {
		grams.add(term);
	}
	
	public void compareTest(String[] sortedGrams, String[] trueGrams) {
		for(int c = 0; c < trueGrams.length; c++)
			assertEquals(trueGrams[c], sortedGrams[c]);
	}
	
	public void compareTest(String[] sortedGrams, String trueGram) {
	}
	

	@Test
	public void test() {
		Kgram grams = new Kgram();
		String term = "this";
		String[] trueGrams = {"$t","$th", "h", "hi", "his", "i", "is", "is$", "s", "s$", "t", "th", "thi"};
		addTerms(grams, term);
		sortKGram(grams);
		System.out.println(grams.toString());
		String[] sortedKGrams = sortKGramToArray(grams);
		compareTest(sortedKGrams, trueGrams);
			
	}

}
