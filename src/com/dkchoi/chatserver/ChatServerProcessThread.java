package com.dkchoi.chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatServerProcessThread extends Thread {
	private Socket socket = null;
	private String JOIN_KEY = "cfc3cf70-c9fc-11eb-9345-0800200c9a66";
	private User user = null;
	private boolean running = true;

	public ChatServerProcessThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader buffereedReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

			PrintWriter printWriter = new PrintWriter(
					new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

			while (running) {
				String request = buffereedReader.readLine();
				consoleLog(request);

				if (request == null) {
					disconnectClient(this.user);
					break;
				}

				String[] tokens = request.split("::");
				
				switch (tokens[0]) {
				case "join": // 앱 진입시 소켓 연결되는 시점
					User user = new User(tokens[1], tokens[2], printWriter); // 들어오는 메시지 예시 - "join::최동규::+821026595819"
					doJoin(user);
					break;
					
				case "createRoom": // 들어오는 메시지 예시 - "createRoom::+821026595819,+821093230128"
					RoomManager.getInstance().createRoom(tokens[1]); // 룸생성
					break;
					
				case "quit":
					doQuit(this.user);
					break;
					
				case "message": // 들어오는 메시지 예시 - "createRoom::+821026595819,+821093230128"
					RoomManager.getInstance().getRoom(tokens[1]).broadcast(tokens[2]);
					break;
					
				case "invite": // 들어오는 메시지 예시 -"invite::+821026595819,+821093230128,+1555...::+15555215556::messageData"
					RoomManager.getInstance().getRoom(tokens[1]).broadcast(tokens[2]);
					
					/**
					 * tokens[1] - room name 
					 * tokens[2] - 초대될 user id 
					 * tokens[3] - messageData
					 * tokens[4] - user json data
					 */
					
					RoomManager.getInstance().inviteRoom(tokens[1], tokens[2], tokens[3], tokens[4]); // 방 새로 만든 후 초대
					break;
					
				case "videoCall":
					
					/**
					 * tokens[1] - channel id 
					 * tokens[2] - user json data 
					 * tokens[3] - user id
					 */
					
					User callUser = UserManager.getInstance().getUser(tokens[3]);
					if (callUser != null) {
						callUser.getPrintWriter().println("videoCall::" + tokens[1] + "::" + tokens[2]);
						callUser.getPrintWriter().flush();
					}
					break;
					
				case "receiveVideoCall":
					User receiveUser = UserManager.getInstance().getUser(tokens[1]);
					if (receiveUser != null) {
						receiveUser.getPrintWriter().println(tokens[0]);
						receiveUser.getPrintWriter().flush();
					}
					break;
					
				case "receiveVoiceCall":
					User recevieVoiceUser = UserManager.getInstance().getUser(tokens[1]);
					if (recevieVoiceUser != null) {
						recevieVoiceUser.getPrintWriter().println(tokens[0]);
						recevieVoiceUser.getPrintWriter().flush();
					}
					break;
					
				case "voiceCall":
					/**
					 * tokens[1] - channel id 
					 * tokens[2] - user json data 
					 * tokens[3] - user id
					 */
					
					User voiceCallUser = UserManager.getInstance().getUser(tokens[3]);
					if (voiceCallUser != null) {
						voiceCallUser.getPrintWriter().println("voiceCall::" + tokens[1] + "::" + tokens[2]);
						voiceCallUser.getPrintWriter().flush();
					}
					break;
					
				case "rejectCall":
					User rejectUser = UserManager.getInstance().getUser(tokens[1]);
					if (rejectUser != null) {
						rejectUser.getPrintWriter().println(tokens[0]);
						rejectUser.getPrintWriter().flush();
					}
					break;

				default:
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			disconnectClient(this.user);
		}
	}

	private void doQuit(User user) {
		if (user == null) {
			System.out.println("user == null 이므로 doQuit return");
			return;
		}
		consoleLog(user.getName() + " doQuit Called");
		UserManager.getInstance().removeUser(user);
		// 존재했던 기존 방이 있는지 확인
		ArrayList<Room> rooms = RoomManager.getInstance().getRoomByUser(user); // 자신이 존재하는 방 list 구함
		if (rooms.size() != 0) { // 존재한 room이 존재한다면
			consoleLog("roomSize() != 0 존재하는 룸이 있으므로 해당 룸에 " + user.getName() + " 제거");
			for (Room room : rooms) {
				room.removeUser(user); // 방에 user 추가
			}
		}
		this.user = null;
		running = false; // 쓰레드 종료
	}


	private void disconnectClient(User user) {
		consoleLog("클라이언트로부터 연결 끊김");
		doQuit(user);
	}

	private void doJoin(User user) {
		this.user = user; // 멤버변수에 유저 객체 저장
		UserManager.getInstance().addUser(user); // 접속중인 유저이므로 add user
		// 존재했던 기존 방이 있는지 확인
		ArrayList<Room> rooms = RoomManager.getInstance().getRoomByUser(user); // 자신이 존재하는 방 list 구함
		if (rooms.size() != 0) { // 존재한 room이 존재한다면
			consoleLog("roomSize() != 0 존재하는 룸이 있으므로 해당 룸에 " + user.getName() + " 추가");
			for (Room room : rooms) {
				room.addUser(user); // 방에 user 추가
			}
		}
	}

	private void consoleLog(String log) {
		System.out.println(log);
	}
}
