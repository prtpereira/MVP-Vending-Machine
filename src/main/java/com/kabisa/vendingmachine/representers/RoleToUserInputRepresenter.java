package com.kabisa.vendingmachine.representers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoleToUserInputRepresenter {
    private String username;
    @JsonProperty("role_name")
    private String roleName;

    public RoleToUserInputRepresenter(String username, String roleName) {
        this.username = username;
        this.roleName = roleName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
