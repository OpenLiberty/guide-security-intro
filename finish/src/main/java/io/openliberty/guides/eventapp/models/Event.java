package io.openliberty.guides.eventapp.models;

import java.util.ArrayList;

public class Event {
  private int id;
	private String name;
	private String location;
	private String time;
	private ArrayList<User> users;

	public Event(String name, String location, String time, int id) {
		users = new ArrayList<User>();
		this.name = name;
		this.location = location;
		this.time = time;
		this.id = id;
	}


	public int getId() {
		return id;
	}

  public void setId(int id){
    this.id = id;
  }

	public void setName(String name) {
		this.name = name;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public String getTime() {
		return time;
	}

	public void addUser(User user) {
		users.add(user);
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}
}
