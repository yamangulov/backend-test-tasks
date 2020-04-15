package com.backend.tasks.service.org.impl;

import com.backend.tasks.exceptions.CustomValidationException;
import com.backend.tasks.exceptions.ObjectAlreadyExistException;
import com.backend.tasks.exceptions.ObjectNotFoundException;
import com.backend.tasks.model.Organization;
import com.backend.tasks.repository.OrganizationRepository;
import com.backend.tasks.service.org.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    @Override
    public Organization create(Organization organization) {
        Organization organizationByName = organizationRepository.findByName(organization.getName());
        if (organizationByName != null) {
            log.error("Organization with such name: {} already exist", organization.getName());
            throw new ObjectAlreadyExistException("User", organization.getName());
        }
        return organizationRepository.save(organization);
    }

    @Transactional
    @Override
    public Organization findById(Long id) {
        Optional<Organization> organizationOpt = organizationRepository.findById(id);
        if (!organizationOpt.isPresent()) {
            ObjectNotFoundException notFoundException = new ObjectNotFoundException("Organization", id);
            log.error(notFoundException.getMessage());
            throw notFoundException;
        }
        return organizationOpt.orElse(null);
    }

    @Transactional
    @Override
    public Organization update(Organization organization) {
        Long organizationId = organization.getId();
        Optional<Organization> organizationOpt = organizationRepository.findById(organizationId);
        if (!organizationOpt.isPresent()) {
            ObjectNotFoundException notFoundException = new ObjectNotFoundException("Organization", organizationId);
            log.error(notFoundException.getMessage());
            throw notFoundException;
        }
        if (!organizationOpt.get().getName().equals(organization.getName())) {
            Organization organizationByName = organizationRepository.findByName(organization.getName());
            if (!Objects.isNull(organizationByName)) {
                throw new CustomValidationException("Organization with  such name already exist:" + organizationByName.getName());
            }
        }

        return organizationRepository.save(organization);
    }

    @Transactional
    @Override
    public void delete(Long orgId) {
        Organization organization = findById(orgId);
        organizationRepository.deleteById(organization.getId());
    }

    @Transactional
    @Override
    public List<Organization> findAll() {
        return organizationRepository.findAll();
    }
}
