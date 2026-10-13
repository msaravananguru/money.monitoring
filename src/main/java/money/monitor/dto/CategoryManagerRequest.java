package money.monitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CategoryManagerRequest {

	@NotNull(message = "userId is required")
	@Positive(message = "userId must be a positive number")
	private Long userId;

	@NotBlank(message = "category is required")
	@Size(max = 100, message = "category must not exceed 100 characters")
	private String category;

	@NotBlank(message = "reason is required")
	@Size(max = 255, message = "reason must not exceed 255 characters")
	private String reason;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
