package com.mycompany.myapp.dao;

import java.awt.AWTException;
import java.awt.Robot;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.mycompany.myapp.domain.CalendarUser;
import com.mycompany.myapp.domain.Event;

public class DaoTest {

	private static void ShowCalendarUser(CalendarUserDao calendarUserDao,
			int userCount) throws ClassNotFoundException, SQLException {
		for (int i = 1; i < userCount + 1; i++) {
			CalendarUser test = calendarUserDao.getUser(i);
			System.out.println("id    : " + test.getId());
			System.out.println("Name  : " + test.getName());
			System.out.println("Email : " + test.getEmail());
			System.out.println("+++++++++++++++++++++++++");
			if (i == userCount) {
				System.out.println("======CalendarUser정보 출력 끝======");
			}
		}
	}

	private static int FindEventId(Event EventInput, EventDao eventDao) throws SQLException, ClassNotFoundException
	{
		int EventId = 0;
		List<Event> temp = eventDao.findForUser(EventInput.getOwner().getId());
		
		ArrayList<Integer> t = new ArrayList<Integer>(); 
		
		for(int i = 0; i < temp.size(); i++)
		{
			t.add(temp.get(i).getId());
		}
		
		Collections.sort(t);
		
		EventId = t.get(t.size() - 1);
		return EventId;
	}
	private static void ShowEvent(EventDao eventDao, int EventCount)
			throws SQLException, ClassNotFoundException {

		for (int i = 100; i < EventCount + 100; i++) {
			Event EventTest = eventDao.getEvent(i);
			System.out.println("Owner : " + EventTest.getOwner().getName());
			System.out.println("Email : " + EventTest.getAttendee().getEmail());
			System.out.println("----------------------");
			if (i == EventCount + 99) {
				System.out.println("======Event정보 출력 끝======");
			}
		}
	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, AWTException {
		ApplicationContext context = new GenericXmlApplicationContext(
				"com/mycompany/myapp/applicationContext.xml");

		CalendarUserDao calendarUserDao = context.getBean("userDao",
				JdbcCalendarUserDao.class);
		EventDao eventDao = context.getBean("eventDao", JdbcEventDao.class);

		// 1. 디폴트로 등록된 CalendarUser 3명 출력 (패스워드 제외한 모든 내용 출력)

		System.out.println("+++++++++++++++++++++++++");
		System.out.println("Default 등록 CalendarUser ");
		ShowCalendarUser(calendarUserDao, 3);
		System.out.println("+++++++++++++++++++++++++");

		// 2. 디폴트로 등록된 Event 3개 출력 (owner와 attendee는 해당 사용자의 이메일과 이름을 출력)

		System.out.println("+++++++++++++++++++++++++");
		System.out.println("Default 등록 Event");
		ShowEvent(eventDao, 3);
		System.out.println("+++++++++++++++++++++++++");

		// 3. 새로운 CalendarUser 2명 등록 및 각각 id 추출
		CalendarUser NewUser1 = new CalendarUser();
		NewUser1.setEmail("NewUser1@email.com");
		NewUser1.setName("NewUserTest1");
		NewUser1.setPassword("NewUserPassWord1!");
		calendarUserDao.createUser(NewUser1);
		int extractId1 = calendarUserDao.findUserByEmail(NewUser1.getEmail())
				.getId();

		CalendarUser NewUser2 = new CalendarUser();
		NewUser2.setEmail("NewUser2@email.com");
		NewUser2.setName("NewUserTest2");
		NewUser2.setPassword("NewUserPassWord2!");
		calendarUserDao.createUser(NewUser2);
		int extractId2 = calendarUserDao.findUserByEmail(NewUser2.getEmail())
				.getId();

		// 4. 추출된 id와 함께 새로운 CalendarUser 2명을 DB에서 가져와 (getUser 메소드 사용) 방금 등록된
		// 2명의 사용자와 내용 (이메일, 이름, 패스워드)이 일치하는 지 비교
		if (NewUser1.getEmail().equals(
				calendarUserDao.getUser(extractId1).getEmail())
				&& NewUser1.getName().equals(
						calendarUserDao.getUser(extractId1).getName())
				&& NewUser1.getPassword().equals(
						calendarUserDao.getUser(extractId1).getPassword())) {
			System.out.println("추출된 id" + extractId1 + "의 내용이 일치 합니다.");
		}

		if (NewUser2.getEmail().equals(
				calendarUserDao.getUser(extractId2).getEmail())
				&& NewUser2.getName().equals(
						calendarUserDao.getUser(extractId2).getName())
				&& NewUser2.getPassword().equals(
						calendarUserDao.getUser(extractId2).getPassword())) {
			System.out.println("추출된 id" + extractId2 + "의 내용이 일치 합니다.");
		}
		// 5. 5명의 CalendarUser 모두 출력 (패스워드 제외한 모든 내용 출력)
		System.out.println("\n+++++++++++++++++++++++++++++++++++++");
		System.out.println("-------5명의  CalendarUser 출력-------");
		ShowCalendarUser(calendarUserDao, 5);

		// 6. 새로운 Event 2개 등록 및 각각 id 추출
		Calendar NewTime1 = Calendar.getInstance();
		Event NewEvent1 = new Event();
		NewEvent1.setWhen(NewTime1);
		NewEvent1.setSummary("EventSummary1");
		NewEvent1.setDescription("EventDescription1");
		NewEvent1.setOwner(calendarUserDao.getUser(1));
		NewEvent1.setAttendee(calendarUserDao.getUser(2));
		eventDao.createEvent(NewEvent1);


		Robot tRobot = new Robot();
		tRobot.delay(1000);
		Calendar NewTime2 = Calendar.getInstance();
		Event NewEvent2 = new Event();
		NewEvent1.setWhen(NewTime2);
		NewEvent2.setSummary("EventSummary2");
		NewEvent2.setDescription("EventDescription2");
		NewEvent2.setOwner(calendarUserDao.getUser(2));
		NewEvent2.setAttendee(calendarUserDao.getUser(1));
		eventDao.createEvent(NewEvent2);
		
		int extraEventId1 = FindEventId(NewEvent1, eventDao);
		int extraEventId2 = FindEventId(NewEvent2, eventDao);
		
		// 7. 추출된 id와 함께 새로운 Event 2개를 DB에서 가져와 (getEvent 메소드 사용) 방금 추가한 2개의
		// 이벤트와 내용 (when, summary, description, owner, attendee)이 일치하는 지 비교
		
		if(NewEvent1.getSummary().equals(eventDao.getEvent(extraEventId1).getSummary())
				&& NewEvent1.getDescription().equals(eventDao.getEvent(extraEventId1).getDescription())
				&& NewEvent1.getOwner().equals(eventDao.getEvent(extraEventId1).getOwner())
				&& NewEvent1.getAttendee().equals(eventDao.getEvent(extraEventId1).getAttendee())){
			System.out.println(extraEventId1+"의 Summary, Owner, description, Attendee 동일합니다");
					}
		
		if(NewEvent2.getSummary().equals(eventDao.getEvent(extraEventId2).getSummary())
				&& NewEvent2.getDescription().equals(eventDao.getEvent(extraEventId2).getDescription())
				&& NewEvent2.getOwner().equals(eventDao.getEvent(extraEventId2).getOwner())
				&& NewEvent2.getAttendee().equals(eventDao.getEvent(extraEventId2).getAttendee())){
			System.out.println(extraEventId2+"의 Summary, Owner, description, Attendee 동일합니다");
					}
		// 8. 5개의 Event 모두 출력 (owner와 attendee는 해당 사용자의 이메일과 이름을 출력)
		ShowEvent(eventDao,5);
	}
}
