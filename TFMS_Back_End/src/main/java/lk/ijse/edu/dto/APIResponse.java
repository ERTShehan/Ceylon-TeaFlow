package lk.ijse.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIResponse {
    private int code;
    private String status;
    private Object data;
}
