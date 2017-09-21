package model;

import java.util.HashSet;
import java.util.Set;

public class ProcessedDocument {
	private int docId;
	private Set<String> terms;
	
	public ProcessedDocument(int docId, Document doc) {
		this.docId = docId;
		this.terms = new HashSet<String>();
	}
}