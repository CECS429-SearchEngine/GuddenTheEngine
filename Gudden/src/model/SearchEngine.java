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
		fileNames = new ArrayList <String> ();

		// index initial directory
		indexDirectory(index, fileNames, "external/articles/test");
		// System.out.println(index);
		Scanner sc = new Scanner(System.in);
		// String[] inputQueries = getInput();
		// List<Query> queries = getQueries(inputQueries);
		// printQueries(queries);
		boolean done = false;
		while (!done) {
			String query = sc.nextLine();
			String[] command = query.split(" ", 2);
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
					processQuery(query.trim().split("(\\s?\\+\\s?)"), index);
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

	private static void processQuery(String[] queries, Indexer idxr) {
		List<Integer> orResult;
		// Do and for all queries.
		for (String each : queries) {
			List<PositionalPosting> andResult = null;
			Query query = new Query(each);
			for (String token : query.getTokens()) {
				if (token.contains(" ")) {
					// do positional Intersect
					
					String [] tokes = token.split(" ");
					// If the the phrase query contains more than two terms, continue doing positional intersect with result and next phrase's positional postings
					if(tokes.length > 2)
					{
						andResult = postionalIntersect(idxr.getPostings(tokes[0]), idxr.getPostings(tokes[1]), 1);
						for(int i = 2; i < tokes.length; i++)
						{
							andResult = postionalIntersect(andResult, idxr.getPostings(tokes[i]), 1);
						}
					}
					else
					{
						andResult = postionalIntersect(idxr.getPostings(tokes[0]), idxr.getPostings(tokes[1]), 1);
						
					}
					
					for(PositionalPosting a: andResult)
					{
						System.out.println(a.getDocId() + ", ");
					}
					
				} else if (andResult != null) {
					// merge positionalposting and token.
					
				} else {
					andResult = idxr.getPostings(token);
					
				}
			}
		}
//		System.out.println(queries.get(0).getTokens());
	}
	
	private static List<PositionalPosting> postionalIntersect(List<PositionalPosting> p1,
															 List<PositionalPosting> p2,
															 int k) {
		List<PositionalPosting> answer = new ArrayList<PositionalPosting>();
		for (int i = 0, j = 0; i < p1.size() && j < p2.size();) {
			int p1ID = p1.get(i).getDocId();
			int p2ID = p2.get(j).getDocId();
			if (p1ID == p2ID) {
				List<Integer> positions = new ArrayList<Integer>();
				List<Integer> pp1 = p1.get(i).getPositions();
				List<Integer> pp2 = p2.get(j).getPositions();
				for (int ii = 0, jj = 0; ii < pp1.size() && jj < pp2.size();) {
					int positionPP1 = pp1.get(ii);
					int positionPP2 = pp2.get(jj);
					if (Math.abs(positionPP1 - positionPP2) <= k) {
						positions.add(positionPP2);
						jj++;
					} else if (positionPP2 > positionPP1) {
						break;
					}
					while(!positions.isEmpty() && Math.abs(positions.get(0) - positionPP1) > k)
						positions.remove(0);
					
					PositionalPosting posAnswer = new PositionalPosting(p1ID);
					for(int pos: positions) {
						posAnswer.addPosition(pos);
					}
					posAnswer.addPosition(positionPP1);
					answer.add(posAnswer);
					ii++;
				}
				i++;
				j++;
			} else if (p1ID < p2ID) {
				i++;
			} else {
				j++;
			}
		}
		return answer;
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
