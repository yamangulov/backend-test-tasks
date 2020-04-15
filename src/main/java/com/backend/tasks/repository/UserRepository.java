package com.backend.tasks.repository;

import com.backend.tasks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByUsernameAndOrganizationId(String username, Long orgId);

    User findByIdAndOrganizationId(Long userId, Long orgId);

    List<User> findByOrganizationId(Long orgId);
}
