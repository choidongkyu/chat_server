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
				System.out.println("add user - " + user.getId());
				this.userList.add(user); // room에 user 추가
			}
		}
	}

	public Room(String name, String inviteUserId, String messageData, String userJson) {
		this.name = name;
		this.userList = new ArrayList<>();
		String[] users = name.split(","); // room name 예시 - "+821026595819,+821093230128" 이므로 , 를 기준으로 split 하여 나눠줌
		User inviteUser = UserManager.getInstance().getUser(inviteUserId);
		for (int i = 0; i < users.length; ++i) {
			User user = UserManager.getInstance().getUser(users[i]); // 접속해 있는 유저인지 확인
			if (user != null) { // 접속해있는 유저라면
				if (user.getId().equals(inviteUserId)) { // invite user는 추후 joinRoom으로 add되기 때문에 제외
					continue;
				}
				System.out.println("add user - " + user.getId());
				this.userList.add(user); // room에 user 추가
			}
		}

		if (inviteUser != null) {
			joinRoom(inviteUser, messageData, userJson);
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

	public void joinRoom(User user, String messageData, String userJson) {
		synchronized (userList) {
			broadcast(JOIN_KEY+messageData+"::"+userJson);
			this.userList.add(user);
		}
	}

	public void exitRoom(User user) {
		synchronized (userList) {
			this.userList.remove(user);
			String data = user.getName() + "님이 퇴장하였습니다.";
			broadcast(data);
		}
	}

	// 룸에 있는 모든 유저에게 메시지 보냄
	public void broadcast(String data) {
		System.out.println("broadcast data = " + data);
		synchronized (userList) {
			for (User user : userList) {
				System.out.println(user.getName() + "에게 메시지 보냄");
				user.getPrintWriter().println(data);
				user.getPrintWriter().flush();
			}
		}
	}

}
