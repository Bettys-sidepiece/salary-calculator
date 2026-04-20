package salary.calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"repo","model"})
@EnableJpaRepositories(basePackages = {"repo"})
public class AppEntryPoint {
    public static void main(String[] args) {
        SpringApplication.run(AppEntryPoint.class, args);
    }
}
