package com.dkchoi.chatserver;

import java.util.HashMap;

public class UserManager {
	private HashMap<String, User> userList;

	private static UserManager userManager = null;

	private UserManager() {
		this.userList = new HashMap<>();
	}

	public static UserManager getInstance() {
		if (userManager == null) {
			userManager = new UserManager();
		}
		return userManager;
	}

	public void addUser(User user) {
		synchronized (userList) {
			userList.put(user.getId(), user);
		}
	}

	public User getUser(String id) {
		return userList.get(id);
	}

	public void removeUser(String id) {
		synchronized (userList) {
			userList.remove(id);
		}
	}

	public void removeUser(User user) {
		synchronized (userList) {
			userList.remove(user.getId(), user);
		}
	}
}
