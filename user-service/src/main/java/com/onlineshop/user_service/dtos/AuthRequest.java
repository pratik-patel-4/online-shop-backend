package com.onlineshop.user_service.dtos;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
