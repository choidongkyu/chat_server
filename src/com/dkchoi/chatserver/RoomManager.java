package com.dkchoi.chatserver;

import java.util.HashMap;

//싱글톤 패턴 사용
public class RoomManager {
	private HashMap<String, Room> roomList;
	private static RoomManager roomManager = null;
	
	private RoomManager() {	
		this.roomList = new HashMap<>();
	}
	
	public static RoomManager getInstance() {
		if (roomManager == null) {
			roomManager = new RoomManager();
		}
		return roomManager;
	}
	
	public void createRoom(String name) {
		synchronized (roomList) {
			roomList.put(name, new Room(name));
		}
	}
	
	public void removeRoom(String name) {
		synchronized (roomList) {
			roomList.remove(name);
		}
	}
	
	public Room getRoom(String name) {
		return roomList.get(name);
	}
}
