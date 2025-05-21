package propensi.amesta.service;

import java.util.List;

import propensi.amesta.model.EndUser.User;
import propensi.amesta.payload.request.TambahKaryawanRequestDTO;
import propensi.amesta.payload.request.UpdateProfileRequestDTO;
import propensi.amesta.payload.response.UserResponseDTO;

public interface UserService {
    UserResponseDTO getCurrentUser();
    
    List<UserResponseDTO> getUserByRole(String role);

    User getUserByEmail(String email);

    User getUserById(String id);

    String hashPassword(String password);

    UserResponseDTO addEmployee (TambahKaryawanRequestDTO karyawan); 

    UserResponseDTO updateProfile(String id, UpdateProfileRequestDTO request);
}
