package webdoc.authentication;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.user.Patient;
import webdoc.authentication.domain.entity.user.Token;

@SpringBootApplication

public class AuthenticationApplication {


	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApplication.class, args);
	}


}
