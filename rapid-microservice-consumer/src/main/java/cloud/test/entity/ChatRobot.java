package cloud.test.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 聊天机器人
 * @author xmc
 */
@Data
public class ChatRobot {
	
	private String id;
	
	private String name;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date gmtCreate;

}
