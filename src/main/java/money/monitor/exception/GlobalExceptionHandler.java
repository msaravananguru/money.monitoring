package money.monitor.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import money.monitor.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new LinkedHashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.putIfAbsent(error.getField(), error.getDefaultMessage());
		}
		log.warn("ERROR [ExceptionHandler] Request validation failed: {}. Sending 400 BAD REQUEST", errors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.failure("Validation failed", errors));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleUnreadable(HttpMessageNotReadableException ex) {
		log.warn("ERROR [ExceptionHandler] Request body is missing or not valid JSON. Sending 400 BAD REQUEST");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.failure("Invalid request body", null));
	}

	@ExceptionHandler(DuplicateReasonException.class)
	public ResponseEntity<ApiResponse<Void>> handleDuplicate(DuplicateReasonException ex) {
		log.warn("ERROR [ExceptionHandler] Duplicate reason: {}. Sending 409 CONFLICT", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.failure(ex.getMessage(), null));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
		log.error("ERROR [ExceptionHandler] Database constraint violated. Sending 409 CONFLICT", ex);
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.failure("Record already exists or violates a database constraint", null));
	}
}
