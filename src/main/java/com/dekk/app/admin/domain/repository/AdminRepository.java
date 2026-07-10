package com.dekk.app.admin.domain.repository;

import com.dekk.app.admin.domain.model.Admin;
import com.dekk.app.admin.domain.model.AdminRole;
import com.dekk.app.admin.domain.model.AdminStatus;
import java.util.Optional;

public interface AdminRepository {
    Admin save(Admin admin);

    Admin saveAndFlush(Admin admin);

    Optional<Admin> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Admin> findById(Long id);

    long countByAdminRoleAndStatus(AdminRole adminRole, AdminStatus status);
}
