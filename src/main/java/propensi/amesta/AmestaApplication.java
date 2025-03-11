package propensi.amesta;

import java.util.Date;

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
				if (userDb.findByUsername("direktur") != null) {
					user = new Direktur();
					user.setName("direktur");
					user.setUsername("direktur");
					user.setPassword(userService.hashPassword("direktur"));
					user.setEmail("direktur@example.com");
					user.setGender(false);
					user.setCreatedDate(new Date());
					user.setUpdatedAt(new Date());
					userDb.save(user);
				}
				if (userDb.findByUsername("sales") != null) {
					user = new Sales();
					user.setName("sales");
					user.setUsername("sales");
					user.setPassword(userService.hashPassword("sales"));
					user.setEmail("sales@example.com");
					user.setGender(false);
					user.setCreatedDate(new Date());
					user.setUpdatedAt(new Date());
					userDb.save(user);
				}
				if (userDb.findByUsername("general_manager") != null) {
					user = new GeneralManager();
					user.setName("general_manager");
					user.setUsername("general_manager");
					user.setPassword(userService.hashPassword("general_manager"));
					user.setEmail("gm@example.com");
					user.setGender(false);
					user.setCreatedDate(new Date());
					user.setUpdatedAt(new Date());
					userDb.save(user);
				}
				if (userDb.findByUsername("kepala_gudang") != null) {
					user = new KepalaGudang();
					user.setName("kepala_gudang");
					user.setUsername("kepala_gudang");
					user.setPassword(userService.hashPassword("kepala_gudang"));
					user.setEmail("kg@example.com");
					user.setGender(false);
					user.setCreatedDate(new Date());
					user.setUpdatedAt(new Date());
					userDb.save(user);
				}
				if (userDb.findByUsername("komisaris") != null) {
					user = new Komisaris();
					user.setName("komisaris");
					user.setUsername("komisaris");
					user.setPassword(userService.hashPassword("komisaris"));
					user.setEmail("komisaris@example.com");
					user.setGender(false);
					user.setCreatedDate(new Date());
					user.setUpdatedAt(new Date());
					userDb.save(user);
				}
			}
        };
    }
}