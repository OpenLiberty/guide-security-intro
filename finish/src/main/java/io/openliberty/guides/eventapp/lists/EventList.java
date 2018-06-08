package io.openliberty.guides.eventapp.lists;

import java.util.ArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import io.openliberty.guides.eventapp.models.User;
import io.openliberty.guides.eventapp.models.Event;

@ManagedBean
@ApplicationScoped
public class EventList {
  private static ArrayList<Event> events = new ArrayList<>();


  public void addEvent(String name, String time, String location, int id) {

    events.add(new Event(name, location, time, id));
    System.out.println("add event" + id);
    
  }

  public Event getEventByName(String eventName) {
    for (Event e : events) {
      if (e.getName().toLowerCase().equals(eventName.toLowerCase())) {
        return e;
      }
    }
    return null;
  }


  public Event getEventById(int eventId) {
    for (Event e : events) {
      if (e.getId() == eventId ) {
        return e;
      }
    }
    return null;
  }

  public ArrayList<Event> getEventList() {
    return events;
  }

  public JsonArray getEvents() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    JsonArrayBuilder jArray = Json.createArrayBuilder();
    JsonArrayBuilder finalArray = Json.createArrayBuilder();
    for (Event event : events) {
      builder.add("name", event.getName())
      .add("time", event.getTime())
      .add("location", event.getLocation())
      .add("id", event.getId());
      for (User user : event.getUsers()) {
        jArray.add(Json.createObjectBuilder().add("name", user.getName())
                       .add("email", user.getEmail()).build());
      }
      finalArray.add(builder.add("users", jArray.build()).build());
    }
    return finalArray.build();
  }

  public void addUserToEvent(User user, int eventId) {
    Event current = this.getEventById(eventId);
    if (current != null){
      current.addUser(user);
    }
  }


}
