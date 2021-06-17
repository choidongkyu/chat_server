package com.dkchoi.chatserver;

import java.util.ArrayList;

public class Room {
	private ArrayList<User> userList;
	private String name;
	private String JOIN_KEY = "cfc3cf70-c9fc-11eb-9345-0800200c9a66";
	
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
		String data = JOIN_KEY + user.getName() + "님이 입장하였습니다.";
		broadcast(data);
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
