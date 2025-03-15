package propensi.amesta.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import propensi.amesta.exceptions.GlobalExceptionHandler;
import propensi.amesta.payload.request.GudangRequestDTO;
import propensi.amesta.payload.response.AlamatGudangResponseDTO;
import propensi.amesta.payload.response.GudangResponseDTO;
import propensi.amesta.payload.response.KepalaGudangResponseDTO;
import propensi.amesta.service.Aset.GudangService;

@ExtendWith(MockitoExtension.class)
class GudangControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GudangService gudangService;

    @InjectMocks
    private GudangController gudangController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private GudangRequestDTO gudangRequestDTO;
    private GudangResponseDTO gudangResponseDTO;
    private List<GudangResponseDTO> gudangList;

    @BeforeEach
    void setup() {
        // Setup MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(gudangController).build();
        
        // Setup test data
        UUID kepalaGudangId = UUID.randomUUID();
        UUID alamatGudangId = UUID.randomUUID();
  
        gudangRequestDTO = new GudangRequestDTO();
        gudangRequestDTO.setNama("Gudang Test");
        gudangRequestDTO.setDeskripsi("Deskripsi Gudang Test");
        gudangRequestDTO.setKapasitas(100);
        gudangRequestDTO.setKepalaGudangId(kepalaGudangId);
        gudangRequestDTO.setAlamat("Jalan Test No. 123");
        gudangRequestDTO.setKota("Jakarta");
        gudangRequestDTO.setProvinsi("DKI Jakarta");
        gudangRequestDTO.setKodePos("12345");

        KepalaGudangResponseDTO kepalaGudangResponseDTO = new KepalaGudangResponseDTO();
        kepalaGudangResponseDTO.setId(kepalaGudangId);
        kepalaGudangResponseDTO.setName("Kepala Gudang Test");
        kepalaGudangResponseDTO.setUsername("kepala_gudang_test");
        kepalaGudangResponseDTO.setEmail("kg@example.com");

        AlamatGudangResponseDTO alamatGudangResponseDTO = new AlamatGudangResponseDTO();
        alamatGudangResponseDTO.setId(alamatGudangId);
        alamatGudangResponseDTO.setAlamat("Jalan Test No. 123");
        alamatGudangResponseDTO.setKota("Jakarta");
        alamatGudangResponseDTO.setProvinsi("DKI Jakarta");
        alamatGudangResponseDTO.setKodePos("12345");
        
        gudangResponseDTO = new GudangResponseDTO();
        gudangResponseDTO.setNama("Gudang Test");
        gudangResponseDTO.setDeskripsi("Deskripsi Gudang Test");
        gudangResponseDTO.setKapasitas(100);
        gudangResponseDTO.setKepalaGudang(kepalaGudangResponseDTO);
        gudangResponseDTO.setAlamatGudang(alamatGudangResponseDTO);
        gudangResponseDTO.setCreatedDate(new Date());
        gudangResponseDTO.setUpdatedDate(new Date());

        gudangList = Arrays.asList(gudangResponseDTO);
    }

    @Test
    void testCreateGudang() throws Exception {
        when(gudangService.addGudang(any(GudangRequestDTO.class))).thenReturn(gudangResponseDTO);

        mockMvc.perform(post("/api/gudang/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gudangRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Gudang berhasil dibuat!"))
                .andExpect(jsonPath("$.data.nama").value("Gudang Test"));

        verify(gudangService, times(1)).addGudang(any(GudangRequestDTO.class));
    }

    @Test
    void testGetAllGudang() throws Exception {
        when(gudangService.getAllGudang()).thenReturn(gudangList);

        mockMvc.perform(get("/api/gudang/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Daftar gudang berhasil diambil!"))
                .andExpect(jsonPath("$.data[0].nama").value("Gudang Test"));

        verify(gudangService, times(1)).getAllGudang();
    }

    @Test
    void testGetGudangByName() throws Exception {
        String gudangName = "Gudang Test";
        when(gudangService.getGudangByName(gudangName)).thenReturn(gudangResponseDTO);

        mockMvc.perform(get("/api/gudang/{namaGudang}", gudangName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Gudang berhasil ditemukan!"))
                .andExpect(jsonPath("$.data.nama").value("Gudang Test"));

        verify(gudangService, times(1)).getGudangByName(gudangName);
    }

    @Test
    void testUpdateGudang() throws Exception {
        String gudangName = "Gudang Test";
        when(gudangService.updateGudang(eq(gudangName), any(GudangRequestDTO.class))).thenReturn(gudangResponseDTO);

        mockMvc.perform(put("/api/gudang/update/{namaGudang}", gudangName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gudangRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Gudang berhasil diperbarui!"))
                .andExpect(jsonPath("$.data.nama").value("Gudang Test"));

        verify(gudangService, times(1)).updateGudang(eq(gudangName), any(GudangRequestDTO.class));
    }

    @Test
    void testCreateGudangValidationFailure() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(gudangController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
                
        GudangRequestDTO invalidRequest = new GudangRequestDTO();

        mockMvc.perform(post("/api/gudang/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(gudangService, times(0)).addGudang(any(GudangRequestDTO.class));
    }

    @Test
    void testUpdateGudangValidationFailure() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(gudangController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
                
        String gudangName = "Gudang Test";
        GudangRequestDTO invalidRequest = new GudangRequestDTO();

        mockMvc.perform(put("/api/gudang/update/{namaGudang}", gudangName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(gudangService, times(0)).updateGudang(eq(gudangName), any(GudangRequestDTO.class));
    }
}