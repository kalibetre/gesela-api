package com.geselaapi.dto;

import com.geselaapi.model.AccountStatus;
import com.geselaapi.model.Customer;

import java.util.UUID;

public class CustomerResponseDTO extends UserResponseDTO {
    private UUID uuid;
    private AccountStatus accountStatus;

    public static CustomerResponseDTO from(Customer customer) {
        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setUuid(customer.getUuid());
        responseDTO.setName(customer.getUserAccount().getName());
        responseDTO.setEmail(customer.getUserAccount().getEmail());
        responseDTO.setPhone(customer.getUserAccount().getPhone());
        responseDTO.setAccountStatus(customer.getAccountStatus());
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

}
