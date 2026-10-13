package money.monitor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import money.monitor.dto.ApiResponse;
import money.monitor.dto.CategoryManagerRequest;
import money.monitor.dto.CategoryManagerResponse;
import money.monitor.service.CategoryManagerService;

@RestController
@RequestMapping("/api/category-manager")
public class CategoryManagerController {

	private static final Logger log = LoggerFactory.getLogger(CategoryManagerController.class);

	private final CategoryManagerService categoryManagerService;

	public CategoryManagerController(CategoryManagerService categoryManagerService) {
		this.categoryManagerService = categoryManagerService;
	}

	@PostMapping("/add")
	public ResponseEntity<ApiResponse<CategoryManagerResponse>> addCategory(
			@Valid @RequestBody CategoryManagerRequest request) {
		log.info("STEP 1 [Controller] Request received: POST /api/category-manager/add | userId={}, category={}, reason={}",
				request.getUserId(), request.getCategory(), request.getReason());
		log.info("STEP 2 [Controller] Request validation passed. Calling service");

		CategoryManagerResponse response = categoryManagerService.addCategory(request);

		log.info("STEP 9 [Controller] Service returned serialNo={}. Sending 201 CREATED response", response.getSerialNo());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Category added successfully", response));
	}
}
