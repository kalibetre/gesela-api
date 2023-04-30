package com.geselaapi.model;
import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer extends BaseModel {
	private AccountStatus accountStatus;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
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
		this.userAccount.setRole(UserRole.DEFAULT);
	}
}
