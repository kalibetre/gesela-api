package com.geselaapi.dto;

import com.geselaapi.model.AccountStatus;

public class CustomerUpdateDTO {
    private AccountStatus accountStatus;

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
}
