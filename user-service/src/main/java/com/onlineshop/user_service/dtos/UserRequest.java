package com.onlineshop.user_service.dtos;

import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String email;
    private String password;

}
