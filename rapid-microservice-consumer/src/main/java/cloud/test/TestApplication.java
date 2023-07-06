package cloud.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import java.util.Arrays;

@SpringBootApplication
public class TestApplication implements CommandLineRunner {
	
	@Autowired
	private ApplicationContext appContext;
	
	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
    	String[] beans = appContext.getBeanDefinitionNames();
		Arrays.sort(beans);
		for (String bean : beans) {
		    System.out.println(bean);
		}
    }
}