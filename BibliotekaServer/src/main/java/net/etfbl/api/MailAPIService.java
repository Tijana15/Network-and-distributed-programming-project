package net.etfbl.api;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.etfbl.model.Book;
import net.etfbl.service.MailService;

@Path("/mail")
public class MailAPIService {
	MailService mailService;

	public MailAPIService() {
		this.mailService = new MailService();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendMail(@QueryParam("mailTo") String mailTo, ArrayList<Book> books) {
		if (mailTo == null || mailTo.isEmpty()) {
			return Response.status(400).build();
		}
		if (books == null || books.isEmpty()) {
			return Response.status(400).build();
		}
		boolean success = mailService.sendMail(mailTo, books);
		if (success) {
			return Response.status(200).build();
		} else {
			return Response.status(500).build();
		}
	}
}
