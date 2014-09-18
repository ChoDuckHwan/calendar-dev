package com.mycompany.myapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.stereotype.Repository;

import com.mycompany.myapp.domain.Event;

@Repository
public class JdbcEventDao implements EventDao {
	private DataSource dataSource;

	// --- constructors ---
	public JdbcEventDao() {
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	// --- EventService ---
	@Override
	public Event getEvent(int eventId) throws SQLException,
			ClassNotFoundException {
		Connection c = dataSource.getConnection();

		PreparedStatement ps = c
				.prepareStatement("select * from events where id = ?");
		ps.setInt(1, eventId);

		ResultSet rs = ps.executeQuery();
		rs.next();

		Event event = new Event();

		ApplicationContext context = new GenericXmlApplicationContext(
				"com/mycompany/myapp/applicationContext.xml");

		CalendarUserDao calendarUserDao = context.getBean("userDao",
				JdbcCalendarUserDao.class);

		event.setId(rs.getInt("id"));
		event.setSummary(rs.getString("summary"));
		event.setDescription(rs.getString("description"));
		event.setOwner(calendarUserDao.getUser(rs.getInt("owner")));

		Calendar temp = Calendar.getInstance();

		temp.setTime(rs.getDate("when"));

		event.setWhen(temp);
		event.setAttendee(calendarUserDao.getUser(rs.getInt("attendee")));

		rs.close();
		ps.close();
		c.close();

		return event;
	}

	@Override
	public int createEvent(final Event event) throws ClassNotFoundException,
			SQLException {
		Timestamp tempWhen = null;
		Calendar cal = null;
		String Summary = null;
		String Description = null;
		int Owner = 0;
		int Attendee = 0;

		cal = event.getWhen();
		Summary = event.getSummary();
		Description = event.getDescription();
		Owner = event.getOwner().getId();
		Attendee = event.getAttendee().getId();

		Connection c = dataSource.getConnection();

		PreparedStatement ps = c
				.prepareStatement("insert into events(`when`, `summary`, `description`, `owner`, `attendee`) values(?,?,?,?,?)");
		
		ps.setTimestamp(1, tempWhen);
		ps.setString(2, Summary);
		ps.setString(3, Description);
		ps.setInt(4, Owner);
		ps.setInt(5, Attendee);

		ps.executeUpdate();

		ps.close();
		c.close();

		return 0;
	}

	@Override
	public List<Event> findForUser(int userId) throws SQLException,
			ClassNotFoundException {
		List<Event> eventList = new ArrayList<Event>();

		Connection c = dataSource.getConnection();
		ApplicationContext context = new GenericXmlApplicationContext(
				"com/mycompany/myapp/applicationContext.xml");
		CalendarUserDao calendarUserDao = context.getBean("userDao",
				JdbcCalendarUserDao.class);

		PreparedStatement ps = c
				.prepareStatement("select * from events where `owner` = ?");
		ps.setInt(1, userId);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Event event = new Event();

			event.setId(rs.getInt("id"));
			event.setSummary(rs.getString("summary"));
			event.setDescription(rs.getString("description"));
			event.setOwner(calendarUserDao.getUser(rs.getInt("owner")));

			Calendar temp = Calendar.getInstance();
			temp.setTime(rs.getTimestamp("when"));

			event.setWhen(temp);
			event.setAttendee(calendarUserDao.getUser(rs.getInt("attendee")));

			// System.out.println(rs.getInt("id"));
			eventList.add(event);
		}
		rs.close();
		ps.close();
		c.close();

		return eventList;
	}

	@Override
	public List<Event> getEvents() throws SQLException, ClassNotFoundException {
		List<Event> allEvent = new ArrayList<Event>();
		Connection c = dataSource.getConnection();

		ApplicationContext context = new GenericXmlApplicationContext(
				"com/mycompany/myapp/applicationContext.xml");
		CalendarUserDao calendarUserDao = context.getBean("userDao",
				JdbcCalendarUserDao.class);
		PreparedStatement ps = c.prepareStatement("select * from events");

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Event event = new Event();

			event.setId(rs.getInt("id"));
			event.setSummary(rs.getString("summary"));
			event.setDescription(rs.getString("description"));
			event.setOwner(calendarUserDao.getUser(rs.getInt("owner")));

			Calendar temp = Calendar.getInstance();

			temp.setTime(rs.getTimestamp("when"));

			event.setWhen(temp);

			event.setAttendee(calendarUserDao.getUser(rs.getInt("attendee")));

			allEvent.add(event);
		}
		rs.close();
		ps.close();
		c.close();

		return allEvent;
	}

	/*
	 * private static final String EVENT_QUERY =
	 * "select e.id, e.summary, e.description, e.when, " +
	 * "owner.id as owner_id, owner.email as owner_email, owner.password as owner_password, owner.name as owner_name, "
	 * +
	 * "attendee.id as attendee_id, attendee.email as attendee_email, attendee.password as attendee_password, attendee.name as attendee_name "
	 * +
	 * "from events as e, calendar_users as owner, calendar_users as attendee "
	 * + "where e.owner = owner.id and e.attendee = attendee.id";
	 */
}
