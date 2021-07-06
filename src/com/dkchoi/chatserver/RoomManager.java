package com.dkchoi.chatserver;

import java.util.ArrayList;
import java.util.Arrays;
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
		if (!roomList.containsKey(name)) { // 기존에 방이 존재하지 않을때만 room 생성
			System.out.println("기존 방이 존재하지 않아 룸생성");
			synchronized (roomList) {
				roomList.put(name, new Room(name));
			}
		}else {
			System.out.println("기존 방이 존재하므로 룸생성하지 않음");
		}
	}

	public void removeRoom(String name) {
		synchronized (roomList) {
			roomList.remove(name);
		}
	}

	public Room getRoom(String name) {
		synchronized (roomList) {
			return roomList.get(name);
		}
	}
	
	public ArrayList<Room> getRoomByUser(User user) {// user id가 포함된 room이 있는지 조회, 없다면 빈 array 반환
		ArrayList<Room> result = new ArrayList<>();
		for(String roomName : roomList.keySet()) {
			String[] userId = roomName.split(",");
			if(Arrays.stream(userId).anyMatch(user.getId()::equals)) { //배열에 찾고자하는 id가 있다면 즉 해당 아이디 포함된 room이 있다면
				result.add(RoomManager.getInstance().getRoom(roomName));  //room 추가
			}
		}
		return result;
	}
}
