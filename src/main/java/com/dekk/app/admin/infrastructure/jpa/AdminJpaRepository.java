package com.dekk.app.admin.infrastructure.jpa;

import com.dekk.app.admin.domain.model.Admin;
import com.dekk.app.admin.domain.model.AdminRole;
import com.dekk.app.admin.domain.model.AdminStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminJpaRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByAdminRoleAndStatus(AdminRole adminRole, AdminStatus status);
}
