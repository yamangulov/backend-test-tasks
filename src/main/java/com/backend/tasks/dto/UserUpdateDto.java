package com.backend.tasks.dto;

import com.backend.tasks.validator.NonWhitespacesField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UserUpdateDto {

    @ApiModelProperty(required = true)
    @Size(min = 2, max = 250)
    @JsonProperty("username")
    @NonWhitespacesField
    private String username;

    @ApiModelProperty(required = true)
    @Size(min = 2, max = 250)
    @JsonProperty("password")
    private String password;
}
