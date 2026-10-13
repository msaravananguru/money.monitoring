package money.monitor.service;

import money.monitor.dto.CategoryManagerRequest;
import money.monitor.dto.CategoryManagerResponse;

public interface CategoryManagerService {

	CategoryManagerResponse addCategory(CategoryManagerRequest request);
}
