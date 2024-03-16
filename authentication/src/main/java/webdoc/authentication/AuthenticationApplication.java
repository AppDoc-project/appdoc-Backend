package webdoc.authentication;

import org.apache.catalina.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class AuthenticationApplication {


	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApplication.class, args);



	}


}
