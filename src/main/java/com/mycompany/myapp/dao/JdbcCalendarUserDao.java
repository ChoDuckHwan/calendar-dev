package com.mycompany.myapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.mycompany.myapp.domain.CalendarUser;

@Repository
public class JdbcCalendarUserDao implements CalendarUserDao {
	private DataSource dataSource;
	
    // --- constructors ---
    public JdbcCalendarUserDao() {

    }
    	
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
		
	}

    // --- CalendarUserDao methods ---
    @Override
    public CalendarUser getUser(int id) throws ClassNotFoundException, SQLException{
    	Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement( "select * from calendar_users where id = ?");
		ps.setLong(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		CalendarUser user = new CalendarUser();
		
		user.setId(rs.getInt("id"));
		user.setName(rs.getString("name"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		
		return user;
    }

    @Override
    public CalendarUser findUserByEmail(String email) {
    	return null;
    }

    @Override
    public List<CalendarUser> findUsersByEmail(String email) {
    	return null;
    }

    @Override
    public int createUser(final CalendarUser userToAdd){
		return 0;
    }
}