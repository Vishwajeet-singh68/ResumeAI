package com.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AuthResponse implements Serializable {
    private Integer id;
    private String token;
    private String fullName;
    private String email;
}
