package cloud.test.mapper;

import cloud.framework.page.Page;
import cloud.test.entity.ChatRobot;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Mapper
public interface ChatRobotMapper {

	ChatRobot get(String id);

	List<ChatRobot> list(ChatRobot chatRobot);

	List<ChatRobot> listPage(Page page);

}