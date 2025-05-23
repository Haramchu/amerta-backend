package propensi.amesta.controller;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import propensi.amesta.model.EndUser.User;
import propensi.amesta.payload.request.LoginJwtRequestDTO;
import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.Auth.LoginJwtResponseDTO;
import propensi.amesta.security.jwt.JwtUtils;
import propensi.amesta.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    Validator validator;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginJwtRequestDTO loginRequest) {
        var baseResponseDTO = new BaseResponseDTO<LoginJwtResponseDTO>();
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        User user = userService.getUserByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), "Invalid email atau password!",
                            new Date(), null));
        }

        String token = jwtUtils.generateJwtToken(user.getId(), user.getName(), user.getUsername(), user.getEmail(),
                user.getRole());
        String userName = user.getName();
        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(new LoginJwtResponseDTO(token, userName, user.getRole(), user.getId()));
        baseResponseDTO.setMessage("Login berhasil!");
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

    }

}