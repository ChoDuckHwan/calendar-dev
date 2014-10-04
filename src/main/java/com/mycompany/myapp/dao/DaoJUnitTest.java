package com.mycompany.myapp.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.mycompany.myapp.domain.CalendarUser;
import com.mycompany.myapp.domain.Event;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="../applicationContext.xml")

public class DaoJUnitTest {
	@Autowired
	private CalendarUserDao calendarUserDao;	
	
	@Autowired
	private EventDao eventDao;
	
	private CalendarUser[] calendarUsers = null;
	private Event[] events = null;
	
	int [] userID = new int [3];
	int [] eventID = new int [3];
	@Before
	public void setUp() {
		calendarUsers = new CalendarUser[3];
		events = new Event[3];
		
		this.calendarUserDao.deleteAll();
		this.eventDao.deleteAll();
		
		
		/* [참고]
		insert into calendar_users(`id`,`email`,`password`,`name`) values (1,'user1@example.com','user1','User1');
		insert into calendar_users(`id`,`email`,`password`,`name`) values (2,'admin1@example.com','admin1','Admin');
		insert into calendar_users(`id`,`email`,`password`,`name`) values (3,'user2@example.com','user2','User1');

		insert into events (`id`,`when`,`summary`,`description`,`owner`,`attendee`) values (100,'2013-10-04 20:30:00','Birthday Party','This is going to be a great birthday',1,2);
		insert into events (`id`,`when`,`summary`,`description`,`owner`,`attendee`) values (101,'2013-12-23 13:00:00','Conference Call','Call with the client',3,1);
		insert into events (`id`,`when`,`summary`,`description`,`owner`,`attendee`) values (102,'2014-01-23 11:30:00','Lunch','Eating lunch together',2,3);
		*/
		this.calendarUsers[0] = new CalendarUser(1, "user1@example.com", "user1", "User1");
		this.calendarUsers[1] = new CalendarUser(2, "admin1@example.com", "admin1", "Admin");
		this.calendarUsers[2] = new CalendarUser(3, "user2@example.com", "user2", "User2");
		List<CalendarUser> users = new ArrayList<CalendarUser>();
		List<Event> event = new ArrayList<Event>();
		
		userID[0] =  calendarUserDao.createUser(calendarUsers[0]);
		userID[1] =  calendarUserDao.createUser(calendarUsers[1]);
		userID[2] =  calendarUserDao.createUser(calendarUsers[2]);
		
		
		
		Calendar calendar1 = Calendar.getInstance();
    	CalendarUser owner1 = new CalendarUser();
    	CalendarUser attendee1 = new CalendarUser();
    	calendar1.set(2013,10, 04, 20, 30);
    	owner1 = calendarUserDao.getUser(userID[0]);
    	attendee1 = calendarUserDao.getUser(userID[1]);
		this.events[0] = new Event(100, calendar1, "Birthday Party", "This is going to be a great birthday",
				owner1, attendee1);
		eventDao.createEvent(events[0]);
		
		Calendar calendar2 = Calendar.getInstance();
    	CalendarUser owner2 = new CalendarUser();
    	CalendarUser attendee2 = new CalendarUser();
		calendar2.set(2013,12, 23, 13, 00);
		owner2 = calendarUserDao.getUser(userID[2]);
		attendee2 = calendarUserDao.getUser(userID[0]);
		this.events[1] = new Event(101, calendar2, "Chicken", "I want eat chicken",
				owner2, attendee2);
		eventDao.createEvent(events[1]);
		
		Calendar calendar3 = Calendar.getInstance();
    	CalendarUser owner3 = new CalendarUser();
    	CalendarUser attendee3 = new CalendarUser();
		calendar3.set(2014, 01, 23, 11, 30);
		owner3 = calendarUserDao.getUser(userID[1]);
		attendee3 = calendarUserDao.getUser(userID[2]);
		this.events[2] = new Event(102, calendar3, "Lunch", "Eating lunch together", owner3, attendee3);
		eventDao.createEvent(events[2]);
		// 1. SQL 코드에 존재하는 3개의 CalendarUser와 Event 각각을 Fixture로서 인스턴스 변수 calendarUsers와 events에 등록하고 DB에도 저장한다. 
		// [주의 1] 모든 id 값은 입력하지 않고 DB에서 자동으로 생성되게 만든다. 
		// [주의 2] Calendar를 생성할 때에는 DB에서 자동으로 생성된 id 값을 받아 내어서 Events를 만들 때 owner와 attendee 값으로 활용한다.
		//System.out.println(calendarUserDao.findUserByEmail("admin1@example.com").getId());
	}
	
	@Test
	public void createCalendarUserAndCompare() {
		// 2. 새로운 CalendarUser 2명 등록 및 각각 id 추출하고, 추출된 id와 함께 새로운 CalendarUser 2명을 DB에서 가져와 (getUser 메소드 사용) 
		//    방금 등록된 2명의 사용자와 내용 (이메일, 이름, 패스워드)이 일치하는 지 비교
		int Test1Id = 0;
		int Test2Id = 0;
		CalendarUser NewCalUser1 = new CalendarUser("test1@example.com", "test1", "test1");
		Test1Id = calendarUserDao.createUser(NewCalUser1);
		NewCalUser1.setId(Test1Id);
		
		CalendarUser NewCalUser2 = new CalendarUser("test2@example.com", "test2", "test2");
		Test2Id = calendarUserDao.createUser(NewCalUser2);
		NewCalUser2.setId(Test2Id);
		
		CalendarUser Test1Compare = new CalendarUser();
		CalendarUser Test2Compare = new CalendarUser();

		Test1Compare = calendarUserDao.getUser(Test1Id);
		Test2Compare = calendarUserDao.getUser(Test2Id);
		
		assertThat(Test1Compare.getEmail(), is(NewCalUser1.getEmail()));
		assertThat(Test1Compare.getPassword(), is(NewCalUser1.getPassword()));
		assertThat(Test1Compare.getName(), is(NewCalUser1.getName()));
		
		assertThat(Test2Compare.getEmail(), is(NewCalUser2.getEmail()));
		assertThat(Test2Compare.getPassword(), is(NewCalUser2.getPassword()));
		assertThat(Test2Compare.getName(), is(NewCalUser2.getName()));
	}
	
	@Test
	public void createEventUserAndCompare() {
		// 3. 새로운 Event 2개 등록 및 각각 id 추출하고, 추출된 id와 함께 새로운 Event 2개를 DB에서 가져와 (getEvent 메소드 사용) 
		//    방금 추가한 2개의 이벤트와 내용 (summary, description, owner, attendee)이 일치하는 지 비교
		// [주의 1] when은 비교하지 않아도 좋다.
		// [주의 2] owner와 attendee는 @Before에서 미리 등록해 놓은 3명의 CalendarUser 중에서 임의의 것을 골라 활용한다.

		Event createEvent1 = new Event();
		Event createEvent2 = new Event();
		int createEvent1Id = 0;
		int createEvent2Id = 0;
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014,10, 05, 12, 00);
		createEvent1.setWhen(Calendar.getInstance());
		createEvent1.setSummary("event1 - summary");
		createEvent1.setDescription("event1 - description");
		createEvent1.setOwner(calendarUserDao.getUser(userID[0]));
		createEvent1.setAttendee(calendarUserDao.getUser(userID[1]));
		
		createEvent2.setWhen(Calendar.getInstance());
		createEvent2.setSummary("event2 - summary");
		createEvent2.setDescription("event2 - description");
		createEvent2.setOwner(calendarUserDao.getUser(userID[2]));
		createEvent2.setAttendee(calendarUserDao.getUser(userID[0]));
		
		createEvent1Id = eventDao.createEvent(createEvent1);
		createEvent2Id = eventDao.createEvent(createEvent2);
		
		Event Event1Compare = new Event();
		Event Event2Compare = new Event();
		
		Event1Compare = eventDao.getEvent(createEvent1Id);
		Event2Compare = eventDao.getEvent(createEvent2Id);
		
		assertThat(Event1Compare.getSummary(), is(createEvent1.getSummary()));
		assertThat(Event1Compare.getDescription(), is(createEvent1.getDescription()));
		assertThat(Event1Compare.getOwner(), is(createEvent1.getOwner()));
		assertThat(Event1Compare.getAttendee(), is(createEvent1.getAttendee()));	
		
		assertThat(Event2Compare.getSummary(), is(createEvent2.getSummary()));
		assertThat(Event2Compare.getDescription(), is(createEvent2.getDescription()));
		assertThat(Event2Compare.getOwner(), is(createEvent2.getOwner()));
		assertThat(Event2Compare.getAttendee(), is(createEvent2.getAttendee()));
		
		//assertThat(Event1Compare.getSummary(), is(NewEvent1.getSummary()));
	}
	
	@Test
	public void getAllEvent() {
		// 4. 모든 Events를 가져오는 eventDao.getEvents()가 올바로 동작하는 지 (총 3개를 가지고 오는지) 확인하는 테스트 코드 작성  
		// [주의] fixture로 등록된 3개의 이벤트들에 대한 테스트
		List<Event> Find = new ArrayList<Event>();
		Find = eventDao.getEvents();
		System.out.println("4번 === 모든 Events 를 가져옴");
		for(int i=0; i<Find.size(); i++)
		{
			System.out.println("ID:"+Find.get(i).getId()+":::"+Find.get(i).getSummary() +":::"+ Find.get(i).getDescription());
		}
	}
	
	@Test
	public void getEvent() {
		// 5. owner ID가 3인 Event에 대해 findForOwner가 올바로 동작하는 지 확인하는 테스트 코드 작성  
		// [주의] fixture로 등록된 3개의 이벤트들에 대해서 owner ID가 3인 것인 1개의 이벤트뿐임 
		List<Event> FindEvent = new ArrayList<Event>();
		FindEvent = eventDao.findForOwner(userID[2]);
		System.out.println("5번 === OwnerID가 3인 Event의 개수 :::"+FindEvent.size());
	}
	
	@Test
	public void getOneUserByEmail() {
		int count = 0;
		// 6. email이 'user1@example.com'인 CalendarUser가 1명뿐임을 확인하는 테스크 코드 작성
		// [주의] public CalendarUser findUserByEmail(String email)를 테스트 하는 코드
		CalendarUser FindUser = new CalendarUser();
		FindUser = calendarUserDao.findUserByEmail("user1@example.com");
		if(FindUser.getEmail().equalsIgnoreCase("user1@example.com"))
		{
			count++;
		}
		System.out.println("6번 ==== email이 'user1@example.com'인 CalendarUser의 개수 :::"+count);
		System.out.println("6번에서 찾은 메일:::"+FindUser.getEmail());
	}
	
	@Test
	public void getTwoUserByEmail() {
		// 7. partialEmail이 'user'인 CalendarUser가 2명임을 확인하는 테스크 코드 작성
		// [주의] public List<CalendarUser> findUsersByEmail(String partialEmail)를 테스트 하는 코드
		List<CalendarUser> FindUser = new ArrayList<CalendarUser>();
		FindUser = calendarUserDao.findUsersByEmail("user");
		System.out.println("7번 ==== partialEmail이'user'인 CalendarUser가 2명임을 확인 :::"+FindUser.size());
	}
}
