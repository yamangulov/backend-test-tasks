package com.backend.tasks.controller;

import com.backend.tasks.dto.UserCreateDto;
import com.backend.tasks.dto.UserReadDto;
import com.backend.tasks.dto.UserUpdateDto;
import com.backend.tasks.mapper.UserMapper;
import com.backend.tasks.model.User;
import com.backend.tasks.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * Implement create, read, update, delete  rest controller endpoints for user.
 * Map endpoints to /orgs/{orgId}/users path
 * 1. Post to /orgs/{orgId}/users endpoint should create and return user for organization with orgId=orgId. Response status should be 201.
 * 2. Put to /orgs/{orgId}/users/{userId} endpoint should update, save and return user with orgId=userId for organization with orgId=orgId.
 * 3. Get to /orgs/{orgId}/users/{userId} endpoint should fetch and return user with orgId=userId for organization with orgId=orgId.
 * 4. Delete to /orgs/{orgId}/users/{userId} endpoint should delete user with orgId=userId for organization with orgId=orgId. Response status should be 204.
 * 5. Get to /orgs/{orgId}/users endpoint should return list of all users for organization with orgId=orgId
 */
@Api
@Slf4j
@RestController
@RequestMapping(value = "/orgs/{orgId}/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful operation", response = UserReadDto.class),
            @ApiResponse(code = 400, message = "Validation exception"),
            @ApiResponse(code = 409, message = "User with such username already exist")})
    @PostMapping(produces = {"application/json"})
    public ResponseEntity create(@PathVariable("orgId") Long orgId, @Valid @RequestBody UserCreateDto userDto) {
        User user = UserMapper.INSTANCE.userCreateDtoToUser(userDto);
        user = userService.create(user, orgId);
        UserReadDto result = UserMapper.INSTANCE.userToUserReadDto(user);
        return ResponseEntity.status(CREATED).body(result);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = UserReadDto.class),
            @ApiResponse(code = 400, message = "Validation exception"),
            @ApiResponse(code = 404, message = "User not found")})
    @PutMapping(value = "/{userId}", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<UserReadDto> update(@PathVariable("orgId") Long orgId, @PathVariable("userId") Long userId, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        User user = UserMapper.INSTANCE.userUpdateDtoToUser(userUpdateDto);
        user.setId(userId);
        user = userService.update(user, orgId);
        UserReadDto result = UserMapper.INSTANCE.userToUserReadDto(user);
        return ResponseEntity.ok(result);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = UserReadDto.class),
            @ApiResponse(code = 400, message = "Validation exception"),
            @ApiResponse(code = 404, message = "User not found")})
    @GetMapping(value = "/{userId}", produces = {"application/json"})
    public ResponseEntity<UserReadDto> findById(@PathVariable("orgId") Long orgId, @PathVariable("userId") Long userId) {
        User existingUser = userService.find(userId, orgId);
        UserReadDto result = UserMapper.INSTANCE.userToUserReadDto(existingUser);
        return ResponseEntity.ok(result);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Validation exception"),
            @ApiResponse(code = 404, message = "User not found")})
    @DeleteMapping(value = "/{userId}", produces = {"application/json"})
    public ResponseEntity delete(@PathVariable("orgId") Long orgId, @PathVariable("userId") Long userId) {
        userService.delete(userId, orgId);
        return ResponseEntity.noContent().build();
    }


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = UserReadDto.class,
                    responseContainer = "List")})
    @GetMapping(produces = {"application/json"})
    public ResponseEntity<List<UserReadDto>> findAllByOrgId(@PathVariable("orgId") Long orgId) {
        List<UserReadDto> organizations = userService.findAllByOrgId(orgId).stream()
                .map(UserMapper.INSTANCE::userToUserReadDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organizations);
    }

}
