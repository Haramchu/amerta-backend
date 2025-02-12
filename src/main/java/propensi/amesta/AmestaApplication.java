package propensi.amesta;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import jakarta.transaction.Transactional;

import propensi.amesta.repository.EndUser.UserDb;
import propensi.amesta.service.UserService;
import propensi.amesta.model.EndUser.User;
import propensi.amesta.model.EndUser.Administrasi;

@SpringBootApplication
public class AmestaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmestaApplication.class, args);
	}

	@Bean
    @Transactional
    CommandLineRunner run(UserDb userDb, UserService userService) {
        return args -> {

            User user;
			if (userDb.findAll() != null){
				if (userDb.findByUsername("admin") != null) {
					user = new Administrasi();
					user.setName("admin");
					user.setUsername("admin");
					user.setPassword(userService.hashPassword("admin"));
					user.setEmail("admin@example.com");
					user.setGender(false);
					user.setCreatedDate(new Date());
					user.setUpdatedAt(new Date());
					userDb.save(user);
				}
			}
        };
    }
}
