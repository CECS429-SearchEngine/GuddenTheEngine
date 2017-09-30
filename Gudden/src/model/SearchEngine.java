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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class SearchEngine {
	private static final String NEAR = "(.*near/\\d.*)";
	
	public static void main(String[] args) throws IOException {
		// the positional index
		Indexer index = new Indexer();
		// the flieNames in the directory
		List<String> fileNames;
		String filePath = "external/articles/test";
		// index initial directory
		fileNames = indexDirectory(index, filePath);
		// System.out.println(index);
		Scanner sc = new Scanner(System.in);
		// String[] inputQueries = getInput();
		// List<Query> queries = getQueries(inputQueries);
		// printQueries(queries);
		boolean done = false;
		while (!done) {
			String input = sc.nextLine();
			String[] command = input.split(" ", 2);
			switch (command[0].toLowerCase()) {
			case ":q":
				done = true;
				break;
			case ":stem":
				stemToken(command[1]);
				break;
			case ":index":
				index.resetIndex();
				// index the new directory path.
				fileNames = indexDirectory(index, command[1]);
				break;
			case ":vocab":
				displayVocabulary(index);
				break;
			default:
				List<Query> queries = getQueries(input.trim().split("(\\s*\\+\\s*)"));
				List<PositionalPosting> result = processQuery(index, queries);
				for (PositionalPosting p : result) {
					System.out.println(fileNames.get(p.getDocId()));
				}
				break;
			}
		}
	}
	
	private static List<PositionalPosting> processQuery(Indexer index, List<Query> queries) {
		LinkedList<List<PositionalPosting>> subQueryResults = new LinkedList<List<PositionalPosting>>();
		for (Query each : queries)
			subQueryResults.add(processSubQuery(index, each));
		return orResult(subQueryResults);
	}

	private static void stemToken(String token) {
		DocProcessor dp = new DocProcessor(token);
		while (dp.hasNextToken())
			System.out.println("Tokens are: " + String.join(", ", dp.nextToken()));
	}

	private static void displayVocabulary(Indexer index) {
		String[] vocabs = index.getDictionary();
		System.out.println(String.join("\n", vocabs));
		System.out.println(vocabs.length);
	}

	private static List<String> indexDirectory(Indexer index, String path) throws IOException {
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
					indexFile(file.toFile(), index, documentID++);
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

	private static List<PositionalPosting> processPhraseQuery(Indexer index, String[] terms) {

		LinkedList<List<PositionalPosting>> result = new LinkedList<List<PositionalPosting>>();
		
		// if phrase was "how are you doing" do the following query:
		//		positionalIntersect(how, are, 1), positionalIntersect(are, you, 1),
		//		positionalIntersect(you, doing, 1).
		for (int i = 1; i < terms.length; i++) {
			List<PositionalPosting> prevPostings = index.getPostings(terms[i - 1]);
			List<PositionalPosting> currPostings = index.getPostings(terms[i]);
			result.add(Indexer.positionalIntersect(prevPostings, currPostings, 1));
		}
		return andResults(result);
	}
	
	private static List<PositionalPosting> processNearQuery(Indexer index, String[] terms) {
		int k = Integer.parseInt(terms[1].split("/")[1]);
		List<PositionalPosting> prevPosting = index.getPostings(terms[0]);
		List<PositionalPosting> currPosting = index.getPostings(terms[2]);
		return Indexer.positionalIntersect(prevPosting, currPosting, k);
	}
	
	private static List<PositionalPosting> andResults(LinkedList<List<PositionalPosting>> result) {
		List<PositionalPosting> andResult = result.poll();
		while (!result.isEmpty()) {
			List<PositionalPosting> nextPosting = result.poll();
			andResult = Indexer.intersect(andResult, nextPosting);
		}
		return andResult;
	}
	
	private static List<PositionalPosting> orResult(LinkedList<List<PositionalPosting>> result) {
		List<PositionalPosting> orResult = result.poll();
		while(!result.isEmpty()) {
			List<PositionalPosting> nextPosting = result.poll();
			orResult = Indexer.union(orResult, nextPosting);
		}
		return orResult;
	}
	private static List<PositionalPosting> processSubQuery(Indexer index, Query query) {
		LinkedList<List<PositionalPosting>> result = new LinkedList<List<PositionalPosting>>(); 
		for (String token : query.getTokens()) {
			// check for near query
			if (token.matches(NEAR)) {
				result.add(processNearQuery(index, token.split("\\s+")));
			}
			// check for phrase query
			else if (token.contains(" ")) {
				result.add(processPhraseQuery(index, token.split("\\s+")));
			}
			// add posting to query.
			else {
				result.add(index.getPostings(token));
			}
		}
		
		return andResults(result);
	}
	
	private static List<Query> getQueries(String[] queryInputs) {
		// Query: "hello world" good near/3 bye + good "easy good" + "gooden java" + good
		// SubQuery1: hello world, good near/3 bye
		// SubQuery2: good, easi good
		// SubQuery3: gooden java
		// SubQuery4: good
		
		List<Query> queries = new ArrayList<Query>();
		for (String queryInput : queryInputs) {
			if (queryInput.matches(NEAR)) {
				String[] input = queryInput.split("\\s+");
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < input.length; i++) {
					if (i < input.length - 1 && input[i + 1].matches(NEAR)) {
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
