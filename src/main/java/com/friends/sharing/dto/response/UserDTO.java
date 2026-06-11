package com.friends.sharing.dto.response;

import com.friends.sharing.configuration.security.Authorities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDTO {
    private String name;
    private String email;
    private Authorities authority;
    private String avatarUrl;
}
