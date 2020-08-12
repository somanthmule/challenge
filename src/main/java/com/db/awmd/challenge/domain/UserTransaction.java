package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserTransaction {
	
	@NonNull
	@Min(value = 1, message = "To transfer amount must be greater than 1.")
	private BigDecimal amount;
	
	@NonNull
	private String fromAccountId;
	
	@NonNull
	private String toAccountId;

	@JsonCreator
	public UserTransaction(@JsonProperty("account") BigDecimal amount,
			@JsonProperty("fromAccountId") String fromAccountId, @JsonProperty("toAccountId") String toAccountId) {

		this.amount = amount;
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
	}

}
