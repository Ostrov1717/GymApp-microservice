package com.example.trainer_work_accounting_service.dto;

public record AuthResponse(
        String token,
        String refreshtoken) {
}
