package net.etfbl.api;

import net.etfbl.model.*;
import net.etfbl.service.BookService;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import redis.clients.jedis.JedisPool;

@Path("/books")
public class BookAPIService {
	final JedisPool pool = new JedisPool("localhost");
	final String instanceName = "Books";
	BookService bookService;
	

	public BookAPIService() {
		bookService = new BookService(pool, instanceName);
		//bookService.generateBooks();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, String>> getAllBooks() {
		return bookService.getAllBooksAsMaps();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBook(@PathParam("id") String id) {
		Book book =bookService.loadBookById(id);
		if (book != null) {
			return Response.status(200).entity(book).build();
		} else {
			return Response.status(404).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addBook(Book book) {
		if (bookService.saveBookAsMap(book)) {
			return Response.status(200).entity(book).build();
		} else {
			return Response.status(500).entity("Error ocupid while adding book.").build();
		}
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateBook(Book book, @PathParam("id") String id) {
		if (bookService.updateBook(book, id)) {
			return Response.status(200).entity(book).build();
		} else {
			return Response.status(404).build();
		}
	}

	@DELETE
	@Path("/{id}")
	public Response deleteBook(@PathParam("id") String id) {
		if (bookService.deleteBook(id)) {
			return Response.status(200).build();
		} else {
			return Response.status(404).build();
		}
	}

}
