package cloud.test.controller;

import cloud.framework.page.Page;
import cloud.framework.page.PageData;
import cloud.framework.result.Result;
import cloud.framework.result.ResultUtils;
import cloud.test.entity.ChatRobot;
import cloud.test.service.ChatRobotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天机器人
 * @author xmc
 */
@RestController
@RequestMapping("/chatRobot")
@CrossOrigin(origins = "*", allowCredentials = "true", maxAge = 3600)
public class ChatRobotController {
	
	@Autowired
	private ChatRobotService chatbotService;

	@PostMapping("/get")
	public Result get(String id) {


//		WebApplicationContext con = ContextLoader.getCurrentWebApplicationContext();
//		Object ooo = con.getBean("chatRobotServiceImpl");
//		System.out.println(ooo.toString());
//		Object yyy = con.getBean("chatRobotService");
//		System.out.println(yyy.toString());
		ChatRobot chatRobot = chatbotService.get(id);
		return ResultUtils.success("查询成功！", chatRobot);
	}

	@PostMapping("/list")
	public Result list(@RequestBody ChatRobot chatRobot) {
		List<ChatRobot> chatRobots = chatbotService.list(chatRobot);
		return ResultUtils.success("查询成功！", chatRobots);
	}

	@PostMapping("/listPage")
	public Result listPage(@RequestBody Page page) {
		PageData pageData = chatbotService.listPage(page);
		return ResultUtils.success("查询成功！", pageData);
	}

}