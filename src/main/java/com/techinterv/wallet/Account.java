package com.techinterv.wallet;

import java.util.Objects;

public final class Account {
    // Avec final on creer une classe immuable, apres creation sa valeur ne peut
    // plus etre modifié, final est principalement utiliser pour la securité, la conception
    // de classe immuable et parfois pour permettre des optimisations par le compilateur

    // declaration des attributs
    private final String accountId; // les champs sont aussi immuables avec final
    private final String userId;

    public Account(String accountId,String userId) // initialise l'objet a da creation et garantit un etat valide
    {
        this.accountId = Objects.requireNonNull(accountId,"accountId"); // si quelqu'un cree un compte avec id null, ca explose immediatement avec un message clair
        this.userId = Objects.requireNonNull(userId,"userId"); // kifkif, si quelqun crre un account avec un userId null , ca explose direcetement
    }  // ici le message d'erreur va etre renvoye un NullPointerException, et avec "accounId" et "userId", ca va nous afficher directement qui est lattribut null au lieu de juste
      // avoir une erreur vague

    public String getAccountId(){ // le getter c pour lire la valeur
        return accountId;
    }
    public String getUserId(){
        return userId;
    }

    /*on ovveride cette methode, parce que si on lexecute directement sur deux objet account, ca va aller comparer leur adresses memoire
     et meme si c le meme compte ca va retourne false, donc on la redefinit pour notre contenu, on comparant les deux id des deux comptr qui sont des strings, et avec equals sur string
     c le contenue qui va etre comparer parce que la classe string a deja ovveride la methode equals */
    @Override
    public boolean equals (Object o)
    {   // this est la classe sur laquelle on utilise la methode equals : a1.equals(a2)  this : a1, Object o : a2
        if (this == o) return true; // ici on va comparer les adresses en memoire de notre classe sur laquelle on passe la methode(this) par rapport a lobjet de comparaison o
        if(o==null || getClass()!= o.getClass()) return false; // getClass() va nous retourner le type de lobject(this) sur lequels on apelle la methode equals, et on le comparer a la classe passe en paramtre pour etre sur quon compare Account et Account
        Account account = (Account)o;
        return accountId.equals(account.getAccountId()); // accountId 7af howa l accountId taa l this, taa l classe li ahna feha taw
    }

    /*En Java, les collections basées sur le hachage comme HashMap et HashSet utilisent à la fois les méthodes hashCode() et equals()
    pour fonctionner correctement. Lorsqu’un objet est ajouté ou recherché dans ce type de collection, Java commence par appeler hashCode()
    afin de déterminer dans quel “bucket” interne chercher. Cette étape sert uniquement à localiser rapidement les éléments potentiellement
    correspondants. Ensuite, Java utilise la méthode equals() pour vérifier si l’objet recherché est logiquement égal à un objet déjà présent.
    Par défaut, hashCode() et equals() sont basés sur l’identité mémoire, ce qui signifie que deux objets différents en mémoire mais contenant
    les mêmes données ne sont pas considérés comme égaux. En redéfinissant equals() pour comparer l’identité logique de l’objet
    (par exemple accountId) et hashCode() à partir du même champ, on garantit que deux objets représentant la même entité logique seront
    traités comme égaux par les collections. Cela permet aux méthodes comme contains, get ou remove de fonctionner correctement.*/

    // hashcode retourne un int qui est souvent base sur l'emplacement memoir d'un objet, hashcode est toujours appele pour
    // les sets et les maps, ils utilisent hashcode pour retourner lemplacement dun objet en memoire et apres aquals pour comparere si lobjet est le meme
    // on doit override hashcode pour quil soit base sur laccount id et non sur ladresse memoire, parce que si on veut faire
    // map.contains(a2) et que a1 equals a2, pour que map.contains(a2) returns true, faut que le hashcode de
    @Override
    public int hashCode()
    {
        return accountId.hashCode();
    }

    @Override
    public String toString(){
        return "Account{"
                + "accoundId=" +accountId + '\''
                + ", userId =" +userId + '\''
                + '}';
    }
}
