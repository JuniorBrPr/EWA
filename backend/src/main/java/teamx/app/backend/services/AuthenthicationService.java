package teamx.app.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import teamx.app.backend.utils.DTO.UserDTO;
import teamx.app.backend.models.JWToken;
import teamx.app.backend.models.User;
import teamx.app.backend.repositories.UserRepository;

import java.util.Optional;

@Service
public class AuthenthicationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JWToken jwtTokenGenerator;

    @Autowired
    public AuthenthicationService(UserRepository userRepository, JWToken jwtTokenGenerator) {
        this.userRepository = userRepository;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    /**
     * Authenticates a user based on the provided credentials.
     *
     * @param inputUser The User object containing login credentials.
     * @return UserDTO with user details and JWT token on successful authentication,
     * null otherwise.
     */
    public UserDTO authenticateUser(User inputUser) {
        Optional<User> userOptional = userRepository.findByEmail(inputUser.getEmail());

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            if (passwordMatches(existingUser.getPassword(), inputUser.getPassword())) {
                String jwtToken = jwtTokenGenerator.generateToken(existingUser.getId(), existingUser.getEmail(),
                        existingUser.getRole().name());

                return UserDTO.builder()
                        .id(existingUser.getId())
                        .email(existingUser.getEmail())
                        .role(existingUser.getRole().name())
                        .jwtToken(jwtToken)
                        .build();
            }
        }
        return null;
    }

    public UserDTO generateResetPassToken(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            String jwtToken = jwtTokenGenerator.generateToken(existingUser.getId(), existingUser.getEmail(),
                    existingUser.getRole().name());

            return UserDTO.builder()
                    .id(existingUser.getId())
                    .email(existingUser.getEmail())
                    .role(existingUser.getRole().name())
                    .jwtToken(jwtToken)
                    .build();
        }

        return null;
    }


    private boolean passwordMatches(String storedPassword, String enteredPassword) {
        // Use the password encoder to check if entered password matches the stored hashed password
        //return passwordEncoder.matches(enteredPassword, storedPassword);
        return storedPassword.equals(enteredPassword);
    }

}
