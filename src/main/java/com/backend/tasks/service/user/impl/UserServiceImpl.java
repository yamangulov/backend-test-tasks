package com.backend.tasks.service.user.impl;

import com.backend.tasks.PersistenceUtils;
import com.backend.tasks.exceptions.CustomValidationException;
import com.backend.tasks.exceptions.ObjectAlreadyExistException;
import com.backend.tasks.exceptions.ObjectNotFoundException;
import com.backend.tasks.model.Organization;
import com.backend.tasks.model.User;
import com.backend.tasks.repository.UserRepository;
import com.backend.tasks.service.org.OrganizationService;
import com.backend.tasks.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final OrganizationService organizationService;

    public UserServiceImpl(UserRepository userRepository, OrganizationService organizationService) {
        this.userRepository = userRepository;
        this.organizationService = organizationService;
    }

    @Transactional
    @Override
    public User create(User user, Long orgId) {
        Organization organization = organizationService.findById(orgId);
        User userByUsername = userRepository.findByUsernameAndOrganizationId(user.getUsername(), orgId);
        if (userByUsername != null) {
            log.error("User with such username: {} already exist in this organization: {}", user.getUsername(), organization.getName());
            throw new ObjectAlreadyExistException("User", user.getUsername());
        }
        user.setOrganization(organization);
        return userRepository.save(user);
    }


    @Transactional
    @Override
    public User find(Long userId, Long orgId) {
        User user = userRepository.findByIdAndOrganizationId(userId, orgId);
        if(user == null) {
            ObjectNotFoundException notFoundException = new ObjectNotFoundException("User", userId);
            log.error(notFoundException.getMessage());
            throw notFoundException;
        }
        return user;
    }

    @Transactional
    @Override
    public User update(User user, Long orgId) {
        Long userId = user.getId();
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            ObjectNotFoundException notFoundException = new ObjectNotFoundException("User", userId);
            log.error(notFoundException.getMessage());
            throw notFoundException;
        }
        User existedUser = userOpt.get();
        if (!existedUser.getUsername().equals(user.getUsername())) {
            User userByName = userRepository.findByUsernameAndOrganizationId(user.getUsername(), orgId);
            if (!Objects.isNull(userByName)) {
                throw new CustomValidationException("User with  such name already exist:" + userByName.getUsername());
            }
        }
        user = (User) PersistenceUtils.partialUpdate(existedUser, user);
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void delete(Long userId, Long orgId) {
        User user = find(userId, orgId);
        userRepository.deleteById(user.getId());
    }

    @Transactional
    @Override
    public List<User> findAllByOrgId(Long orgId) {
        return userRepository.findByOrganizationId(orgId);
    }
}
