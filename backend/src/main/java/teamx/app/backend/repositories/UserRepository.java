package teamx.app.backend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import teamx.app.backend.models.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Interface of repository of user
 * @see User
 *
 * @author Junior Javier Brito Perez
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> getAllByTeam_Id(Long teamId);

    List<User> getAllByTeamIsNull();

    List<User> getAllByIdIn(List<Long> ids);

    List<User> getAllByTeam_IdAndIdNotIn(Long id, List<Long> membersIds);

    List<User> findByRole(User.Role role);

    Collection<Object> getAllByTeam_Warehouse_Id(Long warehouseId);
}