package model;

import java.io.File;
import java.util.List;

public class main {
	public static void main(String[] args) {
		String filepath = "external/articles/test";
		File folder = new File(filepath);
		Indexer idxr = new Indexer();
		File[] listOfFiles = folder.listFiles();
		DocProcessor processor = new DocProcessor(folder.getAbsolutePath());
		for (int i = 0; i < listOfFiles.length; i++) {
			List<String> s = processor.process(listOfFiles[i].getName());
			for (int j = 0; j < s.size(); j++)
				idxr.addPosition(s.get(j), i, j);
		}
		System.out.println(idxr);
		// DocProcess proc = new DocProcess();
		// proc.process("Hello");
		/*
		 * proc.process("H'e'l'l'o'"); proc.process("Hel'lo"); proc.process("He'''llo");
		 * proc.process("Hello'"); proc.process("''''Hello"); proc.process("'Hello'");
		 * /*proc.process("   Hello   "); proc.process("90Hello");
		 * proc.process("!90Hello"); proc.process(" !90Hello");
		 * proc.process("Hello!90"); proc.process("Hello90!");
		 * proc.process("Hello90!   9"); proc.process("Hello90!   9***!!!");
		 * proc.process("*&@#$Hello90!@*#$(@");
		 */
	}
}
