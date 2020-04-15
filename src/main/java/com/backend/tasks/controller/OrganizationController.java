package com.backend.tasks.controller;

import com.backend.tasks.dto.OrganizationCreateOrUpdateDto;
import com.backend.tasks.dto.OrganizationReadDto;
import com.backend.tasks.mapper.OrganizationMapper;
import com.backend.tasks.model.Organization;
import com.backend.tasks.service.org.OrganizationService;
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
 * Implement create, read, update, delete  rest controller endpoints for organization.
 * Map endpoints to /orgs path.
 * 1. Post to /orgs endpoint should create and return organization. Response status should be 201.
 * 2. Put to /orgs/{orgId} endpoint should update, save and return organization with orgId=orgId.
 * 3. Get to /orgs/{orgId} endpoint should fetch and return organization with orgId=orgId.
 * 4. Delete to /orgs/{orgId} endpoint should delete organization with orgId=orgId. Response status should be 204.
 * 5. Get to /orgs endpoint should return list of all organizations
 */
@Api
@Slf4j
@RestController
@RequestMapping(value = "/orgs")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful operation", response = OrganizationReadDto.class),
            @ApiResponse(code = 400, message = "Validation exception"),
            @ApiResponse(code = 409, message = "Organization with such name already exist")})
    @PostMapping(produces = {"application/json"})
    public ResponseEntity create(@Valid @RequestBody OrganizationCreateOrUpdateDto organizationDto) {

        Organization organization = OrganizationMapper.INSTANCE.organizationCreateDtoToOrganization(organizationDto);
        organization = organizationService.create(organization);
        OrganizationReadDto result = OrganizationMapper.INSTANCE.organizationToOrganizationReadDto(organization);
        return ResponseEntity.status(CREATED).body(result);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = OrganizationReadDto.class),
            @ApiResponse(code = 400, message = "Validation exception"),
            @ApiResponse(code = 404, message = "Organization not found")})
    @PutMapping(value = "/{orgId}", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<OrganizationReadDto> update(@PathVariable("orgId") Long id, @Valid @RequestBody OrganizationCreateOrUpdateDto organizationDto) {

        Organization organization = OrganizationMapper.INSTANCE.organizationCreateOrUpdateDtoToOrganization(organizationDto);
        organization.setId(id);

        /*Organization existingOrganization = organizationService.findById(id);
        if (isNull(existingOrganization)) {
            return ResponseEntity.notFound().build();
        }*/

        organization = organizationService.update(organization);
        OrganizationReadDto result = OrganizationMapper.INSTANCE.organizationToOrganizationReadDto(organization);
        return ResponseEntity.ok(result);
    }


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = OrganizationReadDto.class),
            @ApiResponse(code = 400, message = "Validation exception"),
            @ApiResponse(code = 404, message = "Organization not found")})
    @GetMapping(value = "/{orgId}", produces = {"application/json"})
    public ResponseEntity<OrganizationReadDto> findById(@PathVariable("orgId") Long orgId) {
        Organization organization = organizationService.findById(orgId);
        OrganizationReadDto organizationDto = OrganizationMapper.INSTANCE.organizationToOrganizationReadDto(organization);
        return ResponseEntity.ok(organizationDto);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successful operation"),
            @ApiResponse(code = 400, message = "Validation exception"),
            @ApiResponse(code = 404, message = "Organization not found")})
    @DeleteMapping(value = "/{orgId}", produces = {"application/json"})
    public ResponseEntity delete(@PathVariable("orgId") Long orgId) {
        organizationService.delete(orgId);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = OrganizationReadDto.class,
                    responseContainer = "List")})
    @GetMapping(produces = {"application/json"})
    public ResponseEntity<List<OrganizationReadDto>> findAll() {
        List<OrganizationReadDto> organizations = organizationService.findAll().stream()
                .map(OrganizationMapper.INSTANCE::organizationToOrganizationReadDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organizations);
    }
}
