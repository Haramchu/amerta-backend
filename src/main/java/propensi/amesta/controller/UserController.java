package propensi.amesta.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import propensi.amesta.payload.request.TambahKaryawanRequestDTO;
import propensi.amesta.payload.request.UpdateProfileRequestDTO;
import propensi.amesta.payload.request.UpdateEmployeeRequestDTO;
import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.UserResponseDTO;
import propensi.amesta.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<BaseResponseDTO<UserResponseDTO>> getUser() {
        UserResponseDTO user = userService.getCurrentUser();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponseDTO<>(HttpStatus.OK.value(), "User successfully retrieved!", new Date(), user));
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponseDTO<List<UserResponseDTO>>> getAllUser(
            @RequestParam(value = "role", required = false) String role) {
        var baseResponseDTO = new BaseResponseDTO<List<UserResponseDTO>>();
        List<UserResponseDTO> userList = userService.getUserByRole(role);
        if (role.equals("")) {
            userList = userService.getUserByRole("all");
        } else {
            userList = userService.getUserByRole(role);
        }
        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(userList);
        baseResponseDTO.setMessage("User successfully retrieved!");
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        BaseResponseDTO<UserResponseDTO> response = new BaseResponseDTO<>();
        try {
            UserResponseDTO user = userService.getById(id);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("User berhasil ditemukan.");
            response.setData(user);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("User tidak ditemukan: " + e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid TambahKaryawanRequestDTO userRequest) {
        var baseResponseDTO = new BaseResponseDTO<TambahKaryawanRequestDTO>();

        try {
            userService.addEmployee(userRequest);
            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(userRequest);
            baseResponseDTO.setMessage("Registrasi berhasil!");
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), new Date(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Terjadi kesalahan saat registrasi!", new Date(), null));
        }
    }

    @PutMapping("/update-profile/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable("id") String id,
            @RequestBody @Valid UpdateProfileRequestDTO request) {

        var baseResponseDTO = new BaseResponseDTO<UserResponseDTO>();

        try {
            UserResponseDTO updatedUser = userService.updateProfile(id, request);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(updatedUser);
            baseResponseDTO.setMessage("Profil berhasil diperbarui!");
            baseResponseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), new Date(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Terjadi kesalahan saat update profil!", new Date(), null));
        }
    }

    @PutMapping("/update/{idEmployee}")
    public ResponseEntity<?> updateEmployee(@PathVariable String idEmployee, @Valid @RequestBody UpdateEmployeeRequestDTO requestDTO) {
        BaseResponseDTO<UserResponseDTO> baseResponseDTO = new BaseResponseDTO<>();

        try {
            UserResponseDTO result = userService.updateEmployee(idEmployee, requestDTO);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Employee berhasil diperbarui!");
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setData(result);

            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Terjadi kesalahan pada server: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}