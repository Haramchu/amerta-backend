package propensi.amesta.service.Aset;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import propensi.amesta.model.Aset.AlamatGudang;
import propensi.amesta.model.Aset.Gudang;
import propensi.amesta.model.Aset.StockBarangPerGudang;
import propensi.amesta.model.EndUser.KepalaGudang;
import propensi.amesta.payload.request.AlamatGudangRequestDTO;
import propensi.amesta.payload.request.GudangRequestDTO;
import propensi.amesta.payload.response.AlamatGudangResponseDTO;
import propensi.amesta.payload.response.BarangResponseDTO;
import propensi.amesta.payload.response.GudangResponseDTO;
import propensi.amesta.payload.response.KepalaGudangResponseDTO;
import propensi.amesta.repository.Aset.GudangDb;
import propensi.amesta.repository.EndUser.KepalaGudangDb;

class GudangServiceTest {

    @InjectMocks
    private GudangServiceImpl gudangService;

    @Mock
    private GudangDb gudangDb;

    @Mock
    private KepalaGudangDb kepalaGudangDb;
    
    @Mock
    private BarangServiceImpl barangService;

    private KepalaGudang kepalaGudang;
    private Gudang gudang;
    private AlamatGudang alamatGudang;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup KepalaGudang
        kepalaGudang = new KepalaGudang();
        kepalaGudang.setId(UUID.randomUUID());
        kepalaGudang.setName("Kepala Gudang 1");
        kepalaGudang.setUsername("kepala1");
        kepalaGudang.setEmail("kepala1@example.com");
        kepalaGudang.setBusinessPhone("08123456789");

        // Setup AlamatGudang
        alamatGudang = new AlamatGudang();
        alamatGudang.setId(UUID.randomUUID());
        alamatGudang.setAlamat("Alamat Gudang 1");
        alamatGudang.setKota("Kota 1");
        alamatGudang.setProvinsi("Provinsi 1");
        alamatGudang.setKodePos("12345");

        // Setup Gudang
        gudang = new Gudang();
        gudang.setNama("Gudang 1");
        gudang.setDeskripsi("Deskripsi Gudang 1");
        gudang.setKapasitas(100);
        gudang.setCreatedDate(new Date());
        gudang.setUpdatedDate(new Date());
        gudang.setKepalaGudang(kepalaGudang);
        gudang.setAlamatGudang(alamatGudang);
        gudang.setListBarang(new ArrayList<>());
        
        alamatGudang.setGudang(gudang);
    }

    @Test
    void testAddGudang_Success() {
        AlamatGudangRequestDTO alamatGudangRequestDTO = new AlamatGudangRequestDTO();
        alamatGudangRequestDTO.setAlamat("Alamat Gudang 1");
        alamatGudangRequestDTO.setKota("Kota 1");
        alamatGudangRequestDTO.setProvinsi("Provinsi 1");
        alamatGudangRequestDTO.setKodePos("12345");

        GudangRequestDTO gudangRequestDTO = new GudangRequestDTO();
        gudangRequestDTO.setNama("Gudang 1");
        gudangRequestDTO.setDeskripsi("Deskripsi Gudang 1");
        gudangRequestDTO.setKapasitas(100);
        gudangRequestDTO.setKepalaGudangId(kepalaGudang.getId());

        when(kepalaGudangDb.findById(kepalaGudang.getId())).thenReturn(Optional.of(kepalaGudang));
        when(gudangDb.save(any(Gudang.class))).thenReturn(gudang);

        GudangResponseDTO result = gudangService.addGudang(gudangRequestDTO, alamatGudangRequestDTO);

        assertNotNull(result);
        assertEquals("Gudang 1", result.getNama());
        assertNotNull(result.getKepalaGudang());
        assertNotNull(result.getAlamatGudang());
        verify(gudangDb, times(1)).save(any(Gudang.class));
    }

    @Test
    void testAddGudang_KepalaGudangNotFound() {
        AlamatGudangRequestDTO alamatGudangRequestDTO = new AlamatGudangRequestDTO();
        alamatGudangRequestDTO.setAlamat("Alamat Gudang 1");
        alamatGudangRequestDTO.setKota("Kota 1");
        alamatGudangRequestDTO.setProvinsi("Provinsi 1");
        alamatGudangRequestDTO.setKodePos("12345");

        GudangRequestDTO gudangRequestDTO = new GudangRequestDTO();
        gudangRequestDTO.setNama("Gudang 1");
        gudangRequestDTO.setDeskripsi("Deskripsi Gudang 1");
        gudangRequestDTO.setKapasitas(100);
        gudangRequestDTO.setKepalaGudangId(UUID.randomUUID());  // Invalid ID

        when(kepalaGudangDb.findById(any(UUID.class))).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gudangService.addGudang(gudangRequestDTO, alamatGudangRequestDTO);
        });

        assertEquals("Kepala Gudang tidak ditemukan", exception.getMessage());
    }

    @Test
    void testGetAllGudang() {
        List<Gudang> gudangList = List.of(gudang);
        when(gudangDb.findAll()).thenReturn(gudangList);

        List<GudangResponseDTO> result = gudangService.getAllGudang();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Gudang 1", result.get(0).getNama());
    }

    @Test
    void testFilterGudang_WithKeywords() {
        List<Gudang> gudangList = List.of(gudang);
        String keywords = "Gudang";
        
        when(gudangDb.findByNameOrCityOrProvince(keywords)).thenReturn(gudangList);

        List<GudangResponseDTO> result = gudangService.filterGudang(keywords);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Gudang 1", result.get(0).getNama());
        verify(gudangDb, times(1)).findByNameOrCityOrProvince(keywords);
    }

    @Test
    void testFilterGudang_WithEmptyKeywords() {
        List<Gudang> gudangList = List.of(gudang);
        
        when(gudangDb.findAll()).thenReturn(gudangList);

        List<GudangResponseDTO> result = gudangService.filterGudang("");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Gudang 1", result.get(0).getNama());
        verify(gudangDb, times(1)).findAll();
    }

    @Test
    void testFilterGudang_WithNullKeywords() {
        List<Gudang> gudangList = List.of(gudang);
        
        when(gudangDb.findAll()).thenReturn(gudangList);

        List<GudangResponseDTO> result = gudangService.filterGudang(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Gudang 1", result.get(0).getNama());
        verify(gudangDb, times(1)).findAll();
    }

    @Test
    void testGetGudangByName_Success() {
        when(gudangDb.findById("Gudang 1")).thenReturn(Optional.of(gudang));

        GudangResponseDTO result = gudangService.getGudangByName("Gudang 1");

        assertNotNull(result);
        assertEquals("Gudang 1", result.getNama());
    }

    @Test
    void testGetGudangByName_NotFound() {
        when(gudangDb.findById("Gudang 1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gudangService.getGudangByName("Gudang 1");
        });

        assertEquals("Gudang tidak ditemukan", exception.getMessage());
    }

    @Test
    void testUpdateGudang_Success() {
        AlamatGudangRequestDTO alamatGudangRequestDTO = new AlamatGudangRequestDTO();
        alamatGudangRequestDTO.setAlamat("Alamat Gudang 1");
        alamatGudangRequestDTO.setKota("Kota 1");
        alamatGudangRequestDTO.setProvinsi("Provinsi 1");
        alamatGudangRequestDTO.setKodePos("12345");

        GudangRequestDTO gudangRequestDTO = new GudangRequestDTO();
        gudangRequestDTO.setNama("Gudang Updated");
        gudangRequestDTO.setDeskripsi("Deskripsi Gudang 1");
        gudangRequestDTO.setKapasitas(100);
        gudangRequestDTO.setKepalaGudangId(kepalaGudang.getId());

        when(gudangDb.findById("Gudang 1")).thenReturn(Optional.of(gudang));
        when(kepalaGudangDb.findById(kepalaGudang.getId())).thenReturn(Optional.of(kepalaGudang));
        when(gudangDb.save(any(Gudang.class))).thenReturn(gudang);

        GudangResponseDTO result = gudangService.updateGudang("Gudang 1", gudangRequestDTO, alamatGudangRequestDTO);

        assertNotNull(result);
        assertEquals("Gudang Updated", result.getNama());
        verify(gudangDb, times(1)).save(any(Gudang.class));
    }

    @Test
    void testUpdateGudang_WithNullKepalaGudangId() {
        AlamatGudangRequestDTO alamatGudangRequestDTO = new AlamatGudangRequestDTO();
        alamatGudangRequestDTO.setAlamat("Alamat Gudang Updated");
        alamatGudangRequestDTO.setKota("Kota Updated");
        alamatGudangRequestDTO.setProvinsi("Provinsi Updated");
        alamatGudangRequestDTO.setKodePos("54321");

        GudangRequestDTO gudangRequestDTO = new GudangRequestDTO();
        gudangRequestDTO.setNama("Gudang Updated");
        gudangRequestDTO.setDeskripsi("Deskripsi Updated");
        gudangRequestDTO.setKapasitas(200);
        gudangRequestDTO.setKepalaGudangId(null); // Null KepalaGudangId

        when(gudangDb.findById("Gudang 1")).thenReturn(Optional.of(gudang));
        when(gudangDb.save(any(Gudang.class))).thenReturn(gudang);

        GudangResponseDTO result = gudangService.updateGudang("Gudang 1", gudangRequestDTO, alamatGudangRequestDTO);

        assertNotNull(result);
        assertEquals("Gudang Updated", result.getNama());
        verify(gudangDb, times(1)).save(any(Gudang.class));
        // Should not update kepalaGudang
        verify(kepalaGudangDb, times(0)).findById(any(UUID.class));
    }

    @Test
    void testUpdateGudang_NotFound() {
        AlamatGudangRequestDTO alamatGudangRequestDTO = new AlamatGudangRequestDTO();
        alamatGudangRequestDTO.setAlamat("Alamat Gudang Updated");

        GudangRequestDTO gudangRequestDTO = new GudangRequestDTO();
        gudangRequestDTO.setNama("Gudang Updated");

        when(gudangDb.findById("Gudang 1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gudangService.updateGudang("Gudang 1", gudangRequestDTO, alamatGudangRequestDTO);
        });

        assertEquals("Gudang tidak ditemukan", exception.getMessage());
    }

    @Test
    void testKepalaGudangToKepalaGudangResponseDTO() {
        kepalaGudang = new KepalaGudang();
        kepalaGudang.setId(UUID.randomUUID());
        kepalaGudang.setName("Kepala Gudang 1");
        kepalaGudang.setUsername("kepala1");
        kepalaGudang.setEmail("kepala1@example.com");
        kepalaGudang.setBusinessPhone("08123456789");

        KepalaGudangResponseDTO result = gudangService.kepalaGudangToKepalaGudangResponseDTO(kepalaGudang);

        assertNotNull(result);
        assertEquals(kepalaGudang.getId(), result.getId());
        assertEquals(kepalaGudang.getName(), result.getName());
        assertEquals(kepalaGudang.getEmail(), result.getEmail());
        assertEquals(kepalaGudang.getBusinessPhone(), result.getBusinessPhone());
    }

    @Test
    void testAlamatGudangToAlamatGudangResponseDTO() {
        alamatGudang = new AlamatGudang();
        alamatGudang.setId(UUID.randomUUID());
        alamatGudang.setAlamat("Alamat Gudang 1");
        alamatGudang.setKota("Kota 1");
        alamatGudang.setProvinsi("Provinsi 1");
        alamatGudang.setKodePos("12345");

        AlamatGudangResponseDTO result = gudangService.alamatGudangToAlamatGudangResponseDTO(alamatGudang);

        assertNotNull(result);
        assertEquals(alamatGudang.getId(), result.getId());
        assertEquals(alamatGudang.getAlamat(), result.getAlamat());
        assertEquals(alamatGudang.getKota(), result.getKota());
        assertEquals(alamatGudang.getProvinsi(), result.getProvinsi());
        assertEquals(alamatGudang.getKodePos(), result.getKodePos());
    }

    @Test
    void testGudangToGudangResponseDTO_WithListBarang() {
        // Setup StockBarangPerGudang and mock behavior
        StockBarangPerGudang stockBarang = new StockBarangPerGudang();
        List<StockBarangPerGudang> listBarang = new ArrayList<>();
        listBarang.add(stockBarang);
        gudang.setListBarang(listBarang);
        
        BarangResponseDTO barangResponseDTO = new BarangResponseDTO();
        when(barangService.barangToBarangResponseDTO(any())).thenReturn(barangResponseDTO);

        GudangResponseDTO result = gudangService.gudangToGudangResponseDTO(gudang);

        assertNotNull(result);
        assertEquals("Gudang 1", result.getNama());
        assertNotNull(result.getListBarang());
        assertEquals(1, result.getListBarang().size());
    }
}