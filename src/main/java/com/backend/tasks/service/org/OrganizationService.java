package com.backend.tasks.service.org;

import com.backend.tasks.model.Organization;

import java.util.List;

public interface OrganizationService {
    Organization create(Organization organization);

    Organization findById(Long id);

    Organization update(Organization organization);

    void delete(Long orgId);

    List<Organization> findAll();
}
