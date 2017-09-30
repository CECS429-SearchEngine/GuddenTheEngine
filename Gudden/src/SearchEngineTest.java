import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import model.DocProcessor;
import model.Indexer;
import model.SearchEngine;

public class SearchEngineTest {

	@Test
	public void testLiteralQuery1() throws IOException {
		String queries = "a fishbone";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(fileNames().get(1), results.get(0));
	}
	
	@Test
	public void testLiteralQuery2() throws IOException {
		String queries = "dog";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(fileNames().get(0), results.get(0));
		assertEquals(fileNames().get(2), results.get(1));
		assertEquals(fileNames().get(3), results.get(2));
	}
	
	@Test
	public void testLiteralQuery3() throws IOException {
		String queries = "the fox jumps";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(fileNames().get(0), results.get(0));
	}
	
	@Test
	public void testLiteralORQuery1() throws IOException {
		String queries = "fox + lives";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(fileNames().get(0), results.get(0));
		assertEquals(fileNames().get(1), results.get(1));
		assertEquals(fileNames().get(3), results.get(2));
	}
	
	@Test
	public void testLiteralORQuery2() throws IOException {
		String queries = "in + over";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(fileNames().get(0), results.get(0));
		assertEquals(fileNames().get(1), results.get(1));
	}
	
	@Test
	public void testLiteralORQuery3() throws IOException {
		String queries = "with + fox jumps";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(fileNames().get(0), results.get(0));
		assertEquals(fileNames().get(2), results.get(1));
	}
	
	@Test
	public void testPhrase1() throws IOException {
		String queries = "\"the fox jumps\"";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(0, results.size());
	}
	
	@Test
	public void testPhrase2() throws IOException {
		String queries = "\"a fishbone\"";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(fileNames().get(1), results.get(0));
	}
	
	@Test
	public void testPhrase3() throws IOException {
		String queries = "\"catdog is a catdog\"";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(fileNames().get(1), results.get(0));
	}
	
	@Test
	public void testORPhrase1() throws IOException {
		String queries = "\"the dog digs the\" + \"with the dog\"";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(fileNames().get(2), results.get(0));
		assertEquals(fileNames().get(3), results.get(1));
	}
	
	@Test
	public void testORPhrase2() throws IOException {
		String queries = "\"the dog\" + \"the fox\"";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(fileNames().get(0), results.get(0));
		assertEquals(fileNames().get(2), results.get(1));
		assertEquals(fileNames().get(3), results.get(2));
	}
	
	@Test
	public void testORPhrase3() throws IOException {
		String queries = "\"the dog jump over\" + \"catdog is a catdog\" + \"cat plays\" ";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(fileNames().get(0), results.get(0));
		assertEquals(fileNames().get(1), results.get(1));
		assertEquals(fileNames().get(2), results.get(2));
	}
	
	@Test
	public void testORPhrase4() throws IOException {
		String queries = "\"the dog jump over\" + \"the dog\" \"cat plays\" ";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(fileNames().get(0), results.get(0));
		assertEquals(fileNames().get(2), results.get(1));
	}
	
	@Test
	public void testORPhrase5() throws IOException {
		String queries = "the \"jump over\" + \"catdog is a catdog\" + \"cat plays\" with ";
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);
		assertEquals(fileNames().get(0), results.get(0));
		assertEquals(fileNames().get(1), results.get(1));
		assertEquals(fileNames().get(2), results.get(2));
	}
	
	@Test
	public void testORPhrase6() throws IOException {
		String queries;
		List<String> results = SearchEngine.queryResults(fileNames(), populateIndexer(testPathList()), queries);
		printResults(results);

	}
	
	private List<String> testPathList() {
		List<String> fileNames = new ArrayList<String>();
		String folderPath = "external\\testcorpus\\Andrew\\json\\";
		for(int x = 1; x <= 4; x++) 
			fileNames.add(folderPath + "article"+x+".json");
		return fileNames;
	}
	
	private List<String> fileNames(){
		List<String> fileNames = new ArrayList<String>();
		fileNames.add("article1.json");
		fileNames.add("article2.json");
		fileNames.add("article3.json");
		fileNames.add("article4.json");
		
		return fileNames;
	}
	
	private Indexer populateIndexer(List<String> pathList) {
		Indexer index = new Indexer();
		int docID = 0;
		for(String e : pathList) {
			//File file = new File("external/doc" + i + ".json"));
			File file = new File (e);
			DocProcessor docs = new DocProcessor(file);
			int position = 0;
			while(docs.hasNextToken()) {
				List<String> tokens = docs.nextToken();
				for(String term : tokens)
					index.addPosition(term, docID, position++);
			}
			docID++;

		}
		return index;
		
	}
	
	private void printResults(List<String> results) {
		System.out.printf("files: %s\n", String.join(", ", results));
	}

}
