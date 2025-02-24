package net.etfbl.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.etfbl.model.User;
import net.etfbl.service.UserService;

@Path("/auth")
public class AuthAPI {
	private UserService userService = new UserService();

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User user) {
		User loggedInUser = userService.login(user.getUsername(), user.getPassword());
		if (loggedInUser != null) {
			return Response.status(200).entity(loggedInUser).build();
		} else {
			return Response.status(403).entity("Invalid username or password.").build();
		}
	}

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(User newUser) {
		if (userService.register(newUser)) {
			return Response.status(200).entity(newUser).build();
		} else {
			return Response.status(404).build();
		}

	}
}
