public class Monstre {

    private Coordonne position; // Position actuelle du monstre (null = hors plateau)
    private boolean estSurPlateau = false; // Indique si le monstre s'est déjà déplacé sur le plateau ou non
    private int deplacementsRestantsParCycle; // Nombre de déplacements possibles par cycle
    private static final int DEPLACEMENTS_MAX_PAR_CYCLE = 5; // Déplacements maximum par cycle (modifiable)

    public Monstre() {
        this.position = null; // Hors du plateau au départ
        this.estSurPlateau = false;
        this.deplacementsRestantsParCycle = DEPLACEMENTS_MAX_PAR_CYCLE; // Initialisation par défaut
    }

    /**
     * Déplacer le monstre pour la première fois : il se positionne sur la sortie ou par défaut (0, 0).
     *
     * @param sortie Coordonnée de la case de sortie du plateau.
     */
    public void premierDeplacement(Coordonne sortie) {
        if (estSurPlateau) {
            System.out.println("[Erreur] Le monstre est déjà sur le plateau !");
            return;
        }

        // Utilisez la coordonnée de sortie si elle est valide
        if (sortie != null) {
            this.position = sortie;
            this.estSurPlateau = true;
        } else {
            // Sinon positionnez par défaut si aucune sortie n'est fournie
            this.position = new Coordonne(0, 0);
        }
    }

    /**
     * Calcule la prochaine position du monstre en avançant d'une case vers une cible.
     *
     * @param cible La coordonnée cible où se diriger.
     * @return La nouvelle coordonnée du monstre après déplacement.
     */
    public Coordonne calculProchainDeplacement(Coordonne cible) {
        if (position == null) {
            System.out.println("[Erreur] Le monstre n'est pas encore sur le plateau !");
            return null;
        }

        int nouveauX = position.getX();
        int nouveauY = position.getY();

        // Mouvement vers la cible (en avançant d'une case)
        if (position.getX() < cible.getX()) {
            nouveauX++;
        } else if (position.getX() > cible.getX()) {
            nouveauX--;
        }

        if (position.getY() < cible.getY()) {
            nouveauY++;
        } else if (position.getY() > cible.getY()) {
            nouveauY--;
        }

        return new Coordonne(nouveauX, nouveauY);
    }

    /**
     * Déplace le monstre vers une nouvelle position.
     *
     * @param nouvellePosition Nouvelle position calculée pour le monstre.
     */
    public void deplacer(Coordonne nouvellePosition) {
        if (!estSurPlateau) {
            System.out.println("[Erreur] Le monstre n'est pas encore sur le plateau !");
            return;
        }

        this.position = nouvellePosition;
        deplacementsRestantsParCycle--; // Réduit le nombre de déplacements restants pour ce cycle
        System.out.println("Le monstre a été déplacé à la position " + nouvellePosition);
    }

    /**
     * Vérifie si le monstre peut encore se déplacer dans le cycle actuel.
     *
     * @return `true` si le monstre peut encore se déplacer, `false` sinon.
     */
    public boolean peutEncoreSeDeplacer() {
        return deplacementsRestantsParCycle > 0; // Le monstre peut encore se déplacer s'il lui reste des déplacements
    }

    /**
     * Réinitialise les déplacements restants pour un nouveau cycle.
     */
    public void reinitialiserDeplacements() {
        deplacementsRestantsParCycle = DEPLACEMENTS_MAX_PAR_CYCLE; // Remet les déplacements au maximum
    }

    // Getteurs

    public Coordonne getPosition() {
        return position;
    }

    public boolean isSurPlateau() {
        return estSurPlateau;
    }
}
