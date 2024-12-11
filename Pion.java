public class Pion {
    private final String id; // Identifiant unique du pion (ex: "A1", "B2")
    private Coordonne position; // Position actuelle du pion sur le plateau
    private boolean estSorti; // Indique si le pion a atteint la sortie
    private int valeurFaceA; // Face visible utilisée pour les déplacements
    private int valeurFaceB; // Face opposée (sera utilisée après changement de face)
    private Joueur proprietaire; // Référence au joueur propriétaire du pion
    private int deplacementsRestants; // Déplacements restants pour ce pion
    private int retournementsRestants = 2; // Nombre maximal de retournements restants pour la phase joueur
    private int deplacementsEffectuesAvantMonstre = 0;

    // Nouvelle méthode : Incrémente le compteur des déplacements avant le monstre
    public void incrementerDeplacementsAvantMonstre() {
        deplacementsEffectuesAvantMonstre++;
    }

    /**
     * Constructeur du pion avec un identifiant unique.
     *
     * @param proprietaire Joueur propriétaire du pion
     * @param id           Identifiant unique du pion (ex: "A1", "B2")
     * @param position     Position initiale du pion (null s'il n'est pas encore sur le plateau)
     * @param valeurFaceA  Valeur de la face visible
     * @param valeurFaceB  Valeur de la face opposée
     */
    public Pion(Joueur proprietaire, String id, Coordonne position, int valeurFaceA, int valeurFaceB) {
        this.proprietaire = proprietaire;
        this.id = id; // Identifiant unique du pion (joueur + numéro)
        this.position = position;
        this.estSorti = false;
        this.valeurFaceA = valeurFaceA;
        this.valeurFaceB = valeurFaceB;
    }

    public boolean peutEncoreDeplacerAvantMonstre() {
        return deplacementsEffectuesAvantMonstre < 2; // Limite définie à 2 déplacements
    }

    public int getDeplacementsEffectuesAvantMonstre() {
        return deplacementsEffectuesAvantMonstre;
    }

    // Méthode pour retourner le pion (diminue les retournements restants)
    public void retournerPion() {
        if (peutEncoreRetourner()) {
            // Inversion de la face visible
            int temp = valeurFaceA;
            valeurFaceA = valeurFaceB;
            valeurFaceB = temp;

            retournementsRestants--; // Diminue le nombre de retournements autorisés
        } else {
            System.out.println("[Erreur] Le pion ne peut plus être retourné !");
        }
    }

    /**
     * Déplace le pion à une nouvelle position.
     *
     * @param nouvellePosition La nouvelle position du pion.
     */
    public void deplacer(Coordonne nouvellePosition) {
        if (nouvellePosition == null) {
            System.out.println("[Erreur] La nouvelle position ne peut pas être nulle !");
            return;
        }

        this.position = nouvellePosition;
        System.out.println(id + ": Pion déplacé à la nouvelle position : " + nouvellePosition);
    }

    @Override
    public String toString() {
        return id + " (position=" + position + ", faceVisible=" + valeurFaceA + ")";
    }

    // Réinitialisation des retournements pour chaque pion (appelée à chaque fin de phase monstre)
    public void reinitialiserRetournements() {
        retournementsRestants = 2;
    }

    // Réinitialisation des déplacements pour préparer chaque pion pour la nouvelle phase joueur
    public void reinitialiserDeplacements() {
        deplacementsRestants = valeurFaceA; // La valeur de la face A définit le nombre de déplacements pour ce pion
        deplacementsEffectuesAvantMonstre = 0; // Réinitialise le compteur avant monstre (si règle applicable)
    }

    // Méthode pour savoir si le pion peut encore se retourner
    public boolean peutEncoreRetourner() {
        return retournementsRestants > 0;
    }

    public void setDeplacementsEffectuesAvantMonstre(int valeur) {
        this.deplacementsEffectuesAvantMonstre = valeur;
    }

    // Getteurs et Setteurs

    public Joueur getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(Joueur proprietaire) {
        this.proprietaire = proprietaire;
    }

    public String getId() {
        return id;
    }

    public Coordonne getPosition() {
        return position;
    }

    public void setPosition(Coordonne position) {
        this.position = position;
    }

    public boolean estSorti() {
        return estSorti;
    }

    public void sortir() {
        this.estSorti = true;
    }

    public int getValeurFaceA() {
        return valeurFaceA;
    }

    public int getValeurFaceB() {
        return valeurFaceB;
    }
}