package vn.nhom14.qlns.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    public record ApiError(int status, String message, Map<String, String> errors) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ApiError(400, "Dữ liệu không hợp lệ", errors));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> malformed(Exception ex) {
        return ResponseEntity.badRequest().body(new ApiError(400, "JSON hoặc tham số không hợp lệ", Map.of()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> status(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(
                new ApiError(ex.getStatusCode().value(), ex.getReason(), Map.of()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> conflict(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(409,
                "Dữ liệu bị trùng hoặc bản ghi đang được sử dụng. Kiểm tra mã sách, ISBN, tên danh mục và tên NXB.", Map.of()));
    }
    @ExceptionHandler(org.springframework.orm.jpa.JpaSystemException.class)
    public ResponseEntity<ApiError> sqliteConstraint(org.springframework.orm.jpa.JpaSystemException ex) {
        for(Throwable cause=ex;cause!=null;cause=cause.getCause()) {
            if(cause instanceof org.sqlite.SQLiteException se && (se.getResultCode().code & 255)==19)
                return ResponseEntity.status(409).body(new ApiError(409,"Dữ liệu bị trùng hoặc bản ghi đang được sử dụng.",Map.of()));
        }
        return ResponseEntity.internalServerError().body(new ApiError(500,"Không thể xử lý dữ liệu. Vui lòng thử lại.",Map.of()));
    }
}
