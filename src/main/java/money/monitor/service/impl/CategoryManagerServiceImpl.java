package money.monitor.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import money.monitor.dto.CategoryManagerRequest;
import money.monitor.dto.CategoryManagerResponse;
import money.monitor.entity.CategoryManager;
import money.monitor.exception.DuplicateReasonException;
import money.monitor.repository.CategoryManagerRepository;
import money.monitor.service.CategoryManagerService;

@Service
public class CategoryManagerServiceImpl implements CategoryManagerService {

	private final CategoryManagerRepository categoryManagerRepository;

	public CategoryManagerServiceImpl(CategoryManagerRepository categoryManagerRepository) {
		this.categoryManagerRepository = categoryManagerRepository;
	}

	@Override
	@Transactional
	public CategoryManagerResponse addCategory(CategoryManagerRequest request) {
		Long userId = request.getUserId();
		String category = request.getCategory().trim();
		String reason = request.getReason().trim();

		// Same reason cannot be saved more than once for the same user
		if (categoryManagerRepository.existsByUserIdAndReasonIgnoreCase(userId, reason)) {
			throw new DuplicateReasonException(
					"Reason '" + reason + "' already exists for userId " + userId);
		}

		CategoryManager saved = categoryManagerRepository.save(new CategoryManager(userId, category, reason));

		return new CategoryManagerResponse(saved.getSerialNo(), saved.getUserId(), saved.getCategory(),
				saved.getReason());
	}
}
