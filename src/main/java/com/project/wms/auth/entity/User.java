package com.project.wms.auth.entity;

import com.project.wms.supplier.domain.Supplier;
import com.project.wms.warehouse.domain.Warehouse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id") // nullable — a brand new user may have no role yet
    private Role role;

    // Nullable — only meaningful for WAREHOUSE_MANAGER / WAREHOUSE_STAFF.
    // ADMIN, SUPPLIER, BUSINESS_CUSTOMER leave this null (global/external scope).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private boolean active = true;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;
}