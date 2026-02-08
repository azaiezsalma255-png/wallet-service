package com.techinterv.wallet;


/*
* Pourquoi on en a besoin ?

Dans ton service Wallet, tu vas avoir des méthodes comme :
getAccount(accountId)
plus tard withdraw, transfer, etc.
Si l’accountId n’existe pas :
❌ retourner null → mauvaise pratique
❌ laisser une NullPointerException → pas clair
✅ lever une exception explicite → propre, lisible, pro
C’est exactement ce qu’un interviewer attend.*/

public class NotFoundexception extends RuntimeException {

    // on va utiliser ce NotFoundExecption dans notre service, et on ne met pas le msg directement
    //ici pour pouvoir lutiliser pour dautres objets et cas, on met le msg apres when we throw
    // l'exception dans le service suite a une condition
    public NotFoundexception(String message){
        super(message);
    }

}
