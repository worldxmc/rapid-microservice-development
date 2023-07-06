package cloud.test.service.impl;

import cloud.framework.page.Page;
import cloud.framework.page.PageData;
import cloud.framework.util.HttpUtils;
import cloud.framework.web.context.SysContext;
import cloud.test.entity.ChatRobot;
import cloud.test.mapper.ChatRobotMapper;
import cloud.test.service.ChatRobotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ChatRobotServiceImpl implements ChatRobotService {

	@Autowired
	private ChatRobotMapper chatRobotMapper;

	@Override
	public ChatRobot get(String id) {
		return chatRobotMapper.get(id);
	}

	@Override
	public List<ChatRobot> list(ChatRobot chatRobot) {
		return chatRobotMapper.list(chatRobot);
	}

	@Override
	public PageData listPage(Page page) {
		List<ChatRobot> chatRobots = chatRobotMapper.listPage(page);
		PageData pageData = new PageData<>();
		pageData.setTotalSize(page.getTotalSize());
		pageData.setData(chatRobots);
		return pageData;
	}

	/**
	 * 入口方法
	 */
	@Override
	public String query(String query) {
		// 移情计算
		String ec = empathyComputing(query);
		// 对话策略
		String dp = dialoguePolicy(ec);
		
		System.out.println(dp);
		
		return query;
	}

	/**
	 * 移情计算
	 */
	@Override
	public String empathyComputing(String query) {
		// 上下文理解
		String cu = contextUnderstandHandler(query);
		// 用户理解
		String uu = userUnderstandHandler(cu);
		// 系统理解
		String su = systemUnderstandHandler(uu);
		return su;
	}

	/**
	 * 对话策略
	 */
	@Override
	public String dialoguePolicy(String query) {
		// 通用闲聊
		String gc = generalChat(query);
		// 领域闲聊
		String dc = domainChat(gc);
		// 技能分派
		String skill = skillHandler(dc);
		return skill;
	}

	/**
	 * 上下文理解
	 */
	@Override
	public String contextUnderstandHandler(String message) {
		String result = null;

		// 1.词性标注
		result = partOfSpeechTagging(message);

		// 2.实体链接：识别出Q中的实体，并与之前的状态中存储的实体进行链接。

		// 3.指代消解：把Q中所有代词替换为它们指代的实体。

		// 4.句子补全：如果Q不是一个完整的句子，就使用C对它进行补全。

		return result;
	}

	/**
	 * 用户理解
	 */
	@Override
	public String userUnderstandHandler(String message) {
		// 话题：记录了用户当前对话的话题。
		// 意图：存储的是用户当前对话的动作，如greet，request，inform等。就是任务型对话NLU中的动作（act）。
		// 情感：记录了用户当前的情绪，如高兴，伤心，生气，中立等。
		// 观点：存储了用户对当前话题的观点，如正向，负向，中立。
		// 用户：如果用户可辨认，可以获取到他的个人资料，如年龄，性别，兴趣爱好，职业等。
		return message;
	}

	/**
	 * 系统理解
	 */
	@Override
	public String systemUnderstandHandler(String message) {
		// 处理一些变的和不变的内容
		return message;
	}

	/**
	 * 通用闲聊
	 */
	@Override
	public String domainChat(String query) {
		return null;
	}

	/**
	 * 领域闲聊
	 */
	@Override
	public String generalChat(String query) {
		return null;
	}

	/**
	 * 技能分派
	 */
	@Override
	public String skillHandler(String query) {
		return null;
	}

	/**
	 * 词性标注
	 * 
	 * n/名词 np/人名 ns/地名 ni/机构名 nz/其它专名 m/数词 q/量词 mq/数量词 t/时间词 f/方位词 s/处所词 v/动词
	 * vm/能愿动词 vd/趋向动词 a/形容词 d/副词 h/前接成分 k/后接成分 i/习语 j/简称 r/代词 c/连词 p/介词 u/助词
	 * y/语气助词 e/叹词 o/拟声词 g/语素 w/标点 x/其它
	 * 
	 * THULAC：一个高效的中文词法分析工具包 版权所有：清华大学自然语言处理与社会人文计算实验室
	 */
	private String partOfSpeechTagging(String message) {
		String url = "http://thulac.thunlp.org/getResult";
		// TODO
		return HttpUtils.get(url);
	}

}