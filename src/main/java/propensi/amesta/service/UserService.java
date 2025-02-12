package propensi.amesta.service;

import propensi.amesta.model.EndUser.User;
import propensi.amesta.payload.response.UserResponseDTO;

public interface UserService {
    UserResponseDTO getCurrentUser();
    User getUserByEmail(String email);
    String hashPassword(String password);
}
