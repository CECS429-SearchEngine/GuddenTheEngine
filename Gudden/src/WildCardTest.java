import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

import model.DocProcessor;
import model.Indexer;
import model.Kgram;

public class WildCardTest {

	@Test
	public void test() {
		//Has the indexer index the documents
		Indexer index = new Indexer();

		for(int i = 1; i < 6; i++) {
			File file = new File ("external/doc" + i + ".json");
			DocProcessor dp = new DocProcessor(file);
			int position = 0;

			while (dp.hasNextToken()) {
				List<String> tokens = dp.nextToken();
				for (String each : tokens)
					index.addPosition(each, i-1, position);
				position++;
			}
		}
		String [] terms = index.getDictionary();
		Kgram k_gram_index = new Kgram();
		fillKgram(k_gram_index, terms);
		
	}
	
	public void fillKgram(Kgram index, String [] terms)
	{
		for(String term: terms) {
			index.add(term);
		}
	}

}
