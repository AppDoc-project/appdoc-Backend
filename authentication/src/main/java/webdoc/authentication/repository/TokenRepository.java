package webdoc.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webdoc.authentication.domain.entity.user.Token;
@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {
}
