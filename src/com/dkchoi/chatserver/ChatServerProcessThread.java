package com.dkchoi.chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ChatServerProcessThread extends Thread {
	private String nickname = null;
	private Socket socket = null;
	private String JOIN_KEY = "cfc3cf70-c9fc-11eb-9345-0800200c9a66";
	List<PrintWriter> listWriters = null;
	
	private boolean running = true;

	public ChatServerProcessThread(Socket socket, List<PrintWriter> listWriters) {
		this.socket = socket;
		this.listWriters = listWriters;
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

				if (request == null) {
					disconnectClient(printWriter);
					break;
				}

				String[] tokens = request.split("::");
				if ("join".equals(tokens[0])) {
					doJoin(tokens[1], printWriter);
				} else if ("message".equals(tokens[0])) {
					doMessage(tokens[1]);
				} else if ("quit".equals(tokens[0])) {
					doQuit(printWriter);
				}
			}
		} catch (IOException e) {
			consoleLog(this.nickname + "님이 채팅방을 나갔습니다.");
		}
	}

	private void doQuit(PrintWriter writer) {
		removeWriter(writer);
		String data = JOIN_KEY + this.nickname + "님이 퇴장했습니다.";
		consoleLog(data);
		broadcast(data);
		running = false; // 쓰레드 종료
	}
	
	private void disconnectClient(PrintWriter writer) {
		consoleLog("클라이언트로부터 연결 끊김");
		removeWriter(writer);
	}

	private void removeWriter(PrintWriter writer) {
		synchronized (listWriters) {
			listWriters.remove(writer);
		}
	}

	private void doMessage(String data) {
		consoleLog(data);
		broadcast(data);
	}

	private void doJoin(String nickname, PrintWriter writer) {
		this.nickname = nickname;

		String data = JOIN_KEY + nickname + "님이 입장하였습니다.";
		broadcast(data);
		consoleLog(data);
		// writer pool에 저장
		addWriter(writer);
	}

	private void addWriter(PrintWriter writer) {
		synchronized (listWriters) {
			listWriters.add(writer);
		}
	}

	private void broadcast(String data) {
		synchronized (listWriters) {
			for (PrintWriter writer : listWriters) {
				writer.println(data);
				writer.flush();
			}
		}
	}

	private void consoleLog(String log) {
		System.out.println(log);
	}
}
