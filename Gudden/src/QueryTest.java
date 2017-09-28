import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

import model.DocProcessor;
import model.Indexer;

public class QueryTest {

	@Test
	public void test() {
		// Creates the index
		Indexer index = new Indexer();
		for(int i = 1; i < 6; i++) {
			DocProcessor docs = new DocProcessor(new File("external/doc" + i + ".json"));
			List <String> terms = docs.process();
			for(int j = 0; j < terms.size(); j++) {
				index.addPosition(terms.get(j), i-1, j);
			}
		}
		
	}

}
