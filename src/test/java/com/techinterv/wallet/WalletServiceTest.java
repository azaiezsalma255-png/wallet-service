package com.techinterv.wallet;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WalletServiceTest {

// assertEquals(expected, actual)
    @Test
    void createaccountTest () {

        WalletInterface walletService = new WalletService(); // on apelle le service a tester
        Account a = walletService.createAccount("user-1"); // on stock le resultat de la fonction a tester dans son type de retour qui est Account
        assertNotNull(a.getAccountId()); // verifier que le service a bien generer un account id
        assertFalse(a.getAccountId().isBlank()); // verifier l'account id generé n'est pas vide
        //“Je m’attends à ce que l’ID récupéré soit le même que l’ID créé.”
        assertEquals("user-1",a.getUserId()); // verifier que le resultat de la methode correspond bien au resultat attendu
        System.out.println("Account { id :" +a.getAccountId() + ", user : " + a.getUserId() + " }");

    }

    @Test
    void getAccountByIdTest(){
        WalletInterface walletServcie = new WalletService();
        Account created = walletServcie.createAccount("User-1");
        Account fetched = walletServcie.getAccount(created.getAccountId()); // la methode a tester avec le id de laccount creer
        assertEquals(created.getAccountId(),fetched.getAccountId());

    }


    @Test
    void getListAccounts() {
        WalletInterface walletServcie = new WalletService();
        Account created = walletServcie.createAccount("user-1");
        List<Account> list = walletServcie.listAccountsByUser("user-1");
        // pour verifier que les accounts dans la liste esxiste bien et pour ne pas avoir une erreur dans la ligne suivante quand on veut
        // recupere le premier element de la liste des accounts
        assertFalse(list.isEmpty());
        Account fetched = list.get(0);
        assertEquals(created.getAccountId(),fetched.getAccountId());
        assertEquals("user-1",fetched.getUserId());


    }

    @Test
    void initialiserBalanceTest(){
        WalletInterface walletServcie = new WalletService();
        Account account = walletServcie.createAccount("user-1");
        String id = account.getAccountId();
        long balance = walletServcie.getBalance(id);
        assertEquals(0L,balance);
        System.out.println("balance initialise a :" + balance);

    }

    @Test
    void deposit_increaseBalance(){
        WalletInterface walletServcie = new WalletService();
        Account account = walletServcie.createAccount("user-1");
        String accountId = account.getAccountId();
        walletServcie.deposit(accountId,100);
        assertEquals(100,walletServcie.getBalance(accountId));
        System.out.println("Balance after deposit: " + walletServcie.getBalance(accountId));
    }

    @Test
    void montantInvalideTest(){
        // dans ce test on va verifier que l'exception qu'on a lancé dans la methode est
        // bien executer pour des valeurs negatives et inferieures a 0
        WalletInterface walletServcie = new WalletService();
        Account account = walletServcie.createAccount("user-1");
        /*assertThrows veut dire :
         “Je m’attends à ce que ce code lance une exception de tel type.”
          Donc si l’exception est lancée ✅ le test passe.
          Si aucune exception (ou une autre exception) est lancée ❌ le test échoue.*/
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> walletServcie.deposit(account.getAccountId(),0));

        IllegalArgumentException ex1 =assertThrows(IllegalArgumentException.class,
                () -> walletServcie.deposit(account.getAccountId(),-10));

        System.out.println("Execption: " +ex.getClass().getSimpleName());
        System.out.println("Message: " +ex.getMessage());

        System.out.println("Execption: " +ex1.getClass().getSimpleName());
        System.out.println("Message: " +ex1.getMessage());


    }

    @Test
    void compteExistantTest()
    {
        WalletInterface walletService = new WalletService();
        // on va tester sur un account qui n'existe pas pour avoir une exseption not found levée

        NotFoundexception ex =assertThrows(NotFoundexception.class,
                () -> walletService.deposit("Non existing account",100));
    }

    @Test
    void withdrawTest()
    { WalletInterface walletService = new WalletService();
      Account a = walletService.createAccount("user1");
      walletService.deposit(a.getAccountId(),100);
      walletService.withdraw(a.getAccountId(),50);
      assertEquals(50,walletService.getBalance(a.getAccountId()));
      System.out.println("actual amount is : " +walletService.getBalance(a.getAccountId()) + " and expected amount is 50");
    }

    @Test
    void withdrawInvalidAmountTest()
    {
        WalletInterface walletService = new WalletService();
        Account a = walletService.createAccount("user1");
        assertThrows(IllegalArgumentException.class,
                () -> walletService.withdraw(a.getAccountId(),0));
        assertThrows(IllegalArgumentException.class,
                () -> walletService.withdraw(a.getAccountId(),-5));
    }

    @Test
    void InsufficientFunds()
    {
        WalletInterface walletService = new WalletService();
        Account a = walletService.createAccount("user1");
        walletService.deposit(a.getAccountId(),100);
        InsufficientFundsException ex = assertThrows(InsufficientFundsException.class,
                () -> walletService.withdraw(a.getAccountId(),150));

        System.out.println("Exeception : " + ex.getClass().getSimpleName());
        System.out.println("Message" +ex.getMessage());
    }

    @Test
    void TransferFromSenderToReceiverTest() {
        WalletInterface walletService = new WalletService();
        Account a1 = walletService.createAccount("user1");
        Account a2 = walletService.createAccount("user2");
        walletService.deposit(a1.getAccountId(),100);
        walletService.transfer(a1.getAccountId(),a2.getAccountId(),30);
        assertEquals(70,walletService.getBalance(a1.getAccountId()));
        assertEquals(30,walletService.getBalance(a2.getAccountId()));

        System.out.println("expected for a1 is 70, real a1 balance is" +walletService.getBalance(a1.getAccountId()));
        System.out.println("expected for a2 is 30, real a2 balance is" +walletService.getBalance(a2.getAccountId()));

    }
    @Test
    void transfer_withSameIds_throwsException(){
        WalletInterface walletService = new WalletService();
        assertThrows(IllegalArgumentException.class, () -> walletService.transfer("1","1",30));
    }

    @Test
    void transfer_withInvalidFromId_throwsException() {
        WalletInterface walletService = new WalletService();
        assertThrows(IllegalArgumentException.class,
                () -> walletService.transfer("","1",30));
        assertThrows(IllegalArgumentException.class,
                () -> walletService.transfer(" ","1",30));
        assertThrows(IllegalArgumentException.class,
                () -> walletService.transfer("null","1",30));
    }

    @Test
    void transfer_withInvalidToId_throwsException() {
        WalletInterface walletService = new WalletService();
        assertThrows(IllegalArgumentException.class,
                () -> walletService.transfer("1","",30));
        assertThrows(IllegalArgumentException.class,
                () -> walletService.transfer("1"," ",30));
        assertThrows(IllegalArgumentException.class,
                () -> walletService.transfer("1","null",30));
    }

    @Test
    void validAmountForTransfer(){
        WalletInterface walletService = new WalletService();
        assertThrows(IllegalArgumentException.class,
                () -> walletService.transfer("1","1",-5));
        assertThrows(IllegalArgumentException.class,
                () -> walletService.transfer("1","1",0));
    }

    /*Ce test vérifie que l’opération de transfert est atomique, c’est-à-dire qu’elle ne modifie jamais l’état du système de
    manière partielle. Pour cela, on simule volontairement une panne pendant l’étape de crédit du compte destinataire en surchargeant
    la méthode credit afin qu’elle lance une exception. On s’attend alors à ce que le transfert échoue, mais surtout à ce que le solde
    du compte émetteur reste inchangé. Le test confirme ainsi que, même en cas d’erreur en cours d’exécution, aucun argent n’est perdu
    et l’état des comptes reste cohérent, ce qui est essentiel dans un système financier.*/
    @Test
    void transfer_isAtomic_whenCreditFails() {
      WalletInterface walletServie = new WalletService(){
      @Override
      protected void credit(String idTo, long amount){
          throw new RuntimeException("simulated crash during credit");
        }};

      Account a1 = walletServie.createAccount("user1");
      Account a2 = walletServie.createAccount("user2");

      walletServie.deposit(a1.getAccountId(),100);

      //on sattends a une exception
      RuntimeException ex = assertThrows(RuntimeException.class,
              ()-> walletServie.transfer(a1.getAccountId(), a2.getAccountId(), 30));

      assertEquals(100,walletServie.getBalance(a1.getAccountId()));
      assertEquals(0,walletServie.getBalance(a2.getAccountId()));

      System.out.println("Exeception : " + ex.getClass().getSimpleName());
      System.out.println("Message : " +ex.getMessage());
      System.out.println("le montant dans le sender apres crash est account est " +walletServie.getBalance(a1.getAccountId()) + " et il doit etre a 100");
      System.out.println("le montant dans le receiver apres crash est account est " +walletServie.getBalance(a2.getAccountId())+ " et il doit etre a 0");


    }





}
