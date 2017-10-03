package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class SearchEngine {
	private static final String NEAR_REGEX = "(.*near/\\d.*)";
	private static final Indexer INDEX = new Indexer();
	private static final KGramIndex KGRAM_INDEX = new KGramIndex();
	private static final String RESULT_FORMAT = "Files Matching '%s' are:\n\n%s\n\n";
	
	public static void main(String[] args) throws IOException {
		// the positional index
		// the flieNames in the directory
		List<String> fileNames;
		String filePath = args[0];//"external/articles/test";
		// index initial directory
		fileNames = indexDirectory(filePath);
		// System.out.println(index);
		Scanner sc = new Scanner(System.in);
		// String[] inputQueries = getInput();
		// List<Query> queries = getQueries(inputQueries);
		// printQueries(queries);
		boolean done = false;
		while (!done) {
			String input = sc.nextLine().trim();
			String[] command = input.split(" ", 2);
			switch (command[0].toLowerCase()) {
			case ":q":
				done = true;
				break;
			case ":stem":
				stemToken(command[1]);
				break;
			case ":index":
				INDEX.resetIndex();
				// index the new directory path.
				fileNames = indexDirectory(command[1]);
				break;
			case ":vocab":
				displayVocabulary();
				break;
			default:
				List<String> results = queryResults(fileNames, input);
				if (results.isEmpty())
					System.out.println("No files were found.");
				else
					System.out.printf(RESULT_FORMAT, input, String.join(", ", results));
				break;
			}
		}
	}

	public static List<PositionalPosting> processPhraseQuery(String[] terms, Indexer index) {

		LinkedList<List<PositionalPosting>> result = new LinkedList<List<PositionalPosting>>();
		
		// if phrase was "how are you doing" do the following query:
		//		positionalIntersect(how, are, 1), positionalIntersect(are, you, 1),
		//		positionalIntersect(you, doing, 1).
		for (int i = 1; i < terms.length; i++) {
			List<PositionalPosting> prevPostings = index.getPostings(terms[i - 1]);
			List<PositionalPosting> currPostings = index.getPostings(terms[i]);
			result.add(Indexer.positionalIntersect(prevPostings, currPostings, 1));
		}
		return andPositionalPosting(result);
	}
	
	public static List<PositionalPosting> processNearQuery(String[] terms, Indexer index) {
		int k = Integer.parseInt(terms[1].split("/")[1]);
		List<PositionalPosting> prevPosting = index.getPostings(terms[0]);
		List<PositionalPosting> currPosting = index.getPostings(terms[2]);
		return Indexer.positionalIntersect(prevPosting, currPosting, k);
	}
	
	public static List<String> queryResults(List<String> fileNames, String input) {
		
		ArrayList<String> fileNameResults = new ArrayList<String>();
		
		List<Query> queries = createQueries(input.trim().split("(\\s*\\+\\s*)"));
		List<PositionalPosting> result = processQuery(queries);
		if (result != null)
			for (PositionalPosting p : result)
				fileNameResults.add(fileNames.get(p.getDocId()));
		return fileNameResults;
	}
	
	public static PriorityQueue<String> processKGramQuery(String[] grams, KGramIndex kGramIndex) {
		LinkedList<PriorityQueue<String>> result = new LinkedList<PriorityQueue<String>>();
		for (String each : grams) {
			if (!each.equals("$")) {
				if (each.length() > 3) {
					List<String> subGrams = kGramIndex.generateKGrams(3, each);
					result.add(processKGramQuery(subGrams.toArray(new String[subGrams.size()]), kGramIndex));
				} else {
					result.add(kGramIndex.getPostings(each));
				}
			}
		}
		PriorityQueue<String> postingsResult = result.poll();
		while (!result.isEmpty()) {
			PriorityQueue<String> otherPostingsResult = result.poll();
			postingsResult = KGramIndex.intersect(postingsResult, otherPostingsResult);
		}
		return postingsResult;
	}
	
	private static List<PositionalPosting> processQuery(List<Query> queries) {
		LinkedList<List<PositionalPosting>> subQueryResults = new LinkedList<List<PositionalPosting>>();
		for (Query each : queries) {
			List<String> tokens = each.getTokens();
			LinkedList<List<PositionalPosting>> results = processSubQuery(each.getTokens());
			if (tokens.size() > 1) {
				subQueryResults.add(andPositionalPosting(results));
			} else {
				for (List<PositionalPosting> e : results)
					if (e != null) subQueryResults.add(e);
			}
		}
		return orPositionalPosting(subQueryResults);
	}

	private static void stemToken(String token) {
		DocProcessor dp = new DocProcessor(token);
		while (dp.hasNextToken())
			System.out.println("Tokens are: " + String.join(", ", dp.nextToken()));
	}

	private static void displayVocabulary() {
		String[] vocabs = INDEX.getDictionary();
		System.out.println(String.join("\n", vocabs));
		System.out.println(vocabs.length);
	}

	private static List<String> indexDirectory(String path) throws IOException {
		final Path currentWorkingPath = Paths.get(path).toAbsolutePath();

		// the list of file names that were processed
		List<String> fileNames = new ArrayList<String>();

		// This is our standard "walk through all .json files" code.
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
					indexFile(file.toFile(), documentID++);
				}
				return FileVisitResult.CONTINUE;
			}

			// don't throw exceptions if files are locked/other errors occur
			public FileVisitResult visitFileFailed(Path file, IOException e) {
				return FileVisitResult.CONTINUE;
			}
		});

		return fileNames;
	}

	private static void indexFile(File file, int docId) {
		DocProcessor dp = new DocProcessor(file);
		int position = 0;
		while (dp.hasNextToken()) {
			List<String> tokens = dp.nextToken();
			for (String each : tokens) {
				INDEX.addPosition(each, docId, position);
				KGRAM_INDEX.addToken(each);
			}
			position++;
		}
	}

	private static List<PositionalPosting> andPositionalPosting(LinkedList<List<PositionalPosting>> result) {
		if (result.peek() == null) return null;
		List<PositionalPosting> andResult = result.poll();
		while (!result.isEmpty()) {
			List<PositionalPosting> nextPosting = result.poll();
			andResult = Indexer.intersect(andResult, nextPosting);
		}
		return andResult;
	}
	
	
	
	private static List<PositionalPosting> orPositionalPosting(LinkedList<List<PositionalPosting>> result) {
		if (result.peek() == null) return null;
		List<PositionalPosting> orResult = result.poll();
		while(!result.isEmpty()) {
			List<PositionalPosting> nextPosting = result.poll();
			orResult = Indexer.union(orResult, nextPosting);
		}
		return orResult;
	}
	
	private static LinkedList<List<PositionalPosting>> processSubQuery(List<String> tokens) {
		LinkedList<List<PositionalPosting>> result = new LinkedList<List<PositionalPosting>>(); 
		for (String token : tokens) {
			// check for near query
			if (token.matches(NEAR_REGEX)) {
				result.add(processNearQuery(token.split("\\s+"), INDEX));
			}
			// check for kgram query
			else if (token.contains("*")) {
				List<String> subTokens;
				String[] grams = KGramIndex.encapsulateToken(token).split("\\*");
				String originalQuery = String.join(".*", grams).replaceAll("\\$", "");
				PriorityQueue<String> kGramResult = processKGramQuery(grams, KGRAM_INDEX);
				if (kGramResult != null) {
					subTokens = KGramIndex.postFilter(kGramResult, originalQuery);
					result.addAll(processSubQuery(subTokens));
				}
			}
			// check for phrase query
			else if (token.contains(" ")) {
				result.add(processPhraseQuery(token.split("\\s+"), INDEX));
			}
			// add posting to query.
			else {
				result.add(INDEX.getPostings(token));
			}
		}
		return result;
	}
	
	private static List<Query> createQueries(String[] queryInputs) {
		// Query: "hello world" good near/3 bye + good "easy good" + "gooden java" + good
		// SubQuery1: hello world, good near/3 bye
		// SubQuery2: good, easi good
		// SubQuery3: gooden java
		// SubQuery4: good
		
		List<Query> queries = new ArrayList<Query>();
		for (String queryInput : queryInputs) {
			StringBuilder sb = new StringBuilder();
			if (queryInput.matches(NEAR_REGEX)) {
				String[] input = queryInput.split("\\s+");
				for (int i = 0; i < input.length; i++) {
					if (i < input.length - 1 && input[i + 1].matches(NEAR_REGEX)) {
						sb.append(' ');
						sb.append('"');
						sb.append(input[i]);
						sb.append(' ');
						sb.append(input[++i]);
						sb.append(' ');
						sb.append(input[++i]);
						sb.append('"');
						sb.append(' ');
					} else {
						sb.append(' ');
						sb.append(input[i]);
						sb.append(' ');
					}
				}
				queries.add(new Query(sb.toString().trim()));
			} else {
				queries.add(new Query(queryInput));
			}
		}
		return queries;
	}
}
