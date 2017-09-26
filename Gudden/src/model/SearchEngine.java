package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchEngine {
	
	public static void main(String[] args) {
		final List<String> fileNames = new ArrayList<String>();
		final Indexer indexer = new Indexer();
		final DocProcessor docProc = new DocProcessor("external/articles/test");
		String[] inputQueries = getInput();
		List<Query> queries = getQueries(inputQueries);
		printQueries(queries);
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
