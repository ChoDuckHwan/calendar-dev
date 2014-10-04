package com.mycompany.myapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;

import com.mycompany.myapp.domain.CalendarUser;
import com.mycompany.myapp.domain.Event;

@ContextConfiguration(locations = "../applicationContext.xml")
@Repository
public class JdbcEventDao implements EventDao {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private CalendarUserDao calendarUser;

	// --- constructors ---
	public JdbcEventDao() {
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}

	// --- EventService ---
	@Override
	public Event getEvent(int eventId) {
		return this.jdbcTemplate.queryForObject(
				"select * from events where id = ?", new Object[] { eventId },
				new RowMapper<Event>() {
					@Override
					public Event mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						Calendar when = Calendar.getInstance();
						when.setTimeInMillis(rs.getTimestamp("when").getTime());

						CalendarUser owner = calendarUser.getUser(rs
								.getInt("owner"));
						CalendarUser attendee = calendarUser.getUser(rs
								.getInt("attendee"));
						Event event = new Event(Integer.parseInt(rs
								.getString("id")), when, rs
								.getString("summary"), rs
								.getString("description"), owner, attendee);
						return event;
					}
				});
	}

	@Override
	public int createEvent(final Event event) {
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {

				Timestamp timestamp = new Timestamp(event.getWhen()
						.getTimeInMillis());
				PreparedStatement ps = connection
						.prepareStatement(
								"insert into events(`when`, summary, description, owner, attendee) values(?,?,?,?,?)",
								Statement.RETURN_GENERATED_KEYS);
				ps.setTimestamp(1, timestamp);
				ps.setString(2, event.getSummary());
				ps.setString(3, event.getDescription());
				ps.setInt(4, event.getOwner().getId());
				ps.setInt(5, event.getAttendee().getId());
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}

	@Override
	public List<Event> findForOwner(int ownerUserId) {
		return this.jdbcTemplate.query("select * from events where owner = ?",
				new Object[] {ownerUserId},			
    			new RowMapper<Event>()
				{
					@Override
					public Event mapRow(ResultSet rs, int rowNum)
							throws SQLException 
					{
						Calendar when = Calendar.getInstance();
						when.setTimeInMillis(rs.getTimestamp("when").getTime());
						
						CalendarUser owner = calendarUser.getUser(rs.getInt("owner"));
						CalendarUser attendee = calendarUser.getUser(rs.getInt("attendee"));
						Event event = new Event(Integer.parseInt(rs.getString("id")),
								when, rs.getString("summary"), rs.getString("description"),
								owner, attendee);
						//Event에 생성자 함수를 추가하여 코드를 줄임
					return event;
					}
				});
	}

	@Override
	public List<Event> getEvents() {
		return this.jdbcTemplate.query("select * from events",
    			new RowMapper<Event>()
    			{
					@Override
					public Event mapRow(ResultSet rs, int rowNum)
							throws SQLException 
					{
							Calendar when = Calendar.getInstance();
							when.setTimeInMillis(rs.getTimestamp("when").getTime());
					
							CalendarUser owner = calendarUser.getUser(rs.getInt("owner"));
							CalendarUser attendee = calendarUser.getUser(rs.getInt("attendee"));
							Event event = new Event(Integer.parseInt(rs.getString("id")),
									when, rs.getString("summary"), rs.getString("description"),
									owner, attendee);
						return event;
					}			
    			});
	}

	@Override
	public void deleteAll() {
		// Assignment 2
		this.jdbcTemplate.update("delete from events");
	}

}
