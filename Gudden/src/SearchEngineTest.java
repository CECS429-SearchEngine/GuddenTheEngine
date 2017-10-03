import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import model.DocProcessor;
import model.Indexer;
import model.SearchEngine;

public class SearchEngineTest {
	/*	Test Documents List in path1:
	  		Index 0 article1.json: the dog jumped over the fox
			Index 1 article2.json: catdog is a catdog that lives in a fishbone
			Index 2 article3.json: the cat plays with the dog
			Index 3 article4.json: when the dog digs, the fox follows
			
		Test Documents List in path2:
	  		Index 0 article1.json: The monumental raid on parks was enjoyable.
			Index 1 article2.json: Washington’s raid on the British went well.
			Index 2 article3.json: The well-known Washington Monument is in Washington.
			Index 3 article4.json: There are many dogs in Seattle, Washington.
			Index 4 article5.json: Dogs enjoy the dog park.
	  
	 */
	
	String path1 = "external\\testcorpus\\Andrew\\json\\";
	Indexer index1 = new Indexer();
	List<String> fileList1 = indexDirectory(index1, path1);
	List<String> pathList1 = pathList(path1, fileList1);
	
	String path2 = "external\\testcorpus\\Crystal\\json\\";
	Indexer index2 = new Indexer();
	List<String> fileList2 = indexDirectory(index2, path2);
	List<String> pathList2 = pathList(path2, fileList2);

	
	private List<String> pathList(String folderPath, List<String> files) {
		List<String> fileNames = new ArrayList<String>();
		for(String file : files) 
			fileNames.add(folderPath + file);
		return fileNames;
	}
	
	private static List<String> indexDirectory(Indexer index, String path){
		final Path currentWorkingPath = Paths.get(path).toAbsolutePath();

		// the list of file names that were processed
		List<String> fileNames = new ArrayList<String>();

		// This is our standard "walk through all .json files" code.
		try {
		Files.walkFileTree(currentWorkingPath, new SimpleFileVisitor<Path>() {
			int documentID = 0;

			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				// make sure we only process the current working directory.
				if (currentWorkingPath.equals(dir)) {
					return FileVisitResult.CONTINUE;
				}
				return FileVisitResult.SKIP_SUBTREE;
			}

			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				// only process .json files
				if (file.toString().endsWith(".json")) {
					// we have found a .json file; add its name to the fileName list,
					// then idnex the file and increase the document ID counter.
					fileNames.add(file.getFileName().toString());
					indexFile(file.toFile(), index, documentID++);
				}
				return FileVisitResult.CONTINUE;
			}

			// don't throw exceptions if files are locked/other errors occur
			public FileVisitResult visitFileFailed(Path file, IOException e) {
				return FileVisitResult.CONTINUE;
			}
		});
		}catch(Exception e) {e.printStackTrace();}

		return fileNames;
	}
	
	private static void indexFile(File file, Indexer index, int docId) {
		DocProcessor dp = new DocProcessor(file);
		int position = 0;
		while (dp.hasNextToken()) {
			List<String> tokens = dp.nextToken();
			for (String each : tokens)
				index.addPosition(each, docId, position);
			position++;
		}
	}
	
	private void printResults(List<String> results) {
		System.out.printf("files: %s\n", String.join(", ", results));
	}

	
	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	
	@Test
	public void testLiteralQuery1() throws IOException {
		String queries = "a fishbone";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.get(0), fileList1.get(1));
	}
	
	@Test
	public void testLiteralQuery2() throws IOException {
		String query = "dog";
		List<String> results = SearchEngine.queryResults(fileList1,index1, query);
		assertEquals(results.get(0), fileList1.get(0));
		assertEquals(results.get(1), fileList1.get(2));
		assertEquals(results.get(2), fileList1.get(3));
	}
	
	@Test
	public void testLiteralQuery3() throws IOException {
		String queries = "the fox jumps";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.get(0), fileList1.get(0));
	}
	
	@Test
	public void testLiteralORQuery1() throws IOException {
		String queries = "fox + lives";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.get(0), fileList1.get(0));
		assertEquals(results.get(1), fileList1.get(1));
		assertEquals(results.get(2), fileList1.get(3));
	}
	
	@Test
	public void testLiteralORQuery2() throws IOException {
		String queries = "in + over";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.get(0), fileList1.get(0));
		assertEquals(results.get(1), fileList1.get(1));
	}
	
	@Test
	public void testLiteralORQuery3() throws IOException {
		String queries = "with + fox jumps";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.get(0), fileList1.get(0));
		assertEquals(results.get(1), fileList1.get(2));
	}
	
	@Test
	public void testPhrase1() throws IOException {
		String queries = "\"the fox jumps\"";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.size(), 0);
	}
	
	@Test
	public void testPhrase2() throws IOException {
		String queries = "\"a fishbone\"";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.get(0), fileList1.get(1));
	}
	
	@Test
	public void testPhrase3() throws IOException {
		String queries = "\"catdog is a catdog\"";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.get(0), fileList1.get(1));
	}
	
	@Test
	public void testPhrase4() throws IOException {
		String queries = "\"Washington Monument\"";
		List<String> results = SearchEngine.queryResults(fileList2,index2, queries);
		assertEquals(results.get(0), fileList2.get(1));
	}
	
	@Test
	public void testPhrase5() throws IOException {
		String queries = "\"Washington Seattle\"";
		List<String> results = SearchEngine.queryResults(fileList2,index2, queries);
		printResults(results);
		assertEquals(results.size(), 0);
	}
	
	@Test
	public void testPhrase6() throws IOException {
		String queries = "\"Dogs enjoy the\"";
		List<String> results = SearchEngine.queryResults(fileList2,index2, queries);
		assertEquals(results.get(0), fileList2.get(4));
	}
	
	@Test
	public void testORPhrase1() throws IOException {
		String queries = "\"the dog digs the\" + \"with the dog\"";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.get(0), fileList1.get(2));
		assertEquals(results.get(1), fileList1.get(3));
	}
	
	@Test
	public void testORPhrase2() throws IOException {
		String queries = "\"the dog\" + \"the fox\"";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.get(0), fileList1.get(0));
		assertEquals(results.get(1), fileList1.get(2));
		assertEquals(results.get(2), fileList1.get(3));
	}
	
	@Test
	public void testORPhrase3() throws IOException {
		String queries = "\"the dog jump over\" + \"catdog is a catdog\" + \"cat plays\" ";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.get(0), fileList1.get(0));
		assertEquals(results.get(1), fileList1.get(1));
		assertEquals(results.get(2), fileList1.get(2));
	}
	
	@Test
	public void testORPhrase4() throws IOException {
		String queries = "\"the dog jump over\" + \"the dog\" \"cat plays\" ";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.get(0), fileList1.get(0));
		assertEquals(results.get(1), fileList1.get(2));
	}
	
	@Test
	public void testORPhrase5() throws IOException {
		String queries = "the \"jump over\" + \"catdog is a catdog\" + \"cat plays\" with ";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.get(0), fileList1.get(0));
		assertEquals(results.get(1), fileList1.get(1));
		assertEquals(results.get(2), fileList1.get(2));
	}
	
	@Test
	public void testORPhrase6() throws IOException {
		String queries = "the dog + \"cat plays\" + a \"the fox\"";
		List<String> results = SearchEngine.queryResults(fileList1,index1, queries);
		assertEquals(results.get(0), fileList1.get(0));
		assertEquals(results.get(1), fileList1.get(2));
	}
	
}
