package net.etfbl.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Book {
	private String id;
	private String title;
	private String author;
	private String publicationDate;
	private String language;
	private static int i=0;
	private String content;
	private String urlPath;

	public Book() {
		this.id = ++i+"";
	}
	public Book(String title, String author, String publicationDate, String language,String content,String urlPath) {
		super();
		this.id = ++i+"";
		this.title = title;
		this.author = author;
		this.publicationDate = publicationDate;
		this.language = language;
		this.content=content;
		this.urlPath=urlPath;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUrlPath() {
		return urlPath;
	}
	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}
	@Override
	public String toString() {
		return "Book [id=" + id+ " title=" + title + ", author=" + author + ", publicationDate=" + publicationDate + ", language="
				+ language + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return Objects.equals(id, other.id);
	}

	public HashMap<String, String> toMap() {
		HashMap<String, String> obj = new HashMap<>();
		obj.put("id", this.id);
		obj.put("title", title);
		obj.put("author", author);
		obj.put("publicationDate", publicationDate);
		obj.put("language", language);
		obj.put("content",content);
		obj.put("urlPath",urlPath);
		return obj;
	}
	
	public static Book fromMap(Map<String, String> map) {
	    Book book = new Book();
	    book.setId(map.get("id")); 
	    book.setTitle(map.get("title"));
	    book.setAuthor(map.get("author"));
	    book.setPublicationDate(map.get("publicationDate"));
	    book.setLanguage(map.get("language"));
	    book.setContent(map.get("content"));
	    book.setUrlPath("urlPath");
	    return book;
	}

}
