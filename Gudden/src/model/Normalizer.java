package model;

import java.util.HashSet;
import java.util.Set;

public class Normalizer {
	
	public static String trimNonAlphanumeric(String token) {
		return token.replaceAll("^\\W*", "").replaceAll("\\W*$", "");
	}
	
	public static String removeApostrophe(String token) {
		return token.replaceAll("\'", "");
	}
	
	public static Set<String> splitHypenWords(String token) {
		Set<String> tokenSet = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		String[] tokens = token.split("-");
		for (String each : tokens) {
			tokenSet.add(each);
			sb.append(each);
		}
		tokenSet.add(sb.toString());
		return tokenSet;
	}
	
	public static String stemToken(String token) {
		Stemmer stemmer = new Stemmer();
		stemmer.add(token.toCharArray(), token.length());
		stemmer.stem();
		return stemmer.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(trimNonAlphanumeric("\"Explore This Park    Learn About the Park     Photos \\u0026 Multimedia     Photo Gallery              Photo Gallery                       Historic Portraits   4 Photos  Photographs of prominent men and women of the Sand Creek Massacre and associated events.          Science in the Park   4 Photos  Scientists and the National Park Service conducted research to learn more about the Sand Creek Massacre and the landscape before the park opened in 2007.          Sand Creek Massacre NHS Brochure Gallery   2 Photos  Sand Creek Massacre National Historic Site Brochures in .jpg format for easier viewing.            Nature - Plants and Animals   2 Photos  Select images of the plants and animals that inhabit the park.\""));
		System.out.println(trimNonAlphanumeric(" '    \"They're\""));
		Set<String> ts = splitHypenWords("Hewlett-Packard");
		for (String each : ts) {
			System.out.println(each);
		}
	}
}
