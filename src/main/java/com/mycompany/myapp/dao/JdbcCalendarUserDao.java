package com.mycompany.myapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;






import org.springframework.test.context.ContextConfiguration;

import com.mycompany.myapp.domain.CalendarUser;

@ContextConfiguration(locations="../applicationContext.xml")
@Repository
public class JdbcCalendarUserDao implements CalendarUserDao {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	// --- constructors ---
	public JdbcCalendarUserDao() {

	}

	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}

	// --- CalendarUserDao methods ---
	@Override
	public CalendarUser getUser(int id){
		return this.jdbcTemplate.queryForObject("select * from calendar_users where id = ?",
    			new Object[] {id},
    			new RowMapper<CalendarUser>()
    			{
					@Override
					public CalendarUser mapRow(ResultSet rs, int rowNum)
							throws SQLException 
					{
						CalendarUser user = new CalendarUser(rs.getInt("id"),rs.getString("email"),
								rs.getString("password"), rs.getString("name"));
						return user;
					}
    			});
    }

	@Override
	public CalendarUser findUserByEmail(String email) {
		return this.jdbcTemplate.queryForObject("select * from calendar_users where email = ?",
				new Object[] {email},
				new RowMapper<CalendarUser>()
				{
					@Override
					public CalendarUser mapRow(ResultSet rs, int rowNum) 
							throws SQLException
					{
						CalendarUser user = new CalendarUser();
						user.setId(rs.getInt("id"));
						user.setEmail(rs.getString("email"));
						user.setPassword(rs.getString("password"));
						user.setName(rs.getString("name"));
						return user;
					}
				});
	}

	@Override
	public List<CalendarUser> findUsersByEmail(String email) {
		final List<CalendarUser> list = new ArrayList<CalendarUser>();
		
		return this.jdbcTemplate.queryForObject("select * from calendar_users where email like ?",
				new Object[] {"%" + email + "%"},
				
				new RowMapper<List<CalendarUser>>()
				{
				@Override
				public List<CalendarUser> mapRow(ResultSet rs, int rowNum) 
					throws SQLException {
				CalendarUser user = new CalendarUser();
				user.setId(rs.getInt("id"));
				user.setEmail(rs.getString("email"));
				user.setPassword(rs.getString("password"));
				user.setName(rs.getString("name"));
				list.add(user);
		
				while(rs.next())
				{
					CalendarUser Newuser = new CalendarUser();
					Newuser.setId(rs.getInt("id"));
					Newuser.setEmail(rs.getString("email"));
					Newuser.setPassword(rs.getString("password"));
					Newuser.setName(rs.getString("name"));
					list.add(user);
				}
				return list;
		}
	});
	}

	@Override
	public int createUser(final CalendarUser userToAdd)
    {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement("insert into calendar_users (email, password, name) values(?,?,?)", Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, userToAdd.getEmail());
				ps.setString(2, userToAdd.getPassword());
				ps.setString(3, userToAdd.getName());
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().intValue();

    }
	
	
	@Override
	public void deleteAll() {
		// Assignment 2
		this.jdbcTemplate.update("delete from calendar_users");
	}

}