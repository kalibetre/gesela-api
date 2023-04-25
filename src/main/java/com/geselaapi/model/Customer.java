package com.geselaapi.model;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends BaseModel {
	private AccountStatus accountStatus;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_account_uuid")
	private User userAccount;

	public Customer() {
		this.accountStatus = AccountStatus.ACTIVE;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public User getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(User userAccount) {
		this.userAccount = userAccount;
	}

}