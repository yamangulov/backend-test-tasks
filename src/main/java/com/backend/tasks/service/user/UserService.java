package com.backend.tasks.service.user;

import com.backend.tasks.model.User;

import java.util.List;

public interface UserService {

    User create(User user, Long orgId);

    User find(Long userId, Long orgId);

    User update(User user, Long orgId);

    void delete(Long userId, Long orgId);

    List<User> findAllByOrgId(Long orgId);
}
