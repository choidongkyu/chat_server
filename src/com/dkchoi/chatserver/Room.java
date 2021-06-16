package com.dkchoi.chatserver;

import java.util.ArrayList;

public class Room {
	private ArrayList<User> userList;
	private String name;
	
	public Room(String name) {
		this.name = name;
		this.userList = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void joinRoom(User user) {
		this.userList.add(user);
	}
	
	public void exitRoom(User user) {
		this.userList.remove(user);
	}
	
	
	//룸에 있는 모든 유저에게 메시지 보냄
	public void broadcast(String data) {
		synchronized (userList) {
			for (User user : userList) {
				user.getPrintWriter().println(data);
				user.getPrintWriter().flush();
			}
		}
	}
	
	
	
	
	

}
