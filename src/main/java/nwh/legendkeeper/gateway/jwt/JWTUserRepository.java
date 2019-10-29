package nwh.legendkeeper.gateway.jwt;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface used to define all 
 * User Models related data access and manipulation 
 * 
 * @author Nathanial.Heard
 *
 */
@Repository
public interface JWTUserRepository extends MongoRepository<JWTUser, String> {

	JWTUser findOneByJwt(@Param("jwt") String jwt);
	
	JWTUser findOneByUsername(@Param("username") String username);
	
}
