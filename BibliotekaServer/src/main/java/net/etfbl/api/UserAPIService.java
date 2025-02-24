package net.etfbl.api;

import net.etfbl.service.*;
import net.etfbl.model.*;

import java.util.ArrayList;
import java.util.List;
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

@Path("/users")
public class UserAPIService {

	UserService userService;

	public UserAPIService() {
		userService = new UserService();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getAllUsers() {
		return userService.loadUsers();
	}

	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("username") String username) {
		User user = userService.getUser(username);
		if (user != null) {
			return Response.status(200).entity(user).build();
		} else {
			return Response.status(404).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUser(User user) {
		if (userService.saveUser(user)) {
			return Response.status(200).entity(user).build();
		} else {
			return Response.status(500).entity("Error ocupid while adding user.").build();
		}
	}

	@PUT
	@Path("/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(User user, @PathParam("username") String username) {
		if (userService.update(user, username)) {
			return Response.status(200).entity(user).build();
		} else {
			return Response.status(404).build();
		}
	}

	@DELETE
	@Path("/{username}")
	public Response removeUser(@PathParam("username")String username) {
		if (userService.deleteUser(username)) {
			return Response.status(200).build();
		} else {
			return Response.status(404).build();
		}
	}
	@GET
	@Path("requests")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRequests() {
		ArrayList<User> users = userService.getRequests();
		return Response.status(200).entity(users).build();
	}


	@PUT
	@Path("accept")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response acceptRequest(User user) {
		if(userService.acceptRequest(user)) {
			return Response.status(200).build();
		}
		return Response.status(404).build();
	}
}
