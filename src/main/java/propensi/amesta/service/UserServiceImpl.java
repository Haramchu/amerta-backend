package propensi.amesta.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import propensi.amesta.model.EndUser.Administrasi;
import propensi.amesta.model.EndUser.Direktur;
import propensi.amesta.model.EndUser.GeneralManager;
import propensi.amesta.model.EndUser.KepalaGudang;
import propensi.amesta.model.EndUser.Komisaris;
import propensi.amesta.model.EndUser.Sales;
import propensi.amesta.model.EndUser.User;
import propensi.amesta.payload.request.TambahKaryawanRequestDTO;
import propensi.amesta.payload.request.UpdateEmployeeRequestDTO;
import propensi.amesta.payload.request.UpdateProfileRequestDTO;
import propensi.amesta.payload.response.UserResponseDTO;
import propensi.amesta.repository.EndUser.UserDb;
import propensi.amesta.security.service.UserDetailsServiceImpl;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDb userDb;

    @Override
    public UserResponseDTO getCurrentUser() {
        UserDetailsServiceImpl authentication = (UserDetailsServiceImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Optional<User> userFromDb = userDb.findByEmail(authentication.getJwtClaims().getSubject());
        User user = userFromDb.orElseThrow(() -> new NoSuchElementException("Employee tidak ditemukan"));

        return userToUserResponseDTO(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDb.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Employee tidak ditemukan"));
    }

    @Override
    public UserResponseDTO getById(UUID id){
        User user = userDb.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee tidak ditemukan"));

        return userToUserResponseDTO(user);
    }

    @Override
    public User getUserById(UUID id) {
        return userDb.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee tidak ditemukan"));
    }

    @Override
    public String hashPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    @Override
    public List<UserResponseDTO> getUserByRole(String role) {
        List<User> userList;
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
                user.getPhone(),
                user.getHomePhone(),
                user.getBusinessPhone(),
                user.getWhatsappNumber(),
                user.getEntryDate(),
                user.getKtpNumber(),
                user.getNotes(),
                user.getBirthDate(),
                user.isEmployeeStatus(),
                user.getCreatedDate(),
                user.getUpdatedAt(),
                user.getRole());
    }

    @Override
    public UserResponseDTO addEmployee(TambahKaryawanRequestDTO userRequest) {
        if (userDb.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email sudah terdaftar.");
        }
        User newUser;
        switch (userRequest.getRole().toLowerCase()) {
            case "administrasi" -> newUser = new Administrasi();
            case "direktur" -> newUser = new Direktur();
            case "sales" -> newUser = new Sales();
            case "general_manager" -> newUser = new GeneralManager();
            case "kepala_gudang" -> newUser = new KepalaGudang();
            case "komisaris" -> newUser = new Komisaris();
            default -> throw new IllegalArgumentException("Role tidak valid: " + userRequest.getRole());
        }
        newUser.setName(userRequest.getName());
        newUser.setUsername(userRequest.getUsername());
        newUser.setEmail(userRequest.getEmail());
        newUser.setPassword(hashPassword(userRequest.getPassword()));
        newUser.setGender(userRequest.isGender());
        newUser.setPhone(userRequest.getPhone());
        newUser.setHomePhone(userRequest.getHomePhone());
        newUser.setBusinessPhone(userRequest.getBusinessPhone());
        newUser.setWhatsappNumber(userRequest.getWhatsappNumber());
        newUser.setEntryDate(userRequest.getEntryDate());
        newUser.setKtpNumber(userRequest.getKtpNumber());
        newUser.setNotes(userRequest.getNotes());
        newUser.setBirthDate(userRequest.getBirthDate());
        newUser.setEmployeeStatus(userRequest.isEmployeeStatus());
        newUser.setRole(userRequest.getRole());
         
        User userNew = userDb.save(newUser);

        return userToUserResponseDTO(userNew);
    }

    @Override
    public UserResponseDTO updateProfile(UUID id, UpdateProfileRequestDTO request) {
        User currentUser = getUserById(id);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {

            if (request.getOldPassword() == null || request.getOldPassword().isEmpty()) {
                throw new IllegalArgumentException("Password lama wajib diisi.");
            }

            if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
                throw new IllegalArgumentException("Password lama tidak sesuai.");
            }

            List<User> allUsers = userDb.findAll();
            for (User u : allUsers) {
                if (!u.getId().equals(currentUser.getId())) {
                    if (passwordEncoder.matches(request.getPassword(), u.getPassword())) {
                        throw new IllegalArgumentException("Password sudah digunakan.");
                    }
                }
            }

            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = userDb.save(currentUser);
        return userToUserResponseDTO(updated);
    }

    @Override
    public UserResponseDTO updateEmployee(UUID idEmployee, UpdateEmployeeRequestDTO request) {
        User employee = userDb.findById(idEmployee)
                .orElseThrow(() -> new IllegalArgumentException("Employee tidak ditemukan."));

        // Update all allowed fields
        employee.setPhone(request.getPhone());
        employee.setHomePhone(request.getHomePhone());
        employee.setBusinessPhone(request.getBusinessPhone());
        employee.setWhatsappNumber(request.getWhatsappNumber());
        employee.setNotes(request.getNotes());
        employee.setEmployeeStatus(request.isEmployeeStatus());
        employee.setRole(request.getRole());

        User updated = userDb.save(employee);
        return userToUserResponseDTO(updated);
    }
}
