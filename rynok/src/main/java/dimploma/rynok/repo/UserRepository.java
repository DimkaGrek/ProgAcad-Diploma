package dimploma.rynok.repo;

import dimploma.rynok.model.CustomUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<CustomUser, Long> {

    // find user by login
    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT u FROM CustomUser u where u.login = :login")
    CustomUser findByLogin(@Param("login") String login);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM CustomUser u WHERE u.login = :login")
    boolean existsByLogin(@Param("login") String login);

    @Query(value = "SELECT counter_id FROM user_counters WHERE user_id = :user", nativeQuery = true)
    List<Long> findCountersIdsByUserId(@Param("user") Long user);
}
