package com.personel.auth.server.repository;
import com.personel.auth.server.modeles.ERole;
import com.personel.auth.server.modeles.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}