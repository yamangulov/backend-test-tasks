package com.backend.tasks.utils;

import com.backend.tasks.dto.OrganizationCreateOrUpdateDto;
import com.backend.tasks.dto.UserCreateDto;
import com.backend.tasks.dto.UserUpdateDto;
import com.backend.tasks.model.Organization;
import com.backend.tasks.model.User;

public class TestUtils {
    public static UserCreateDto createUserCreateDto(String username, String password) {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(username);
        userCreateDto.setPassword(password);
        return userCreateDto;
    }

    public static Organization createOrganization(Long id, String name) {
        Organization organizationA = new Organization();
        organizationA.setId(id);
        organizationA.setName(name);
        return organizationA;
    }

    public static User createUser(Long id, String username, String password) {
        User userA = new User();
        userA.setId(id);
        userA.setUsername(username);
        userA.setPassword(password);
        return userA;
    }


    public static UserUpdateDto createUserUpdateDto(String username, String password) {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUsername(username);
        userUpdateDto.setPassword(password);
        return userUpdateDto;
    }

    public static OrganizationCreateOrUpdateDto createOrganizationCreateOrUpdateDto(String organizationName) {
        OrganizationCreateOrUpdateDto organizationADto = new OrganizationCreateOrUpdateDto();
        organizationADto.setName(organizationName);
        return organizationADto;
    }

}
