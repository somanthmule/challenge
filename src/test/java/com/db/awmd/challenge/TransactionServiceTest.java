package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.UserTransaction;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransactionService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {

  @Autowired
  private AccountsService accountsService;
  
  @Autowired
  private TransactionService transactionService;
    
  @Test
  public void transfer_success() throws Exception {
	   Account account = new Account("Id-2001", new BigDecimal("123.45"));
		this.accountsService.createAccount(account);
		Account account1 = new Account("Id-2002", new BigDecimal("200.25"));
		this.accountsService.createAccount(account1);
		UserTransaction userTransaction=new UserTransaction(new BigDecimal("100"),"Id-2001","Id-2002");
    	assertEquals(Boolean.TRUE, this.transactionService.transferAccountBalance(userTransaction));
     
  }
  
  @Test
  public void transfer_fails() throws Exception {
	   Account account = new Account("Id-2001", new BigDecimal("123.45"));
		this.accountsService.createAccount(account);
		Account account1 = new Account("Id-2002", new BigDecimal("200.25"));
		this.accountsService.createAccount(account1);
		UserTransaction userTransaction=new UserTransaction(new BigDecimal("200.25"),"Id-2001","Id-2002");
    try {
      this.transactionService.transferAccountBalance(userTransaction);
      fail("Should have failed when source account is not sufficient.");
    } catch (Exception ex) {
      assertThat(ex.getMessage()).isEqualTo("Not enough Fund from source Account ");
    }
  }
  
  @Test
  public void transfer_fails_for_negative_amount() throws Exception {
	  Account account = new Account("Id-101", new BigDecimal("123.45"));
		this.accountsService.createAccount(account);
		Account account1 = new Account("Id-102", new BigDecimal("200.25"));
		this.accountsService.createAccount(account1);
		UserTransaction userTransaction=new UserTransaction(new BigDecimal("-53"),"Id-101","Id-102");
    try {
      this.transactionService.transferAccountBalance(userTransaction);
      fail("Should have failed when source account is not sufficient.");
    } catch (Exception ex) {
      assertThat(ex.getMessage()).isEqualTo("Transfer amount Should be positive.");
    }
  }
 
  @Test
  public void transfer_fails_for_zero_amount() throws Exception {
	  Account account = new Account("Id-101", new BigDecimal("123.45"));
		this.accountsService.createAccount(account);
		Account account1 = new Account("Id-102", new BigDecimal("200.25"));
		this.accountsService.createAccount(account1);
		UserTransaction userTransaction=new UserTransaction(new BigDecimal("0"),"Id-101","Id-102");
    try {
      this.transactionService.transferAccountBalance(userTransaction);
      fail("Should have failed when source account is not sufficient.");
    } catch (Exception ex) {
      assertThat(ex.getMessage()).isEqualTo("Transfer amount Should be positive.");
    }
  }
}
