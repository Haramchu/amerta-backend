package propensi.amesta.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponseDTO<T> {
    private int status;
    private String message;
    private Date timestamp;
    private T data;
}