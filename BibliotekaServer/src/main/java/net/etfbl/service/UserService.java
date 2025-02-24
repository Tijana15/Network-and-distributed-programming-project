package net.etfbl.service;

import net.etfbl.config.ConfigLoader;
import java.util.List;
import net.etfbl.model.User;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;

public class UserService {
	private static final String users_file = ConfigLoader.getProperty("USERS_FILE");
	private File usersFile = new File(users_file);
	private static List<User> users;

	public UserService() {
		this.setUsers(loadUsers());
		if (this.getUsers() == null) {
			this.setUsers(new ArrayList<>());
		}
	}

	public boolean saveUser(User user) {
		if (getUser(user.getUsername()) == null) {
			users.add(user);
			saveUsersToFile();
			System.out.println("User saved.");
			return true;
		} else {
			System.out.println("User with this id already exists.");
			return false;
		}
	}

	public User getUser(String username) {
		loadUsers();
		for (User user : getUsers()) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
	}

	public List<User> loadUsers() {
		System.out.println(usersFile.getAbsolutePath());
		List<User> usersList = null;

		if (usersFile.exists() && usersFile.isFile()) {
			try (XMLDecoder decoder = new XMLDecoder(new FileInputStream(usersFile))) {
				usersList = (List<User>) decoder.readObject();

			} catch (Exception e) {
				System.out.println("Error occurred while loading users from file: " + e.getMessage());
			}
		} else {
			System.out.println("No users file found, starting with an empty list.");
		}
		return usersList;
	}

	private void saveUsersToFile() {
		try (XMLEncoder encoder = new XMLEncoder(new FileOutputStream(usersFile))) {
			encoder.writeObject(getUsers());
			System.out.println("saveusers");
		} catch (Exception e) {
			System.out.println("Error occurred while saving users: " + e.getMessage());
		}
	}

	public boolean deleteUser(String username) {
		loadUsers();
		users.removeIf(user -> user.getUsername().equals(username));
		saveUsersToFile();
		return true;
	}


	public boolean update(User newUser,String username) {
		loadUsers();
		User existingUser = getUser(username);

		if (existingUser != null) {
			if (newUser.getUsername() != null) {
				existingUser.setUsername(newUser.getUsername());
			}
			if (newUser.getEmail() != null) {
				existingUser.setEmail(newUser.getEmail());
			}
			if (newUser.getAddress() != null) {
				existingUser.setAddress(newUser.getAddress());
			}
			if (newUser.getPassword() != null) {
				existingUser.setPassword(newUser.getPassword());
			}
			if (newUser.getName() != null) {
				existingUser.setName(newUser.getName());
			}
			if (newUser.getLastname() != null) {
				existingUser.setLastname(newUser.getLastname());
			}
			saveUsersToFile();
			return true;
		} else {
			return false;
		}
	}

	public User login(String username, String password) {
		loadUsers();
		for (User user : users) {
			if (user.getUsername().equals(username) && user.getPassword().equals(password) && user.getActivated().equals(true)) {
				System.out.println("Login successful for user: " + username);
				return user;
			}else {
				System.out.println("Not activated account");
			}
		}
		System.out.println("Invalid username or password.");
		return null;
	}

	public boolean register(User newUser) {
		loadUsers();
		for (User user : users) {
			if (user.getUsername().equals(newUser.getUsername())) {
				return false;
			}
		}
		users.add(newUser);
		saveUsersToFile();
		return true;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		UserService.users = users;
	}

	public ArrayList<User> getRequests() {
		loadUsers();
		ArrayList<User> inactiveUsers = new ArrayList<User>();
		for (User user : users) {
			if(user.getActivated() == false) {
				inactiveUsers.add(user);
				System.out.println("INACTIVE USERS: ");
				for(User a:inactiveUsers) {
					System.out.println("user: "+a);
				}
			}
		}
		return inactiveUsers;
	}


	public boolean acceptRequest(User user) {
		loadUsers();
		System.out.println("test " + user);
		for (User u : users) {
			if(user.equals(u)) {
				u.setActivated(true);
				System.out.println("Activated "+u);
				saveUsersToFile();
				return true;
			}
		}
		
		return false;
	}
}
