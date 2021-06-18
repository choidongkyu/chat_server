package com.dkchoi.chatserver;

import java.util.ArrayList;
import com.dkchoi.chatserver.UserManager;

public class Room {
	private ArrayList<User> userList;
	private String name;
	private String JOIN_KEY = "cfc3cf70-c9fc-11eb-9345-0800200c9a66";

	public Room(String name) {
		this.name = name;
		this.userList = new ArrayList<>();

		String[] users = name.split(","); // room name 예시 - "+821026595819,+821093230128" 이므로 , 를 기준으로 split 하여 나눠줌

		for (int i = 0; i < users.length; ++i) {
			User user = UserManager.getInstance().getUser(users[i]); // 접속해 있는 유저인지 확인
			if (user != null) { // 접속해있는 유저라면
				this.userList.add(user); // room에 user 추가
			}
		}
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void addUser(User user) {
		synchronized (userList) {
			this.userList.add(user);
		}
	}
	
	public void removeUser(User user) {
		synchronized (userList) {
			this.userList.remove(user);
		}
	}

	public void joinRoom(User user) {
		synchronized (userList) {
			String data = JOIN_KEY + user.getName() + "님이 입장하였습니다.";
			broadcast(data);
			this.userList.add(user);
		}
	}

	public void exitRoom(User user) {
		this.userList.remove(user);
		String data = JOIN_KEY + user.getName() + "님이 퇴장하였습니다.";
		broadcast(data);
	}

	// 룸에 있는 모든 유저에게 메시지 보냄
	public void broadcast(String data) {
		synchronized (userList) {
			for (User user : userList) {
				user.getPrintWriter().println(data);
				user.getPrintWriter().flush();
			}
		}
	}

}
