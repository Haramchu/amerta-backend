package propensi.amesta.service.Aset;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import propensi.amesta.model.EndUser.KepalaGudang;
import propensi.amesta.payload.request.AlamatGudangRequestDTO;
import propensi.amesta.payload.request.GudangRequestDTO;
import propensi.amesta.payload.response.AlamatGudangResponseDTO;
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
        gudangRequestDTO.setAlamatGudang(alamatGudangRequestDTO);
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
        gudangRequestDTO.setAlamatGudang(alamatGudangRequestDTO);
        gudangRequestDTO.setKepalaGudangId(UUID.randomUUID());  // Invalid ID

        when(kepalaGudangDb.findById(any(UUID.class))).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gudangService.addGudang(gudangRequestDTO, alamatGudangRequestDTO);
        });

        assertEquals("Kepala Gudang tidak ditemukan", exception.getMessage());
    }

    @Test
    void testAddGudang_KepalaGudangNull() {
        AlamatGudangRequestDTO alamatGudangRequestDTO = new AlamatGudangRequestDTO();
        alamatGudangRequestDTO.setAlamat("Alamat Gudang 1");
        alamatGudangRequestDTO.setKota("Kota 1");
        alamatGudangRequestDTO.setProvinsi("Provinsi 1");
        alamatGudangRequestDTO.setKodePos("12345");

        GudangRequestDTO gudangRequestDTO = new GudangRequestDTO();
        gudangRequestDTO.setNama("Gudang 1");
        gudangRequestDTO.setDeskripsi("Deskripsi Gudang 1");
        gudangRequestDTO.setKapasitas(100);
        gudangRequestDTO.setAlamatGudang(alamatGudangRequestDTO);
        gudangRequestDTO.setKepalaGudangId(null);  // KepalaGudangId null

        // Skip the KepalaGudang lookup
        when(gudangDb.save(any(Gudang.class))).thenReturn(gudang);

        GudangResponseDTO result = gudangService.addGudang(gudangRequestDTO, alamatGudangRequestDTO);

        assertNotNull(result);
        assertEquals("Gudang 1", result.getNama());
        assertNull(result.getKepalaGudang());
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
        gudangRequestDTO.setAlamatGudang(alamatGudangRequestDTO);
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

        KepalaGudangResponseDTO result = gudangService.kepalaGudangToKepalaGudangResponseDTO(kepalaGudang);

        assertNotNull(result);
        assertEquals(kepalaGudang.getId(), result.getId());
        assertEquals(kepalaGudang.getName(), result.getName());
        assertEquals(kepalaGudang.getUsername(), result.getUsername());
        assertEquals(kepalaGudang.getEmail(), result.getEmail());
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
    void testGudangToGudangResponseDTO_KepalaGudangNull_AlamatGudangNull() {
        gudang = new Gudang();
        gudang.setNama("Gudang 1");
        gudang.setDeskripsi("Deskripsi Gudang 1");
        gudang.setKapasitas(100);
        gudang.setCreatedDate(new Date());
        gudang.setUpdatedDate(new Date());
        gudang.setKepalaGudang(null);
        gudang.setAlamatGudang(null);

        GudangResponseDTO result = gudangService.gudangToGudangResponseDTO(gudang);

        assertNotNull(result);
        assertEquals("Gudang 1", result.getNama());
        assertNull(result.getKepalaGudang());
        assertNull(result.getAlamatGudang());
    }
}