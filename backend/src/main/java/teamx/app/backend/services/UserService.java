package teamx.app.backend.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import teamx.app.backend.models.InventoryOrder;
import teamx.app.backend.models.Team;
import teamx.app.backend.models.User;
import teamx.app.backend.repositories.OrderRepository;
import teamx.app.backend.repositories.TeamRepository;
import teamx.app.backend.repositories.UserRepository;
import teamx.app.backend.utils.DTO.UserDTO ;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private final TeamRepository teamRepository;

    @Autowired
    public UserService(UserRepository userRepository, OrderRepository orderRepository, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.teamRepository = teamRepository;
    }

    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }


    public List<User> getAllByTeamId(Long teamId) {
        if (teamId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team id is null");
        }

        List<User> users = userRepository.getAllByTeam_Id(teamId);

        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Users not found");
        }

        return users;
    }

    public List<User> getAll() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Users not found");
        }

        return users;
    }

    public User getById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
                );
    }

    public User add(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this email already exists");
        }

        return userRepository.save(user);
    }

    public User update(UserDTO user, Long id) {
        User existingUser = getById(id);
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setRole(User.Role.valueOf(user.getRole()));
        existingUser.setTeam(user.getTeamId() != null ?
                teamRepository.findById(user.getTeamId()).orElse(null) :
                null
        );
        return userRepository.save(existingUser);
    }

    public User resetPass(long id, String password) {
        User existingUser = getById(id);
        existingUser.setPassword(password);

        return userRepository.save(existingUser);
    }

    @Transactional
    public User delete(Long userId) {
        // Retrieve all inventoryOrders related to the user
        List<InventoryOrder> inventoryOrders = orderRepository.findAllByOrderedById(userId);
        User user = getById(userId);

        // Remove the user from all inventoryOrders related to the user
        for (InventoryOrder inventoryOrder : inventoryOrders) {
            inventoryOrder.setOrderedBy(null);
            orderRepository.save(inventoryOrder);
        }
        
        userRepository.deleteById(userId);

        if (userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User could not be deleted");
        }

        return user;
    }


    public List<User> getAllByIds(List<Long> membersIds) {
        List<User> users = userRepository.getAllByIdIn(membersIds);

        if (users.size() != membersIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Users not found");
        }

        return users;
    }

    public List<User> getAllByNoTeam() {
        List<User> users = userRepository.getAllByTeamIsNull();

        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Users not found");
        }

        return users;
    }

    public List<User> setTeam(List<Long> membersIds, Team team) {
        validateInput(membersIds, team);
        List<User> users = getAllByIds(membersIds);
        unsetUsersFromTeam(team, membersIds);
        return setUserTeamAndSave(users, team);
    }

    private void validateInput(List<Long> membersIds, Team team) {
        if (membersIds == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User IDs are null");
        }
        if (team == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team is null");
        }
    }

    private void unsetUsersFromTeam(Team team, List<Long> membersIds) {
        List<User> usersToUnset = userRepository.getAllByTeam_IdAndIdNotIn(team.getId(), membersIds);
        usersToUnset.forEach(user -> user.setTeam(null));
        userRepository.saveAll(usersToUnset);
    }

    private List<User> setUserTeamAndSave(List<User> users, Team team) {
        users.forEach(user -> user.setTeam(team));
        List<User> savedUsers = userRepository.saveAll(users);
        if (savedUsers.size() != users.size()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Users could not be set to team");
        }
        return savedUsers;
    }

    public Collection<Object> getAllByWarehouseId(Long warehouseId) {
        return userRepository.getAllByTeam_Warehouse_Id(warehouseId);
    }
}