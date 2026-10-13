package money.monitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import money.monitor.entity.CategoryManager;

@Repository
public interface CategoryManagerRepository extends JpaRepository<CategoryManager, Long> {

	boolean existsByUserIdAndReasonIgnoreCase(Long userId, String reason);
}
