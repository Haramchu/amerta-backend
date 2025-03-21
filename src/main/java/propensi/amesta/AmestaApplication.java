package propensi.amesta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import jakarta.transaction.Transactional;
import propensi.amesta.model.Aset.AlamatGudang;
import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Aset.Gudang;
import propensi.amesta.model.Aset.StockBarangPerGudang;
import propensi.amesta.model.EndUser.Administrasi;
import propensi.amesta.model.EndUser.Direktur;
import propensi.amesta.model.EndUser.GeneralManager;
import propensi.amesta.model.EndUser.KepalaGudang;
import propensi.amesta.model.EndUser.Komisaris;
import propensi.amesta.model.EndUser.Sales;
import propensi.amesta.model.EndUser.User;
import propensi.amesta.repository.Aset.BarangDb;
import propensi.amesta.repository.Aset.GudangDb;
import propensi.amesta.repository.EndUser.UserDb;
import propensi.amesta.service.UserService;

@SpringBootApplication
public class AmestaApplication {
	public static void main(String[] args) {
		SpringApplication.run(AmestaApplication.class, args);
	}

	@Bean
    @Transactional
    CommandLineRunner run(UserDb userDb, UserService userService, GudangDb gudangDb, BarangDb barangDb) {
        return args -> {
            createUserIfNotExists(userDb, userService, new Administrasi(), "admin", "admin@example.com", "admin", "ADMIN");
            createUserIfNotExists(userDb, userService, new Direktur(), "direktur", "direktur@example.com", "direktur", "DIREKTUR");
            createUserIfNotExists(userDb, userService, new Sales(), "sales", "sales@example.com", "sales", "SALES");
            createUserIfNotExists(userDb, userService, new GeneralManager(), "general_manager", "gm@example.com", "general_manager", "GENERAL_MANAGER");
            createUserIfNotExists(userDb, userService, new KepalaGudang(), "kepala_gudang", "kg@example.com", "kepala_gudang", "KEPALA_GUDANG");
            createUserIfNotExists(userDb, userService, new Komisaris(), "komisaris", "komisaris@example.com", "komisaris", "KOMISARIS");

            Optional<User> kp = userDb.findByUsername("kepala_gudang");
            if (kp.isPresent() && kp.get() instanceof KepalaGudang) {
                KepalaGudang kepalaGudang = (KepalaGudang) kp.get();
                Gudang gud = createGudangDummy(kepalaGudang, gudangDb);
                createBarangDummy(gud, barangDb);
            }
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

    private Gudang createGudangDummy(KepalaGudang kepalaGudang, GudangDb gudangDb){
        AlamatGudang alamatGudang = new AlamatGudang();
        alamatGudang.setAlamat("Jl. Gudang No. 1");
        alamatGudang.setKota("Jakarta");
        alamatGudang.setProvinsi("DKI Jakarta");
        alamatGudang.setKodePos("12345");

        Gudang gudang = new Gudang();
        gudang.setNama("Gudang 1");
        gudang.setDeskripsi("Gudang 1 Deskripsi");
        gudang.setKapasitas(1000);
        gudang.setKepalaGudang(kepalaGudang);
        gudang.setAlamatGudang(alamatGudang);
        gudang.setCreatedDate(new Date());
        gudang.setUpdatedDate(new Date());
        alamatGudang.setGudang(gudang);

        return gudangDb.save(gudang);
    }

    private void createBarangDummy(Gudang gudang, BarangDb barangDb) {
        for (int i = 0; i < 10; i++) {
            String id = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .replace("-01-", "-I-")
            .replace("-02-", "-II-")
            .replace("-03-", "-III-")
            .replace("-04-", "-IV-")
            .replace("-05-", "-V-")
            .replace("-06-", "-VI-")
            .replace("-07-", "-VII-")
            .replace("-08-", "-VIII-")
            .replace("-09-", "-IX-")
            .replace("-10-", "-X-")
            .replace("-11-", "-XI-")
            .replace("-12-", "-XII-");
    
            id += "-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            Barang barang = new Barang();
            barang.setId(id);
            barang.setNama("Barang " + i);
            barang.setKategori("Kategori " + i);
            barang.setMerk("Merk " + i);
            barang.setActive(true);

            List<StockBarangPerGudang> listBarang = new ArrayList<>();
            StockBarangPerGudang stockBarang = new StockBarangPerGudang();
            stockBarang.setBarang(barang);
            stockBarang.setStock(10+i);
            stockBarang.setGudang(gudang);
            listBarang.add(stockBarang);

            barang.setListStockBarang(listBarang);

            barangDb.save(barang);
        }
    }
}