package propensi.amesta.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import propensi.amesta.model.EndUser.User;
import propensi.amesta.payload.response.UserResponseDTO;
import propensi.amesta.repository.EndUser.UserDb;
import propensi.amesta.security.service.UserDetailsServiceImpl;



@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDb userDb;

    @Override
    public UserResponseDTO getCurrentUser() {
        UserDetailsServiceImpl authentication = (UserDetailsServiceImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userFromDb = userDb.findByEmail(authentication.getJwtClaims().getSubject());
        User user = userFromDb.orElseThrow(() -> new NoSuchElementException("User not found"));

        return userToUserResponseDTO(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDb.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found for the provided email"));
    }

    @Override
    public String hashPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }  

    @Override
    public List<UserResponseDTO> getUserByRole(String role) {
        List<User> userList = new ArrayList<>();
        if (role.equals("all")) {
            userList = userDb.findAll();
        } else {
            userList = userDb.findByRole(role);
        }
        return userList.stream().map(this::userToUserResponseDTO).toList();
    }

    public UserResponseDTO userToUserResponseDTO(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getName(),
            user.getUsername(),
            user.getEmail(),
            user.isGender(),
            user.getCreatedDate(),
            user.getUpdatedAt(),
            user.getRole()
        );
    }

}
