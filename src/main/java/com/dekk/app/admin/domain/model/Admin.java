package com.dekk.app.admin.domain.model;

import com.dekk.app.admin.domain.exception.AdminBusinessException;
import com.dekk.app.admin.domain.exception.AdminErrorCode;
import com.dekk.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(
        name = "admins",
        uniqueConstraints = {@UniqueConstraint(name = "uk_admin_email", columnNames = "email")})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE admins SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Filter(name = "deletedFilter")
public class Admin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminRole adminRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminStatus status;

    @Column(nullable = false, length = 100)
    private String department;

    private Admin(String email, String password, AdminRole adminRole, String department) {
        this.email = email;
        this.password = password;
        this.adminRole = adminRole;
        this.status = AdminStatus.ACTIVE;
        this.department = department;
    }

    public static Admin create(String email, String encodedPassword, AdminRole adminRole, String department) {
        validate(email, encodedPassword, adminRole);
        if (department == null || department.isBlank()) {
            throw new AdminBusinessException(AdminErrorCode.INVALID_ADMIN_DATA);
        }
        return new Admin(email, encodedPassword, adminRole, department);
    }

    public void suspend() {
        this.status = AdminStatus.SUSPENDED;
    }

    private static void validate(String email, String encodedPassword, AdminRole adminRole) {
        if (email == null
                || email.isBlank()
                || encodedPassword == null
                || encodedPassword.isBlank()
                || adminRole == null) {
            throw new AdminBusinessException(AdminErrorCode.INVALID_ADMIN_DATA);
        }
    }
}
