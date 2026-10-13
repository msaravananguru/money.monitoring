package money.monitor.dto;

public class CategoryManagerResponse {

	private Long serialNo;
	private Long userId;
	private String category;
	private String reason;

	public CategoryManagerResponse() {
	}

	public CategoryManagerResponse(Long serialNo, Long userId, String category, String reason) {
		this.serialNo = serialNo;
		this.userId = userId;
		this.category = category;
		this.reason = reason;
	}

	public Long getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(Long serialNo) {
		this.serialNo = serialNo;
	}

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
