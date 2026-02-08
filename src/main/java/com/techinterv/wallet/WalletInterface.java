package com.techinterv.wallet;

import java.util.List;

public interface WalletInterface {

    Account createAccount(String userId);
    Account getAccount(String accountId);
    public Long getBalance(String accountId);
    List<Account> listAccountsByUser(String userId);
    void deposit(String accountId, long amount); // amount est le deposit ajouter au compte
    void withdraw(String accoundId, long amount);
    void transfer (String idFrom, String idTo, long amount);
}
