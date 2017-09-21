package controller;

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

import model.Document;

public class JsonDocumentParser {

	private String filePath;
	private String articlePath;

	public JsonDocumentParser() {
		this.filePath = null;
		this.articlePath = null;
	}

	public JsonDocumentParser(String filePath, String articlePath) {
		this.filePath = filePath;
		this.articlePath = articlePath;
	}

	private JsonObject parseToObject(JsonReader reader) {
		JsonParser parser = new JsonParser();
		return parser.parse(reader).getAsJsonObject();
	}
	
	private JsonReader createReader(InputStream in) throws IOException {
		return new JsonReader(new InputStreamReader(in, "UTF-8"));
	}
	
	private JsonWriter createWriter(OutputStream out) throws IOException {
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
		writer.setIndent("  ");
		return writer;
	}

	private void createDocumentJson(int n, JsonElement e, Gson gson) throws IOException {
		Document doc = gson.fromJson(e, Document.class);
		JsonWriter writer = createWriter(new FileOutputStream(this.articlePath + (n + 1) + ".json"));
		gson.toJson(doc, Document.class, writer);
		writer.close();
	}

	private boolean pathsAreSet() {
		return this.filePath != null && this.articlePath != null;
	}

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
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public void setArticlePath(String articlePath) {
		this.articlePath = articlePath;
	}

	public static void main(String[] args) throws IOException {
		String filepath = "all-nps-sites.json";
		String articlepath = "articles/article";
		new JsonDocumentParser(filepath, articlepath).separateDocuments("documents");
	}
}
