package money.monitor.controller;

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

	private final CategoryManagerService categoryManagerService;

	public CategoryManagerController(CategoryManagerService categoryManagerService) {
		this.categoryManagerService = categoryManagerService;
	}

	@PostMapping("/add")
	public ResponseEntity<ApiResponse<CategoryManagerResponse>> addCategory(
			@Valid @RequestBody CategoryManagerRequest request) {
		CategoryManagerResponse response = categoryManagerService.addCategory(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Category added successfully", response));
	}
}
