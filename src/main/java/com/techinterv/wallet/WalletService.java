package com.techinterv.wallet;

import java.util.*;

public class WalletService implements WalletInterface {
    private final Map<String,Account> accountsById = new HashMap<>(); // retrouver vite un account par un id
    private final Map<String,List<String>> accountsIdsbyUser = new HashMap<>(); // retouver tous les comptes d'un user

    private final Map<String,Long> balanceByAccountId = new HashMap<>(); // ici on va stocker la balac=nce de chaque compte par son id, in le la declare
    //pas en tant que attribut dans la classe account, car balance ets une valeur changeable et modifiable, et account est immuable et non changeable pour des raisons de securite

    // ecrire une methode pour valider d'id entrer du user et ca evite de repeter la validation du user dans chaque methode
    private void validateUser(String userId)
    {
        if(userId == null || userId.isBlank())
            throw new IllegalArgumentException("userId must be not blanck");
    }
    @Override
    public Account createAccount(String userId) {
        validateUser(userId); // on valide le user, nest pas null
        String accountId = UUID.randomUUID().toString(); // on genere un id de account unique
        Account account = new Account(accountId,userId); // on creer l'instance de l'account avec le userid passe en parametre et lid unique genere
        accountsIdsbyUser.computeIfAbsent(userId, k -> new ArrayList<>()).add(accountId);
        accountsById.put(accountId,account);
        // computeIfAbsent veut dire : si la liste des comptes pour ce user n'existe pas encore, cree une nouvelle arrayList,
        // puis ajoute l'ID dedans, car xomputeIfAbsent retourne la value d ela key qui est la list des IDs
        // computeIfAbsent necrase pas la valeur de la cle si cette cle existe deja dans la map, even if we map a new value in the expression
        // mais va quand meme retourner la valeur, dans notre cas ca retourne la liste et on y applique add()

        balanceByAccountId.put(accountId,0L); // ici on initialise la balance du compte cree a 0
        return account;
    }

    @Override
    public Account getAccount(String accountId) {
        if(accountId == null || accountId.isBlank()){
            throw new IllegalArgumentException("account id must not be blank");
        }

        Account account = accountsById.get(accountId);
        if(account == null){
            throw  new NotFoundexception("Account Not found:" +accountId);
        }

        return account;
    }

    public Long getBalance(String accountId){
        getAccount(accountId);
        return balanceByAccountId.get(accountId);
    }

    /*@Override cette mthode sert a lister tous les accounts, ce nest pas logique car un simple user aura acces aux accounts des autres
    public List<Account> listAccounts() {
      List<Account> list = new ArrayList<>();
      for(Account account : accountsById.values())
      { list.add(account);}

        return list;
    }*/

    @Override
    public List<Account> listAccountsByUser(String userId) {
        validateUser(userId);
        List<Account> list = new ArrayList<>();
        List<String> listOfIdsOfAccounts = accountsIdsbyUser.get(userId);
        if(listOfIdsOfAccounts == null) return new ArrayList<>();
        for(String id : listOfIdsOfAccounts)
        {
            Account account = getAccount(id);
            list.add(account);
        }
        return list;
    }

    @Override
    public void deposit(String accountId, long amount) {
        getAccount(accountId);
        if(amount <= 0){
            throw new IllegalArgumentException("amount must be > 0");
        }
        long oldBalance =getBalance(accountId);
        balanceByAccountId.put(accountId,oldBalance + amount);

    }

    @Override
    public void withdraw(String accoundId, long amount) {
        getAccount(accoundId);
        if(amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        long currentBalance = getBalance(accoundId);
        if(amount > currentBalance){
            throw new InsufficientFundsException("Insufficient funds: balance= "
                    +currentBalance + ", required= " + amount);
        }
        balanceByAccountId.put(accoundId,currentBalance - amount);
    }

    protected void credit (String idTo, long amount){
        long old = balanceByAccountId.get(idTo);
        balanceByAccountId.put(idTo, old +amount);
    }


    /*Dans la méthode transfer, l’atomicité est garantie en sauvegardant d’abord l’état initial des soldes avant toute modification.
    Le débit du compte émetteur est effectué en premier, puis le crédit du compte destinataire est tenté à l’intérieur d’un bloc try.
    Si le crédit échoue et qu’une exception est levée, le bloc catch restaure immédiatement le solde initial du compte émetteur (rollback)
    et relance l’exception. Cette structure try/catch permet d’assurer que le transfert s’exécute entièrement ou pas du tout, empêchant
    ainsi toute perte d’argent ou incohérence dans les données.*/
    @Override
    public void transfer(String idFrom, String idTo, long amount) {
        if(idFrom == null || idFrom.isBlank())
        { throw new IllegalArgumentException("idFrom is empty");}
        if(idTo == null || idTo.isBlank())
        { throw new IllegalArgumentException("idTo is empty");}

        if(idTo.equals(idFrom)){
            throw new IllegalArgumentException("ids need to be distinct");
        }
        if(amount<=0) {
            throw new IllegalArgumentException("amount must be > 0");
        }

        getAccount(idFrom);
        getAccount(idTo);

        // old state
        long amountSenderAccount = balanceByAccountId.get(idFrom);
        long amountReceiverAccount = balanceByAccountId.get(idTo);

        if(amountSenderAccount < amount)
        {throw new InsufficientFundsException("insuffscient balance on sender account, your balance us currently :"
                    +amountSenderAccount +" and you want to send : " +amount);}

        //atomicity
        balanceByAccountId.put(idFrom, amountSenderAccount - amount); // debit
        try{ // dans try on met le code qui peut echouer
            credit(idTo,amount); // credit
        }catch (RuntimeException e){ // dans catch, quoi faire si ca echoue
            //rollback
            balanceByAccountId.put(idFrom,amountSenderAccount);
            throw e;
        }


    }


}
