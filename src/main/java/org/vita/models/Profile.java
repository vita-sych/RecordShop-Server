package org.vita.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class Profile {
    private int userId;
    private String firstName = "";
    private String lastName = "";
    private String phone = "";
    private String email = "";
    private String address = "";
    private String city = "";
    private String state = "";
    private String zip = "";

    public Profile() {}
}
