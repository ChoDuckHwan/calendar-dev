package com.mycompany.myapp.dao;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.mycompany.myapp.domain.CalendarUser;
import com.mycompany.myapp.domain.Event;
public class DaoTest {
	
	private static void ShowCalendarUser(CalendarUserDao calendarUserDao, int userCount) throws ClassNotFoundException, SQLException
	{
		for(int i = 1; i < userCount + 1; i++)
		{
			CalendarUser test = calendarUserDao.getUser(i);
			System.out.println("id    : " + test.getId());
			System.out.println("Name  : " + test.getName());
			System.out.println("Email : " + test.getEmail());
			System.out.println("----------------------");
			if(i==userCount)
			{
				System.out.println("======CalendarUser정보 출력 끝======");
			}
		}
	}
	
	private static void ShowEvent(EventDao eventDao, int EventCount) throws SQLException, ClassNotFoundException
	{

		for(int i = 100; i < EventCount + 100; i++)
		{
			Event EventTest = eventDao.getEvent(i);
			System.out.println("Owner : " + EventTest.getOwner().getName());
			System.out.println("Email : " + EventTest.getAttendee().getEmail());
			//System.out.println("when : " + EventTest.getWhen());
			System.out.println("----------------------");
			if(i==EventCount+99)
			{
				System.out.println("======Event정보 출력 끝======");
			}
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		ApplicationContext context =  new GenericXmlApplicationContext("com/mycompany/myapp/applicationContext.xml");
		
		CalendarUserDao calendarUserDao = context.getBean("userDao", JdbcCalendarUserDao.class);
		EventDao eventDao = context.getBean("eventDao", JdbcEventDao.class);

	
		//1. 디폴트로 등록된 CalendarUser 3명 출력 (패스워드 제외한 모든 내용 출력)
		
		System.out.println("-------------------------");
		System.out.println("Default 등록 CalendarUser ");
		ShowCalendarUser(calendarUserDao, 3);
		System.out.println("-------------------------");
		
		//2. 디폴트로 등록된 Event 3개 출력 (owner와 attendee는 해당 사용자의 이메일과 이름을 출력) 
		
		System.out.println("----------------------");
		System.out.println("Default 등록 Event");
		ShowEvent(eventDao, 3);
		System.out.println("--------------------");
	
		//3. 새로운 CalendarUser 2명 등록 및 각각 id 추출
		//4. 추출된 id와 함께 새로운 CalendarUser 2명을 DB에서 가져와 (getUser 메소드 사용) 방금 등록된 2명의 사용자와 내용 (이메일, 이름, 패스워드)이 일치하는 지 비교
		//5. 5명의 CalendarUser 모두 출력 (패스워드 제외한 모든 내용 출력)
		
		//6. 새로운 Event 2개 등록 및 각각 id 추출
		//7. 추출된 id와 함께 새로운 Event 2개를 DB에서 가져와 (getEvent 메소드 사용) 방금 추가한 2개의 이벤트와 내용 (when, summary, description, owner, attendee)이 일치하는 지 비교
		//8. 5개의 Event 모두 출력 (owner와 attendee는 해당 사용자의 이메일과 이름을 출력)
	}
}
