package com.dekk.app.admin.infrastructure;

import com.dekk.app.admin.domain.model.Admin;
import com.dekk.app.admin.domain.model.AdminRole;
import com.dekk.app.admin.domain.model.AdminStatus;
import com.dekk.app.admin.domain.repository.AdminRepository;
import com.dekk.app.admin.infrastructure.jpa.AdminJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminRepository {

    private final AdminJpaRepository adminJpaRepository;

    @Override
    public Admin save(Admin admin) {
        return adminJpaRepository.save(admin);
    }

    @Override
    public Admin saveAndFlush(Admin admin) {
        return adminJpaRepository.saveAndFlush(admin);
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return adminJpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return adminJpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<Admin> findById(Long id) {
        return adminJpaRepository.findById(id);
    }

    @Override
    public long countByAdminRoleAndStatus(AdminRole adminRole, AdminStatus status) {
        return adminJpaRepository.countByAdminRoleAndStatus(adminRole, status);
    }
}
