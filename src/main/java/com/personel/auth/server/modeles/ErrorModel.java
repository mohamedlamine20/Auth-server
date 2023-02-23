package com.personel.auth.server.modeles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorModel {
    private String fieldName ="Unknown error";
    private Object rejectedValue ="Unknown error";
    private List<String> messageError = Collections.singletonList("Unknown error");
}
