package com.mycompany.myapp.dao;

import java.sql.SQLException;
import java.util.List;

import com.mycompany.myapp.domain.Event;

public interface EventDao {

	Event getEvent(int eventId) throws SQLException, ClassNotFoundException;

	int createEvent(Event event) throws SQLException, ClassNotFoundException;

	List<Event> findForUser(int userId) throws SQLException,
			ClassNotFoundException;

	List<Event> getEvents() throws SQLException, ClassNotFoundException;
}