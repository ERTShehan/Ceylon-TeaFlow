package lk.ijse.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIResponse <T>{
    private int code;
    private String status;
    private Object data;
}
