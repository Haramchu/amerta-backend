package propensi.amesta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Date;

import propensi.amesta.payload.request.DashboardRequestDTO;
import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.DashboardResponseDTO;
import propensi.amesta.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @PostMapping("/get-data")
    public ResponseEntity<?> getDashboardData(
            @Valid @RequestBody DashboardRequestDTO request,
            BindingResult bindingResult) {

        BaseResponseDTO<DashboardResponseDTO> baseResponse = new BaseResponseDTO<>();

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(errorMessages.toString());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            DashboardResponseDTO response = dashboardService.getDynamicDashboardData(request);
            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setMessage("Data dashboard berhasil diambil.");
            baseResponse.setData(response);
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Terjadi kesalahan pada server: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
