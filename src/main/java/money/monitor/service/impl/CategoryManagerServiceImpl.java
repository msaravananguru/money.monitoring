package money.monitor.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger log = LoggerFactory.getLogger(CategoryManagerServiceImpl.class);

	private final CategoryManagerRepository categoryManagerRepository;

	public CategoryManagerServiceImpl(CategoryManagerRepository categoryManagerRepository) {
		this.categoryManagerRepository = categoryManagerRepository;
	}

	@Override
	@Transactional
	public CategoryManagerResponse addCategory(CategoryManagerRequest request) {
		log.info("STEP 3 [Service] addCategory started. Trimming input values");
		Long userId = request.getUserId();
		String category = request.getCategory().trim();
		String reason = request.getReason().trim();

		// Same reason cannot be saved more than once for the same user
		log.info("STEP 4 [Service] Checking duplicate in database | userId={}, reason={}", userId, reason);
		boolean exists = categoryManagerRepository.existsByUserIdAndReasonIgnoreCase(userId, reason);

		if (exists) {
			log.warn("STEP 5 [Service] Duplicate found. Reason already exists for userId={}. Stopping, nothing saved", userId);
			throw new DuplicateReasonException(
					"Reason '" + reason + "' already exists for userId " + userId);
		}
		log.info("STEP 5 [Service] No duplicate found. Safe to save");

		log.info("STEP 6 [Service] Saving to table category_manager | userId={}, category={}, reason={}",
				userId, category, reason);
		CategoryManager saved = categoryManagerRepository.save(new CategoryManager(userId, category, reason));
		log.info("STEP 7 [Service] Saved in database with S_NO={}", saved.getSerialNo());

		log.info("STEP 8 [Service] Building response and returning to controller");
		return new CategoryManagerResponse(saved.getSerialNo(), saved.getUserId(), saved.getCategory(),
				saved.getReason());
	}
}
