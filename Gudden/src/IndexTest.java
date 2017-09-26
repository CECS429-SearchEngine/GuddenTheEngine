import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import model.DocProcessor;
import model.Document;
import model.Indexer;
import model.PositionalPosting;

public class IndexTest {

	@Test
	public void test() {
		DocProcessor docs = new DocProcessor("external/");
		Indexer index = new Indexer();
		for(int i = 1; i < 6; i++) {
			List <String> terms = docs.process("doc" + i + ".json");
			for(int j = 0; j < terms.size(); j++) {
				index.addPosition(terms.get(j), i-1, j);
			}
		}
		System.out.println(index);
		assertEquals(21, index.getTermCount());
		String [] terms = {"the","monument","raid","on","park",""};
		/*Create positional posting for all terms here*/
		Indexer handBuilt = new Indexer();
		handBuilt.addPosition("the", 0, 0);
		
		
		for(int i = 0; i < index.getPostings("the").size(); i ++) {
			assertEquals(handBuilt.getPostings(""), index.getPostings("the").get(i));
		}
	}

}
