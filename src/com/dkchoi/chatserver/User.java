package com.dkchoi.chatserver;

import java.io.PrintWriter;

public class User {
	private String id;
	private String name;
	private PrintWriter printWriter;

	public User(String name, String id, PrintWriter printWriter) {
		this.name = name;
		this.id = id;
		this.printWriter = printWriter;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PrintWriter getPrintWriter() {
		return printWriter;
	}

	public void setPrintWriter(PrintWriter printWriter) {
		this.printWriter = printWriter;
	}
}
