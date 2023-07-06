package cloud.test.service;

import cloud.framework.page.Page;
import cloud.framework.page.PageData;
import cloud.test.entity.ChatRobot;
import java.util.List;

public interface ChatRobotService {

	ChatRobot get(String id);

	List<ChatRobot> list(ChatRobot chatRobot);

	PageData listPage(Page page);



	String query(String query);

	String empathyComputing(String query);
	
	String dialoguePolicy(String query);
	
	String contextUnderstandHandler(String query);

	String userUnderstandHandler(String message);
	
	String systemUnderstandHandler(String message);

	String domainChat(String query);

	String generalChat(String query);

	String skillHandler(String query);

	
}