package dimploma.rynok.repo;

import dimploma.rynok.enums.Role;
import dimploma.rynok.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RolesRepository extends JpaRepository<Roles, Long> {

//    @Query("SELECT CASE WHEN EXISTS (SELECT r FROM Roles r WHERE r.role = :role) THEN true ELSE false END FROM Roles")
//    boolean existsByRole(@Param("role") Role role);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Roles r WHERE r.role = :role")
    boolean existsByRole(@Param("role") Role role);
}
