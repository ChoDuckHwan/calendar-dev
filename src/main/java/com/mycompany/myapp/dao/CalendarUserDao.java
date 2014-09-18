package com.mycompany.myapp.dao;

import java.sql.SQLException;
import java.util.List;

import com.mycompany.myapp.domain.CalendarUser;

public interface CalendarUserDao {
    CalendarUser getUser(int id) throws ClassNotFoundException, SQLException;

    CalendarUser findUserByEmail(String email);

    List<CalendarUser> findUsersByEmail(String partialEmail);

    int createUser(CalendarUser user);
}
