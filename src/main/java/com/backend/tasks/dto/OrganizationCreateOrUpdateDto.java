package com.backend.tasks.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class OrganizationCreateOrUpdateDto {
    @ApiModelProperty(value = "name", required = true)
    @Size(min = 2, max = 250)
    @JsonProperty("name")
    @NotEmpty
    private String name;

}
