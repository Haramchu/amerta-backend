package propensi.amesta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import jakarta.transaction.Transactional;
import propensi.amesta.model.Customer;
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
import propensi.amesta.repository.CustomerDb;
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
    CommandLineRunner run(UserDb userDb, UserService userService, GudangDb gudangDb, BarangDb barangDb, CustomerDb customerDb) {
        return args -> {
            createUserIfNotExists(userDb, userService, new Administrasi(), "admin", "admin@example.com", "admin", "ADMIN");
            createUserIfNotExists(userDb, userService, new Direktur(), "direktur", "direktur@example.com", "direktur", "DIREKTUR");
            createUserIfNotExists(userDb, userService, new Sales(), "sales", "sales@example.com", "sales", "SALES");
            createUserIfNotExists(userDb, userService, new GeneralManager(), "general_manager", "gm@example.com", "general_manager", "GENERAL_MANAGER");
            createUserIfNotExists(userDb, userService, new KepalaGudang(), "kepala_gudang", "kg@example.com", "kepala_gudang", "KEPALA_GUDANG");
            createUserIfNotExists(userDb, userService, new Komisaris(), "komisaris", "komisaris@example.com", "komisaris", "KOMISARIS");

            Optional<User> kp = userDb.findByUsername("kepala_gudang");
            if (kp.isPresent() && kp.get() instanceof KepalaGudang kepalaGudang) {
                Gudang gud = createGudangIfNotExists("Gudang 1", "Jl. Gudang No. 1", kepalaGudang, gudangDb);
                Gudang gud2 = createGudangIfNotExists("Gudang 2", "Jl. Gudang No. 2", kepalaGudang, gudangDb);
                createBarangDummyIfNotExists(barangDb, gud, gud2);
            }

            createCustomerIfNotExists(customerDb, "customer@example.com");
        };
    }

    private void createUserIfNotExists(UserDb userDb, UserService userService, User user, String username, String email, String password, String role) {
        Optional<User> existingUser = userDb.findByUsername(username);
        if (existingUser.isEmpty()) {
            Date now = new Date();

            user.setName(username);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(userService.hashPassword(password));
            user.setGender(false);
            user.setPhone("08123456789");
            user.setHomePhone("021567890");
            user.setBusinessPhone("021123456");
            user.setWhatsappNumber("08123456789");
            user.setEntryDate(now);
            user.setKtpNumber("1234567890123456");
            user.setNotes("User dummy untuk testing");
            user.setRole(role);
            user.setCreatedDate(now);
            user.setUpdatedAt(now);

            user.setBirthDate(now);
            user.setEmployeeStatus(true);

            String id = generateCustomUserId(user);
            user.setId(id);

            userDb.save(user);
            System.out.println("User " + username + " berhasil ditambahkan.");
        } else {
            System.out.println("User " + username + " sudah ada, tidak ditambahkan.");
        }
    }

    private String generateCustomUserId(User user) {
        // Ambil singkatan role
        String roleCode = switch (user.getRole().toLowerCase()) {
            case "admin" -> "ADM";
            case "direktur" -> "DIR";
            case "sales" -> "SAL";
            case "general_manager" -> "GM";
            case "kepala_gudang" -> "KG";
            case "komisaris" -> "KOM";
            default -> "UNK";
        };

        // Format tanggal entry
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(user.getEntryDate());

        // Ambil 3 huruf terakhir username
        String username = user.getUsername();
        String suffix = username.length() >= 3 ? username.substring(username.length() - 3) : username;

        return roleCode + "-" + datePart + "-" + suffix.toUpperCase();
    }

    private Gudang createGudangIfNotExists(String nama, String alamat, KepalaGudang kepalaGudang, GudangDb gudangDb) {
        Optional<Gudang> existing = gudangDb.findByNama(nama);
        if (existing.isPresent()) return existing.get();

        AlamatGudang alamatGudang = new AlamatGudang();
        alamatGudang.setAlamat(alamat);
        alamatGudang.setKota("Jakarta");
        alamatGudang.setProvinsi("DKI Jakarta");
        alamatGudang.setKodePos("12345");

        Gudang gudang = new Gudang();
        gudang.setNama(nama);
        gudang.setDeskripsi(nama + " Deskripsi");
        gudang.setKapasitas(1000000);
        gudang.setKepalaGudang(kepalaGudang);
        gudang.setAlamatGudang(alamatGudang);
        gudang.setCreatedDate(new Date());
        gudang.setUpdatedDate(new Date());
        alamatGudang.setGudang(gudang);

        return gudangDb.save(gudang);
    }

    private void createBarangDummyIfNotExists(BarangDb barangDb, Gudang gudang, Gudang gudang2) {
        for (int i = 0; i < 50; i++) {
            String namaBarang = "Barang " + i;
            if (barangDb.findByNama(namaBarang).isPresent()) continue;

            String id = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .replace("-01-", "-I-").replace("-02-", "-II-").replace("-03-", "-III-")
                .replace("-04-", "-IV-").replace("-05-", "-V-").replace("-06-", "-VI-")
                .replace("-07-", "-VII-").replace("-08-", "-VIII-").replace("-09-", "-IX-")
                .replace("-10-", "-X-").replace("-11-", "-XI-").replace("-12-", "-XII-");
            id += "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            Barang barang = new Barang();
            barang.setId(id);
            barang.setNama(namaBarang);
            barang.setKategori("Kategori " + i);
            barang.setMerk("Merk " + i);
            barang.setActive(true);
            barang.setHargaBeli(BigDecimal.valueOf(10000 + (i * 500)));
            barang.setHargaJual(BigDecimal.valueOf(20000 + (i * 500)));

            StockBarangPerGudang stockBarang = new StockBarangPerGudang();
            stockBarang.setBarang(barang);
            stockBarang.setStock(10 + i);
            stockBarang.setGudang((i % 2 == 0) ? gudang : gudang2);

            List<StockBarangPerGudang> listBarang = new ArrayList<>();
            listBarang.add(stockBarang);
            barang.setListStockBarang(listBarang);

            barangDb.save(barang);
        }
    }

    private void createCustomerIfNotExists(CustomerDb customerDb, String email) {
        Customer existing = customerDb.findByEmail(email);
        if (existing != null) return;

        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("Customer 1");
        customer.setPhone("08123456789");
        customer.setHandphone("08123456789");
        customer.setWhatsapp("08123456789");
        customer.setEmail(email);
        customer.setAddress("Jl. Customer No. 1");
        customer.setRole("VENDOR");
        customerDb.save(customer);
    }
}
