import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import model.DocProcessor;
import model.Document;
import model.Indexer;
import model.PositionalPosting;

/**
 * Tests the positional inverted index
 * @author crystalchun
 *
 */
public class IndexTest {

	@Test
	public void testWashingtonTestCorpus() {
		/*	
		//Has the indexer index the document
		Indexer index = new Indexer();
		for(int i = 1; i < 6; i++) {
			DocProcessor docs = new DocProcessor(new File("external/doc" + i + ".json"));
			List <String> terms = docs.process();
			for(int j = 0; j < terms.size(); j++) {
				index.addPosition(terms.get(j), i-1, j);
			}
		}
		*/
		
		
		Indexer index = new Indexer();
		for(int i = 1; i < 6; i++) {
			//File file = new File("external/doc" + i + ".json"));
			File file = new File ("external\\testcorpus\\Crystal\\json\\article"+i+".json");
			DocProcessor docs = new DocProcessor(file);
			int position = 0;
			while(docs.hasNextToken()) {
				List<String> tokens = docs.nextToken();
				for(String term : tokens)
					index.addPosition(term, i-1, position++);
			}

		}
		
		//System.out.println(index);
		assertEquals(21, index.getTermCount());
		
		String [] terms = {"ar","british","dog","enjoi",
				"enjoy","in","is","known","mani","monument",
				"on","park","raid","seattl","the","there","wa",
				"washington","well","wellknown","went"};
		
		Indexer handBuilt = new Indexer();
		fillHandBuilt(handBuilt, terms);
		
		// Runs through each term and each positional posting to see if the handbuilt index matches the index
		for(int i = 0; i < terms.length; i ++) {
			for(int j = 0; j < handBuilt.getPostings(terms[i]).size(); j++) {
				System.out.println(terms[i]);
				System.out.println("Hand built: " +handBuilt.getPostings(terms[i]).get(j).getDocId());
				System.out.println("index: " + index.getPostings(terms[i]).get(j).getDocId());
				assertEquals(handBuilt.getPostings(terms[i]).get(j), index.getPostings(terms[i]).get(j));
			}
		}
	}
	
	/**
	 * Fills hand built index with terms by hand
	 * @param index Hand built index
	 * @param terms List of terms
	 */
	public void fillHandBuilt(Indexer index, String [] terms) {
		index.addPosition(terms[0], 3, 1); // ar Term, docID, pos
		index.addPosition(terms[1], 2, 4); // british
		index.addPosition(terms[2], 3, 3); // dog
		index.addPosition(terms[2], 4, 0); // dog
		index.addPosition(terms[2], 4, 3); // dog
		index.addPosition(terms[3], 4, 1); // enjoi
		index.addPosition(terms[4], 0, 6); // enjoy
		index.addPosition(terms[5], 1, 5); // in, potential error
		index.addPosition(terms[5], 3, 4); // in
		index.addPosition(terms[6], 1, 4); // is, potential error
		index.addPosition(terms[7], 1, 1); // known
		index.addPosition(terms[8], 3, 2); // mani
		index.addPosition(terms[9], 0, 1); // monument
		index.addPosition(terms[9], 1, 3); // monument potential error
		index.addPosition(terms[10], 0, 3); // on
		index.addPosition(terms[10], 2, 2); // on
		index.addPosition(terms[11], 0, 4); // park
		index.addPosition(terms[11], 4, 4); // park
		index.addPosition(terms[12], 0, 2); // raid
		index.addPosition(terms[12], 2, 1); // raid
		index.addPosition(terms[13], 3, 5); // seattl
		index.addPosition(terms[14], 0, 0); // the
		index.addPosition(terms[14], 1, 0); // the
		index.addPosition(terms[14], 2, 3); // the
		index.addPosition(terms[14], 4, 2); // the
		index.addPosition(terms[15], 3, 0); // there
		index.addPosition(terms[16], 0, 5); // wa
		index.addPosition(terms[17], 1, 2); // washington potential error
		index.addPosition(terms[17], 2, 6); // washington potential error
		index.addPosition(terms[17], 2, 0); // washington
		index.addPosition(terms[17], 3, 6); // washington
		index.addPosition(terms[18], 1, 1); // well potential error
		index.addPosition(terms[18], 2, 6); // well 
		index.addPosition(terms[19], 1, 1); // wellknown
		index.addPosition(terms[20], 2, 6); // went
	}

}