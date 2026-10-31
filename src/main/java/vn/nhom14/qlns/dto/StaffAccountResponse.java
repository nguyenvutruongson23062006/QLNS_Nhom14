package vn.nhom14.qlns.dto;
import java.time.LocalDateTime;
public record StaffAccountResponse(Long id,String username,String email,String role,
                                   boolean enabled,LocalDateTime createdAt) {}
