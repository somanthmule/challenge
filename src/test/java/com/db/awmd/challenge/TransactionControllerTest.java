package com.db.awmd.challenge;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransactionService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransactionControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

		// Reset the existing accounts before each test.
		accountsService.getAccountsRepository().clearAccounts();
	}

	@Test
	public void testTransactionEnoughFund() throws Exception {
		Account account = new Account("Id-101", new BigDecimal("123.45"));
		this.accountsService.createAccount(account);
		Account account1 = new Account("Id-102", new BigDecimal("200.25"));
		this.accountsService.createAccount(account1);
		this.mockMvc
				.perform(post("/v1/transaction").contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\":\"100\",\"fromAccountId\":\"Id-101\",\"toAccountId\":\"Id-102\"}"))
				.andExpect(status().isOk());

	}

	@Test
	public void testTransactionNotEnoughFund() throws Exception {
		Account account = new Account("Id-101", new BigDecimal("10.45"));
		this.accountsService.createAccount(account);
		Account account1 = new Account("Id-102", new BigDecimal("200.25"));
		this.accountsService.createAccount(account1);
		this.mockMvc
				.perform(post("/v1/transaction").contentType(MediaType.APPLICATION_JSON)
						.content("{\"amount\":\"100\",\"fromAccountId\":\"Id-101\",\"toAccountId\":\"Id-102\"}"))
				.andExpect(status().isBadRequest());

	}
}
