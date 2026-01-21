package org.roni.ronitrouble.dto.userCredential.req;

import lombok.Data;

@Data
public class LoginReq {

    private String email;
    private String password;
    private boolean keepSignedInFor7Days;

}
