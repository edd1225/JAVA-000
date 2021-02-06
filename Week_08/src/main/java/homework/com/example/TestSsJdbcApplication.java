package homework.com.example;

import homework.com.example.testxa.TransactionConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan("homework.com.example.service")
@MapperScan("homework.com.example.dao.mapper")
@Import(TransactionConfiguration.class)
public class TestSsJdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestSsJdbcApplication.class, args);
    }

}
