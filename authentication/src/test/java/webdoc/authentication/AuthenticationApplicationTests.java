package webdoc.authentication;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webdoc.authentication.domain.entity.user.Doctor;
import webdoc.authentication.domain.entity.user.Patient;
import webdoc.authentication.domain.entity.user.Token;

@SpringBootTest
class AuthenticationApplicationTests {
	@Autowired
	EntityManager em;

	@Test
	@Transactional
	void contextLoads() {


	}

}
