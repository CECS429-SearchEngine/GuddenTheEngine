package model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * A Json Document Parser that can read JSON documents and manipulate them.
 */
public class JsonDocumentParser {

	/** The directory of the documents */
	private String filePath;
	/** The name of the file (document) */
	private String articlePath;

	/**
	 * Constructs a JsonDocumentParser with both filePath and articlePath set to
	 * null.
	 */
	public JsonDocumentParser() {
		this.filePath = null;
		this.articlePath = null;
	}

	/**
	 * Constructs a JsonDocumentParser with the specified filePath and articlePath
	 * 
	 * @param filePath
	 *            The directory path.
	 * @param articlePath
	 *            The file name.
	 */
	public JsonDocumentParser(String filePath, String articlePath) {
		this.filePath = filePath;
		this.articlePath = articlePath;
	}

	/**
	 * Separates documents within one large Json file.
	 * 
	 * @param root
	 *            The root
	 * @throws IOException
	 */
	public void separateDocuments(String root) throws IOException {
		if (!pathsAreSet()) {
			throw new NullPointerException("filePath and articlePath must be set.");
		}

		JsonArray documents = parseToObject(createReader(new FileInputStream(this.filePath))).getAsJsonArray(root);
		Gson gson = new Gson();
		int counter = 0;
		for (JsonElement e : documents) {
			createDocumentJson(counter++, e, gson);
		}
	}

	/**
	 * Gets one document from the specified filePath with the specified articlePath
	 * name.
	 * 
	 * @return A document object representing the Json document.
	 * @throws IOException
	 */
	public Document getDocument() throws IOException {
		JsonObject documents = parseToObject(createReader(new FileInputStream(this.filePath + "/" + this.articlePath)));
		Gson gson = new Gson();
		Document doc = gson.fromJson(documents, Document.class);
		return doc;
	}

	/**
	 * Sets the directory path.
	 * 
	 * @param filePath
	 *            The directory path
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * Sets the file name.
	 * 
	 * @param articlePath
	 *            The file name
	 */
	public void setArticlePath(String articlePath) {
		this.articlePath = articlePath;
	}

	/**
	 * Parses a JsonReader to a JsonObject.
	 * 
	 * @param reader
	 *            The JsonReader
	 * @return A JsonObject
	 */
	private JsonObject parseToObject(JsonReader reader) {
		JsonParser parser = new JsonParser();
		return parser.parse(reader).getAsJsonObject();
	}

	/**
	 * Creates a JsonReader.
	 * 
	 * @param in
	 *            The input stream
	 * @return A JsonReader
	 * @throws IOException
	 */
	private JsonReader createReader(InputStream in) throws IOException {
		return new JsonReader(new InputStreamReader(in, "UTF-8"));
	}

	/**
	 * Creates a JsonWriter.
	 * 
	 * @param out
	 *            The output stream
	 * @return A JsonWriter
	 * @throws IOException
	 */
	private JsonWriter createWriter(OutputStream out) throws IOException {
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
		writer.setIndent("  ");
		return writer;
	}

	/**
	 * Creates a Json Document with the specified articlePath name.
	 * 
	 * @param n
	 *            This document's number
	 * @param e
	 *            The Json element
	 * @param gson
	 *            The Gson object
	 * @throws IOException
	 */
	private void createDocumentJson(int n, JsonElement e, Gson gson) throws IOException {
		Document doc = gson.fromJson(e, Document.class);
		JsonWriter writer = createWriter(new FileOutputStream(this.articlePath + (n + 1) + ".json"));
		gson.toJson(doc, Document.class, writer);
		writer.close();
	}

	/**
	 * Checks to see if both paths (filePath and articlePath) are set.
	 * 
	 * @return True if both paths are not null.
	 */
	private boolean pathsAreSet() {
		return this.filePath != null && this.articlePath != null;
	}
}
