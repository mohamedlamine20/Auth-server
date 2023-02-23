package com.personel.auth.server.payload.response;

import com.personel.auth.server.modeles.ErrorModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String status;
    private String error_type;
    private String message;
    private List<ErrorModel> errors;
}
