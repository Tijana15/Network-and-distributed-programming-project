package net.etfbl.service;

import net.etfbl.model.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BookService {
	public ArrayList<Book> books = new ArrayList<>();
	private final JedisPool jedisPool;
	private final String instanceName;

	public BookService(JedisPool jedisPool, String instanceName) {
		this.jedisPool = jedisPool;
		this.instanceName = instanceName;
	}

	public boolean saveBookAsMap(Book book) {
		try (Jedis jedis = jedisPool.getResource()) {
				System.out.println(book);
				System.out.println(book.getId());
				jedis.hmset(instanceName + ":books:map:" +book.getId(),book.toMap());
				return true;
		}
		
	}
	public Book loadBookById(String bookId) {
	    try (Jedis jedis = jedisPool.getResource()) {
	        Map<String, String> bookData = jedis.hgetAll(instanceName + ":books:map:" + bookId);

	        if (bookData.isEmpty()) {
	            return null; 
	        }
	        return Book.fromMap(bookData);
	    }
	}


	public void deleteInstance() {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.del(instanceName);
		}
	}


	public List<Map<String, String>> getAllBooksAsMaps() {
		List<Map<String, String>> books = new ArrayList<>();
		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> keys = jedis.keys(instanceName + ":books:map:*");
			for (String key : keys) {
				books.add(jedis.hgetAll(key));
			}
		}
		return books;
	}

	public boolean updateBook(Book book, String id) {
		try (Jedis jedis = jedisPool.getResource()) {
			String key = instanceName + ":books:map:" + id;
			
			if (jedis.exists(key)) {
				jedis.hmset(key,book.toMap());
				System.out.println("Book with id "+id+" updated");
				return true;
			} else {
				System.out.println("Book with id " + id + " not found.");
				return false;
			}
		}
		
	}
	
	public boolean deleteBook(String id) {
		try (Jedis jedis = jedisPool.getResource()) {
			String key = instanceName + ":books:map:" + id;
			if (jedis.exists(key)) {
				jedis.del(key);
				System.out.println("Book " + id + " deleted.");
				return true;
			} else {
				System.out.println("Book " + id + " not found.");
				return false;
			}
		}

	}
	public void generateBooks() {
	    
	    Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "1925", "English","https://www.gutenberg.org/cache/epub/64317/pg64317.txt","https://www.gutenberg.org/cache/epub/64317/pg64317.cover.medium.jpg");
	    saveBookAsMap(book1);
	    System.out.println("Generated test books: ");
	    System.out.println(book1);
	   
	}
}
