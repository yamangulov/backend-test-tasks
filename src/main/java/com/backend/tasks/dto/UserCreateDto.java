package com.backend.tasks.dto;

import com.backend.tasks.validator.NonWhitespacesField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UserCreateDto {
    @ApiModelProperty(required = true)
    @Size(min = 2, max = 250)
    @JsonProperty("username")
    @NotEmpty
    @NonWhitespacesField
    private String username;

    @ApiModelProperty(required = true)
    @Size(min = 2, max = 250)
    @JsonProperty("password")
    @NotEmpty
    private String password;
}
