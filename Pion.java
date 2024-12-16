public class Pion {
    private final String id; // Identifiant unique du pion
    private Coordonne position;
    private boolean estSorti;
    private boolean sortiTemporairement;
    private int valeurFaceA; // Face visible utilisée pour les déplacements
    private int valeurFaceB; // Face opposée (sera utilisée après changement de face)
    private Joueur proprietaire; // Référence au joueur propriétaire du pion
    private int deplacementsRestants;
    private int retournementsRestants = 2;
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
        this.sortiTemporairement = false;
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
        if (retournementsRestants > 0) {
            int temp = this.valeurFaceA;
            this.valeurFaceA = this.valeurFaceB;
            this.valeurFaceB = temp;

            retournementsRestants--; // Diminue le compteur
            System.out.println("[INFO] Le pion " + this.id + " a été retourné. Retournements restants : " + retournementsRestants);
        } else {
        }
    }

    @Override
    public String toString() {
        return this.id + " (position=" + this.position + ", faceVisible=" + this.valeurFaceA + ")";
    }

    public void reinitialiserEntree() {
        if (this.estSorti) {
            System.out.println("Impossible de réinitialiser le pion " + this.id + " car il a été mangé définitivement.");
            return; // Le pion ne peut pas être réinitialisé
        }

        // Réinitialise l'état pour un pion hors plateau
        this.position = null;
        this.reinitialiserFace();
        this.deplacementsRestants = this.valeurFaceA;
        this.deplacementsEffectuesAvantMonstre = 0;
        this.reinitialiserRetournement();
        System.out.println("[Info] Le pion " + this.id + " a été réinitialisé pour un nouveau cycle.");
    }

    public void sortirPion(Pion pion) {
        if (Jeu.premiereManche) {
            // Sortie temporaire en première manche
            pion.sortirTemporairement(); // Sortie temporaire
            System.out.println("[INFO] Le pion " + pion.getId() + " a été retiré provisoirement du plateau. Il pourra revenir.");
        } else if (Jeu.secondeManche) {
            // Sortie définitive en seconde manche
            pion.sortirDefinitivement(); // Sortie définitive
            System.out.println("[INFO] Le pion " + pion.getId() + " a été mangé définitivement dans la seconde manche !");
        }
        pion.setPosition(null); // Retire la position du pion
    }

    // Méthode pour remettre la face visible à son état initial (Face A par défaut)
    // Méthode pour remettre les faces visibles à leur état initial
    public void reinitialiserFace() {
        this.valeurFaceA = Integer.parseInt(this.getId().substring(this.getId().length() - 1)); // Remet A à sa valeur initiale
        if (this.valeurFaceB < this.valeurFaceA) { // Ajuster B si besoin (ordre garantit la face initiale visible)
            int temp = this.valeurFaceA;
            this.valeurFaceA = this.valeurFaceB;
            this.valeurFaceB = temp;
        }
    }
    public boolean peutRevenirEnJeu() {
        // Pion doit être en état sorti temporairement
        return this.sortiTemporairement && !this.estSorti && this.position == null;
    }

    public boolean remettreEnJeu(Coordonne position) {
        if (!peutRevenirEnJeu()) {
            System.out.println("[Erreur] Le pion " + this.id + " ne peut pas revenir en jeu.");
            return false; // Impossible de remettre en jeu
        }

        // Met à jour les états
        this.position = position; // Mise à jour de la position sur le plateau
        this.sortiTemporairement = false; // Le pion n'est plus temporairement sorti
        this.estSorti = false; // Assurez-vous qu'il n'est pas marqué comme définitivement sorti

        System.out.println("[Info] Le pion " + this.id + " a été remis en jeu sur la position : " + position);
        return true; // Remise en jeu réussie
    }

    // Réinitialisation des retournements pour chaque pion (appelée à chaque fin de phase monstre)
    public void reinitialiserRetournement() {
        retournementsRestants = 2;
    }

    // Réinitialisation des déplacements pour préparer chaque pion pour la nouvelle phase joueur
    public void reinitialiserDeplacements() {
        this.deplacementsRestants = this.valeurFaceA; // Réinitialise avec la face visible
        this.deplacementsEffectuesAvantMonstre = 0; // Remet à zéro les déplacements avant la phase du monstre
    }

    // Méthode pour savoir si le pion peut encore se retourner
    public boolean peutEncoreRetourner() {
        return retournementsRestants > 0;
    }

    public void setDeplacementsEffectuesAvantMonstre(int valeur) {
        deplacementsEffectuesAvantMonstre = valeur;
    }

    // Sortir définitivement du plateau
    public void sortirDefinitivement() {
        this.estSorti = true; // Marque comme sorti définitif
        this.sortiTemporairement = false; // Plus temporaire
    }

    public void sortirTemporairement() {
        this.estSorti = false; // Ce n'est pas une sortie définitive
        this.sortiTemporairement = true; // Sortie temporaire en 1ère manche
        this.position = null;
    }

    // Réinitialiser état temporaire pour le remettre en jeu
    public void reinitialiserPourRejouer() {
        this.estSorti = false;
        this.sortiTemporairement = false; // Il est maintenant actif à nouveau
    }

    public boolean estSorti() {
        return this.estSorti;
    }

    public boolean estSortiTemporairement() {
        return this.sortiTemporairement;
    }

    public void sortir() {
        this.estSorti = true;
    }

    // Getteurs et Setteurs

    public Joueur getProprietaire() {
        return this.proprietaire;
    }

    public void setProprietaire(Joueur proprietaire) {
        this.proprietaire = proprietaire;
    }

    public String getId() {
        return this.id;
    }

    public Coordonne getPosition() {
        return this.position;
    }

    public boolean isEstSorti() {
        return estSorti;
    }

    public void setEstSorti(boolean estSorti) {
        this.estSorti = estSorti;
    }

    public boolean isSortiTemporairement() {
        return sortiTemporairement;
    }

    public void setSortiTemporairement(boolean sortiTemporairement) {
        this.sortiTemporairement = sortiTemporairement;
    }

    public void setPosition(Coordonne position) {
        this.position = position;
    }

    public int getValeurFaceA() {
        return this.valeurFaceA;
    }

    public int getValeurFaceB() {
        return this.valeurFaceB;
    }
}
