package org.roni.ronitrouble.dto.userCredential.req;

import lombok.Data;

@Data
public class MerchantRegisterReq {

    private String email;
    private String password;
    private String name;
    private String address;
    private String phoneNumber;

}
