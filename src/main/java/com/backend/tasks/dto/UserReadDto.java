package com.backend.tasks.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UserReadDto {
    @ApiModelProperty(value = "User ID")
    @Valid
    @JsonProperty("id")
    private Long id;

    @ApiModelProperty(required = true)
    @Size(min = 2, max = 250)
    @JsonProperty("username")
    @NotEmpty
    private String username;

    @ApiModelProperty(required = true)
    @Size(min = 2, max = 250)
    @JsonProperty("password")
    @NotEmpty
    private String password;

}
