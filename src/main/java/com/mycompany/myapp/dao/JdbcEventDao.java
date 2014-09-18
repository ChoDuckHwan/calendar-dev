package com.mycompany.myapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

    // --- EventService ---
    @Override
    public Event getEvent(int eventId) throws SQLException, ClassNotFoundException {
    	Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement("select * from events where id = ?");
		ps.setInt(1, eventId);
        
		ResultSet rs = ps.executeQuery();
		rs.next();
		
		Event event = new Event();
		
		
		ApplicationContext context = 
				new GenericXmlApplicationContext("com/mycompany/myapp/applicationContext.xml");
		
		CalendarUserDao calendarUserDao = 
				context.getBean("userDao", JdbcCalendarUserDao.class);
		
		
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
    public int createEvent(final Event event) {
        return 0;
    }

    @Override
    public List<Event> findForUser(int userId) {
        return null;
    }

    @Override
    public List<Event> getEvents() {
        return null;
    }

    /*
    private static final String EVENT_QUERY = "select e.id, e.summary, e.description, e.when, " +
            "owner.id as owner_id, owner.email as owner_email, owner.password as owner_password, owner.name as owner_name, " +
            "attendee.id as attendee_id, attendee.email as attendee_email, attendee.password as attendee_password, attendee.name as attendee_name " +
            "from events as e, calendar_users as owner, calendar_users as attendee " +
            "where e.owner = owner.id and e.attendee = attendee.id";
     */
}
