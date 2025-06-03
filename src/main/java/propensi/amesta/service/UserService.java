package propensi.amesta.service;

import java.util.List;
import java.util.UUID;

import propensi.amesta.model.EndUser.User;
import propensi.amesta.payload.request.TambahKaryawanRequestDTO;
import propensi.amesta.payload.request.UpdateEmployeeRequestDTO;
import propensi.amesta.payload.request.UpdatePasswordRequestDTO;
import propensi.amesta.payload.request.UpdateProfileRequestDTO;
import propensi.amesta.payload.response.UserResponseDTO;

public interface UserService {
    UserResponseDTO getCurrentUser();
    
    List<UserResponseDTO> getUserByRole(String role);

    User getUserByEmail(String email);

    UserResponseDTO getById (UUID id);

    User getUserById(UUID id);

    String hashPassword(String password);

    UserResponseDTO addEmployee(TambahKaryawanRequestDTO karyawan);

    UserResponseDTO updateEmployee(UUID idEmployee, UpdateEmployeeRequestDTO employee);

    UserResponseDTO updateProfile(UUID id, UpdateProfileRequestDTO request);

    UserResponseDTO updatePassword(UUID id, UpdatePasswordRequestDTO request);
}
