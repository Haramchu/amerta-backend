package propensi.amesta;

import java.util.Date;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import jakarta.transaction.Transactional;
import propensi.amesta.model.EndUser.Administrasi;
import propensi.amesta.model.EndUser.Direktur;
import propensi.amesta.model.EndUser.GeneralManager;
import propensi.amesta.model.EndUser.KepalaGudang;
import propensi.amesta.model.EndUser.Komisaris;
import propensi.amesta.model.EndUser.Sales;
import propensi.amesta.model.EndUser.User;
import propensi.amesta.repository.EndUser.UserDb;
import propensi.amesta.service.UserService;
import org.springframework.context.annotation.Bean;

import jakarta.transaction.Transactional;
import propensi.amesta.model.EndUser.Administrasi;
import propensi.amesta.model.EndUser.Direktur;
import propensi.amesta.model.EndUser.GeneralManager;
import propensi.amesta.model.EndUser.KepalaGudang;
import propensi.amesta.model.EndUser.Komisaris;
import propensi.amesta.model.EndUser.Sales;
import propensi.amesta.model.EndUser.User;
import propensi.amesta.repository.EndUser.UserDb;
import propensi.amesta.service.UserService;

@SpringBootApplication
public class AmestaApplication {
	public static void main(String[] args) {
		SpringApplication.run(AmestaApplication.class, args);
	}

	@Bean
    @Transactional
    CommandLineRunner run(UserDb userDb, UserService userService) {
        return args -> {
            createUserIfNotExists(userDb, userService, new Administrasi(), "admin", "admin@example.com", "admin", "ADMIN");
            createUserIfNotExists(userDb, userService, new Direktur(), "direktur", "direktur@example.com", "direktur", "DIREKTUR");
            createUserIfNotExists(userDb, userService, new Sales(), "sales", "sales@example.com", "sales", "SALES");
            createUserIfNotExists(userDb, userService, new GeneralManager(), "general_manager", "gm@example.com", "general_manager", "GENERAL_MANAGER");
            createUserIfNotExists(userDb, userService, new KepalaGudang(), "kepala_gudang", "kg@example.com", "kepala_gudang", "KEPALA_GUDANG");
            createUserIfNotExists(userDb, userService, new Komisaris(), "komisaris", "komisaris@example.com", "komisaris", "KOMISARIS");
        };
    }

    private void createUserIfNotExists(UserDb userDb, UserService userService, User user, String username, String email, String password, String role) {
        Optional<User> existingUser = userDb.findByUsername(username);
        if (existingUser.isEmpty()) {
            user.setName(username);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(userService.hashPassword(password));
            user.setGender(false);
            user.setPhone("08123456789");
            user.setHomePhone("021567890");
            user.setBusinessPhone("021123456");
            user.setWhatsappNumber("08123456789");
            user.setEntryDate(new Date());
            user.setKtpNumber("1234567890123456");
            user.setNotes("User dummy untuk testing");
            user.setRole(role);
            user.setCreatedDate(new Date());
            user.setUpdatedAt(new Date());

            userDb.save(user);
            System.out.println("User " + username + " berhasil ditambahkan.");
        } else {
            System.out.println("User " + username + " sudah ada, tidak ditambahkan.");
        }
    }
}