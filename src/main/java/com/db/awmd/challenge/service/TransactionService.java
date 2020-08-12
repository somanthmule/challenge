package com.db.awmd.challenge.service;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.UserTransaction;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;

@Service
public class TransactionService {
	@Getter
	private final AccountsRepository accountsRepository;

	@Autowired
	public TransactionService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public Boolean transferAccountBalance(@Valid UserTransaction userTransaction) throws Exception {
		return this.accountsRepository.transferAccountBalance(userTransaction);
	}

}
