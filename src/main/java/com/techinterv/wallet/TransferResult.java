package com.techinterv.wallet;
/*
 * Idempotency principle:

 * Each transfer request is associated with an idempotency key that represents
 * a single business intention (for example: "send money once").

 * If the same request is retried (because of a network issue, timeout, or client retry),
 * it is sent again with the same idempotency key.

 * The service checks this key:
 * - if the key is new, the transfer is executed and a TransferResult object is created
 *   and stored.
 * - if the key was already processed, the transfer is NOT executed again.
 *   The previously stored TransferResult is returned instead.

 * This prevents duplicate transfers and ensures that the same operation
 * always returns the same result when retried.

 * TransferResult exists to represent the "receipt" of a transfer and to allow
 * the service to replay the exact same response for repeated requests,
 * instead of recalculating values or modifying balances again.
 */

public class TransferResult {
    private final String transferId;
    private final String fromId;
    private final String toId;
    private final long amount;
    private final long senderBalanceAfter;
    private final long receiverBlanaceAfter;

    public TransferResult (String transferId, String fromId, String toId, long amount, long senderBalanceAfter,
    long receiverBlanaceAfter)
    {
        this.transferId=transferId;
        this.fromId=fromId;
        this.toId=toId;
        this.amount=amount;
        this.senderBalanceAfter=senderBalanceAfter;
        this.receiverBlanaceAfter=receiverBlanaceAfter;
    }

    public String getTransferId() { return transferId; }
    public String getFromId () { return fromId; }
    public String getToId() { return toId; }
    public long getAmount() {return amount;}
    public long getSenderBalanceAfter() { return senderBalanceAfter;}
    public long getReceiverBlanaceAfter() { return receiverBlanaceAfter; }







}


/*1) Cas le plus fréquent : la requête a “échoué” côté client, mais a “réussi” côté serveur
Exemple : timeout / réseau.
Le serveur a exécuté le transfert ✅
Mais la réponse n’est jamais arrivée au client ❌ (timeout, wifi, etc.)
Dans ce cas :
le serveur a déjà le résultat stocké pour cette key
quand le client retry avec la même key → le serveur renvoie le résultat stocké
donc tu as l’impression “ça a échoué”, mais en réalité ça avait déjà été exécuté
👉 C’est exactement le problème que l’idempotency résout.
Quand est-elle réellement exécutée ?
➡️ La première fois que le serveur reçoit la key et arrive à finir l’opération.


2) Cas plus rare : la requête a vraiment échoué côté serveur (avant de terminer)
Exemple : bug, crash, exception, DB down, etc.
Ici, il y a 2 sous-cas :
2A) Échec “avant d’avoir fait quoi que ce soit”
Rien n’a été débité
Rien n’a été crédité
Pas de résultat à stocker
✅ Dans ce cas, le retry avec la même key ré-exécutera (et cette fois, si ça marche, on stocke).
2B) Échec “pendant l’exécution”
C’est là où atomicité entre en jeu :
Si ton transfert est atomique, alors même si ça crash au milieu, tu rollback
Donc à la fin : soit tout a réussi, soit rien n’a changé
✅ Donc tu ne stockes un résultat que si l’opération a réellement réussi.
La règle pro à retenir (très importante)
On ne stocke jamais un TransferResult pour une key si l’opération n’a pas réussi.
On stocke seulement quand on est sûr que :
l’état est cohérent
le transfert a été appliqué exactement une fois

Donc ta question : “quand sera elle réellement exécutée ?”
✅ Elle est exécutée :
à la première tentative qui réussit côté serveur
Et si la première tentative “échoue” juste à cause du réseau :
elle a quand même été exécutée côté serveur
donc les retries ne font que récupérer le résultat


Pour ton exercice wallet (in-memory)
Dans notre implémentation :
si transfer(from,to,amount) lance une exception → on ne stocke pas dans resultsByIdempotencyKey
si transfer réussit → on stocke le TransferResult
Donc :
“vrai échec serveur” → retry réexécute
“échec réseau client” → le serveur avait déjà stocké → retry renvoie pareil*/
