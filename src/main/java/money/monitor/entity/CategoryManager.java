package money.monitor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "category_manager",
		uniqueConstraints = @UniqueConstraint(name = "UK_USER_ID_REASON", columnNames = { "USER_ID", "REASON" }))
public class CategoryManager {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "S_NO")
	private Long serialNo;

	@Column(name = "USER_ID", nullable = false)
	private Long userId;

	@Column(name = "CATEGORY", nullable = false, length = 100)
	private String category;

	@Column(name = "REASON", nullable = false, length = 255)
	private String reason;

	public CategoryManager() {
	}

	public CategoryManager(Long userId, String category, String reason) {
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
