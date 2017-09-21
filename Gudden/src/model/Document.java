package model;


public class Document {
	private String url;
	private String body;
	private String title;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Title: ");
		sb.append(title);
		return sb.toString();
	}
}
