package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.UserTransaction;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.EmailNotificationService;
import com.db.awmd.challenge.service.NotificationService;

import lombok.Getter;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	@Getter
	private final EmailNotificationService emailNotificationService;

	@Autowired
	public AccountsRepositoryInMemory(EmailNotificationService emailNotificationService) {
		this.emailNotificationService = emailNotificationService;
	}

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {
		return accounts.get(accountId);
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	/**
	 * Transfer balance between two accounts.
	 */

	public Boolean transferAccountBalance(@Valid UserTransaction userTransaction) throws Exception {
		// To get Accounts by Id.
		Account fromAccount = accounts.get(userTransaction.getFromAccountId());
		Account toAccount = accounts.get(userTransaction.getToAccountId());
		// check for null
		if (null == fromAccount || null == toAccount) {
			throw new Exception("Fail to lock both accounts for write");
		}

		// Check for negative fund to transfer.
		if (userTransaction.getAmount().compareTo(new BigDecimal(0))<=0) {
			throw new Exception("Transfer amount Should be positive.");
		}
		
		// check enough fund in source account
		BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(userTransaction.getAmount());
		if (fromAccountLeftOver.compareTo(new BigDecimal(0)) < 0) {
			throw new Exception("Not enough Fund from source Account ");
		}

		// proceed with update
		// No need for synchronization because we used ConcurrentHashMap.
		fromAccount.setBalance(fromAccountLeftOver);
		accounts.computeIfPresent(fromAccount.getAccountId(), (key, val) -> fromAccount);
		// To notify source account holder
		emailNotificationService.notifyAboutTransfer(fromAccount,
				userTransaction.getAmount() + " Amount debited from your account.");

		toAccount.setBalance(userTransaction.getAmount());
		// To notify destination account holder.
		emailNotificationService.notifyAboutTransfer(fromAccount,
				userTransaction.getAmount() + " Amount credited to your account.");
		accounts.computeIfPresent(toAccount.getAccountId(), (key, val) -> toAccount);
		return Boolean.TRUE;
	}

}
