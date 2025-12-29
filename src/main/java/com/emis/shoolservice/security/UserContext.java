package com.emis.shoolservice.security;


import com.emis.shoolservice.enums.UserRole;

public record UserContext(
                            String username,
                            String firstName,
                            String lastName,
                            UserRole role,
                            String email,
                            String lga,
                            String state
){

}
