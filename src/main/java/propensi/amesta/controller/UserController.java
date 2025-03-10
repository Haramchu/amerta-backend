package propensi.amesta.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import propensi.amesta.model.EndUser.User;
import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.LoginJwtResponseDTO;
import propensi.amesta.payload.response.UserResponseDTO;
import propensi.amesta.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;

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
        if (role.equals("")){
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

}