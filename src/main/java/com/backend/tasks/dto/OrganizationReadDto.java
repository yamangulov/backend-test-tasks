package com.backend.tasks.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class OrganizationReadDto {
    @ApiModelProperty(value = "Organization ID")
    @Valid
    @JsonProperty("id")
    private Long id;

    @ApiModelProperty(value = "name", required = true)
    @Size(min = 2, max = 250)
    @JsonProperty("name")
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "users")
    private Set<UserCreateDto> users;
}
