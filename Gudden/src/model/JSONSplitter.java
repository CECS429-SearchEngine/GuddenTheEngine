package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/** 
 * Converts the document with all of the JSON documents into
 * separate JSON documents.
 * @author crystalchun
 *
 */
public class JSONSplitter 
{
	/**The path to the current working directory of this project*/
	private static final Path currentDirectory = Paths.get("").toAbsolutePath();
	/**The Gson object*/
	private static Gson gson;
	
	/**
	 * Constructs a new JSON Splitter object.
	 */
	public JSONSplitter()
	{
		gson = new Gson();
	}
	
	/**
	 * Converts the JSON file into multiple JSON files and stores them
	 * into the output folder on this project's current working directory.
	 * @param file The name of the JSON file to be split.
	 * @param outputFolder The folder to store the split JSON files.
	 */
	public static void convert(String file, String outputFolder)
	{
		// Creates the directory if it hasn't already been created.
		boolean makeDirectory = (new File(outputFolder)).mkdir();
		if(makeDirectory)
		{
			System.out.println("Successfully created folder for files.");
		}
		else
		{
			System.out.println("Error creating a new folder for the files.");
			System.out.println(currentDirectory + "/" + outputFolder + " already exists?");
		}
		
		read(file, outputFolder);
	}
	
	/**
	 * Reads in the JSON file and splits each document within it.
	 * Then writes the documents to a new file in the specified folder.
	 * @param file The name of the file to be split.
	 * @param folder The folder to store the new files.
	 */
	private static void read(String file, String folder)
	{
		// For the name of the output file
		int docNum = 0;
		try
		{
			InputStreamReader in = new InputStreamReader(new FileInputStream(file), "UTF-8");
			JsonReader read = gson.newJsonReader(in);
			
			read.beginObject(); // First token is a begin object 
			String doc = read.nextName(); // Next thing is doc name
			read.beginArray(); // Third token is beginning of array of all docs
			
			// Reads the JSON file
			while(read.hasNext())
			{
				String [] items = new String [6];
				int i = 0;
				read.beginObject(); // Each document is an object
				
				// Reads each document's values
				while(read.hasNext())
				{
					String name = read.nextName();
					items[i] = name;
					
					i++;
					
					String value = read.nextString();
					items[i] = value;
					
					i++;
				}
				
				read.endObject();
				
				write(items, "article" + docNum + ".json", folder);
				
				docNum ++;
			}
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Error, the file does not exist.");
		}
		catch(IOException e)
		{
			System.out.println("Error, something went wrong reading the file.");
		}
	}
	
	
	/**
	 * Writes a JSON document into its separate file.
	 * @param items The items in the JSON document.
	 * @param fileName The name of the file to write the JSON document.
	 * @param folder The folder where this file will be stored.
	 */
	private static void write(String [] items, String fileName, String folder)
	{	
		try 
		{
			PrintWriter out = new PrintWriter(currentDirectory + "/" +folder + "/" + fileName);
			JsonWriter writer = gson.newJsonWriter(out);
			
			System.out.println("Trying to write to " + currentDirectory + "/" + folder + "/" + fileName);
			writer.beginObject();
			
			// Writes values of JSON document to file
			for(int i = 0; i < items.length; i += 2)
			{
				writer.name(items[i]);
				writer.value(items[i+1]);
			}
			
			writer.endObject();
			writer.close();
			
			System.out.println("Successfully wrote to " + currentDirectory + "/" + folder + "/" + fileName);
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("Error, no such file named " + fileName + ".");
		} 
		catch (IOException e) 
		{
			System.out.println("Error writing to file " + fileName + ".");
		}
	}	
}
