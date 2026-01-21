package org.roni.ronitrouble.dto.userCredential.resp;

import org.roni.ronitrouble.enums.Role;

public record LoginResp(String token, String email, Role role) {

}

