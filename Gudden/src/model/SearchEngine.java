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
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;

public class SearchEngine {

	public static void main(String[] args) {
		// the positional index
		Indexer index = new Indexer();

		// the flieNames in the directory
		List<String> fileNames = null;

		// index initial directory
		indexDirectory(index, fileNames, "external/articles/test");
		// System.out.println(index);
		Scanner sc = new Scanner(System.in);

		// String[] inputQueries = getInput();
		// List<Query> queries = getQueries(inputQueries);
		// printQueries(queries);
		boolean done = false;
		while (!done) {
			String[] command = sc.nextLine().split(" ", 2);
			for (String each : command) {
				System.out.println(each);
			}
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
					indexDirectory(index, fileNames, command[1]);
					break;
				case ":vocab":
					displayVocabulary(index);
					break;
				default:
					System.out.println("in deafult");
					break;
			}
		}
	}

	private static void stemToken(String token) {
		DocProcessor dp = new DocProcessor(token);
		while (dp.hasNextToken())
			System.out.println("Tokens are: " + String.join(", ", dp.nextToken()));
	}

	private static void indexDirectory(Indexer index, List<String> fileNames, String path) {
		try {
			fileNames = processDocuments(index, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void displayVocabulary(Indexer index) {
		String[] vocabs = index.getDictionary();
		System.out.println(String.join("\n", vocabs));
		System.out.println(vocabs.length);
	}

	private static List<String> processDocuments(Indexer index, String path) throws IOException {
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

	private static void processQuery(List<Query> queries) {
		for (Query each : queries) {
		}
	}

	private static List<Query> getQueries(String[] inputQueries) {
		List<Query> queries = new ArrayList<Query>();
		for (String each : inputQueries)
			queries.add(new Query(each));
		return queries;
	}

	private static String[] getInput() {
		Scanner sc = new Scanner(System.in);
		return sc.nextLine().trim().split("(\\s?\\+\\s?)");
	}

	private static void printQueries(List<Query> queries) {
		for (Query x : queries) {
			System.out.print(x.getTokens().size() + " of tokens in query: ");
			for (String y : x.getTokens()) {
				System.out.print(y + ",");
			}
			System.out.println();
		}
	}
}
