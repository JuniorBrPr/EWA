package teamx.app.backend.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import teamx.app.backend.models.User;
import teamx.app.backend.repositories.UserRepository;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User foundUser = userRepository.findByEmail(user.getEmail());
        if (foundUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (!foundUser.getPassword().equals(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Wrong password");
        }

        return ResponseEntity.status(HttpStatus.OK).body(foundUser);
    }
}