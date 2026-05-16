package com.url.shortner.dtos;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    // ✅ Fix: Removed Set<String> role — role is hardcoded as ROLE_USER in AuthController
    // No need to accept role from client (security risk!)
}