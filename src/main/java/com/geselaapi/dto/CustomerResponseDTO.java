package com.geselaapi.dto;

import com.geselaapi.model.AccountStatus;
import com.geselaapi.model.Customer;

import java.util.UUID;

public class CustomerResponseDTO {
    private UUID uuid;
    private AccountStatus accountStatus;
    private UserResponseDTO userAccount;

    public static CustomerResponseDTO from(Customer customer) {
        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setUuid(customer.getUuid());
        responseDTO.setUserAccount(UserResponseDTO.from(customer.getUserAccount()));
        return responseDTO;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public UserResponseDTO getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserResponseDTO userAccount) {
        this.userAccount = userAccount;
    }
}
