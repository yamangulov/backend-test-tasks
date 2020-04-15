package com.backend.tasks.mapper;

import com.backend.tasks.dto.OrganizationCreateOrUpdateDto;
import com.backend.tasks.dto.OrganizationReadDto;
import com.backend.tasks.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface OrganizationMapper {
    OrganizationMapper INSTANCE = Mappers.getMapper(OrganizationMapper.class);

    OrganizationReadDto organizationToOrganizationReadDto(Organization organization);

    Organization organizationCreateDtoToOrganization(OrganizationCreateOrUpdateDto organizationDto);

    Organization organizationCreateOrUpdateDtoToOrganization(OrganizationCreateOrUpdateDto organizationDto);
}
