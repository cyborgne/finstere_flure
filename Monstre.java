import java.util.ArrayList;
import java.util.List;

public class Monstre {

    public static final int DEPLACEMENTS_MAX_PAR_CYCLE = 20; // Déplacements maximum par cycle

    private Coordonne position; // Position actuelle du monstre (null = hors plateau)
    private boolean estSurPlateau = false; // Indique si le monstre est sur le plateau
    private int deplacementsRestantsParCycle; // Nombre de déplacements possibles par cycle
    private Direction direction; // Orientation actuelle du monstre

    public Monstre() {
        this.position = null; // Hors du plateau au départ
        this.estSurPlateau = false;
        this.deplacementsRestantsParCycle = DEPLACEMENTS_MAX_PAR_CYCLE;
        this.direction = Direction.BAS; // Par défaut : regard vers le bas
    }

    /**
     * Place le monstre sur une position initiale.
     *
     * @param positionInitiale La position initiale où le monstre doit être placé.
     * @param plateau          Le plateau du jeu (doit être valide).
     */
    public void premierDeplacement(Coordonne positionInitiale, Plateau plateau) {
        if (positionInitiale == null) {
            throw new IllegalArgumentException("[Erreur] La position initiale du monstre ne peut pas être nulle.");
        }
        if (plateau == null) {
            throw new IllegalArgumentException("[Erreur] Le plateau ne peut pas être null.");
        }

        if (this.estSurPlateau) {
            System.out.println("[Erreur] Le monstre est déjà sur le plateau !");
            return;
        }

        // Placement initial
        this.position = positionInitiale;
        this.estSurPlateau = true;

        // Mise à jour du plateau
        plateau.setPositionMonstre(this.position);
        plateau.mettreAJour();
        System.out.println("Le monstre entre en jeu à la position " + this.position + ".");
    }

    public void remettrePionsEnJeu(List<Pion> pions, Plateau plateau) {
        for (Pion pion : pions) {
            if (pion.peutRevenirEnJeu()) { // Identifier les pions qui peuvent être remis plus tard
                pion.setPosition(null); // Hors plateau
                pion.setSortiTemporairement(true);
                System.out.println("[Info] Le pion " + pion.getId() + " a été sorti temporairement par le monstre.");
            }
        }
    }

    public void deplacerSelonTuile(TuilePierreTombale tuile, Plateau plateau) {
        if (tuile == null || plateau == null) {
            throw new IllegalArgumentException("[Erreur] La tuile ou le plateau ne peut pas être nul.");
        }

        int nombreDePas = tuile.getPas();
        String typeTuile = tuile.getCible(); // Type de la tuile, comme "une proie" ou "deux proies"
        System.out.println("[Info] Déplacement avec la tuile : " + tuile);

        // Ajouter prise en charge des tuiles spécifiques
        if ("une proie".equalsIgnoreCase(typeTuile)) {
            System.out.println("[Info] Gestion de la tuile 'une proie'.");
            gererDeplacementVersUneProie(nombreDePas, plateau); // Méthode dédiée pour gérer "une proie"
            return;
        } else if ("deux proies".equalsIgnoreCase(typeTuile)) {
            System.out.println("[Info] Gestion de la tuile 'deux proies'.");
            gererDeplacementVersDeuxProies(nombreDePas, plateau); // Méthode dédiée pour gérer "deux proies"
            return;
        }

        // Si ce n'est ni une tuile "une proie" ni "deux proies", appliquer le comportement standard
        // Boucle sur chaque pas assigné
        for (int i = 0; i < nombreDePas; i++) {
            // Affiche l'état du plateau
            System.out.println(plateau);

            // 1. Observer dans les trois directions
            Coordonne cible = regarderEtChoisirDirection(plateau); // Trouver la meilleure cible visible

            // 2. Se déplacer d'une case dans la direction actuelle
            boolean deplacementReussi = avancerDuneCase(plateau);

            // Si déplacement échoue (mur ou obstacle), arrêter la boucle
            if (!deplacementReussi) {
                System.out.println("[Info] Déplacement arrêté : le monstre a rencontré un obstacle.");
                break;
            }

            // 3. Vérifier si le monstre mange un pion sur la case actuelle
            if (plateau.contientPion(this.position)) {
                System.out.println("[Info] Le monstre a mangé un pion en position : " + this.position);
                mangerProie(plateau, this.position);
            }
        }

        // Observer une dernière fois après le dernier déplacement pour ajuster l'orientation
        regarderEtChoisirDirection(plateau);
        System.out.println("[Info] Direction finale après déplacement : " + this.direction);

        // Affichage final après les déplacements
        System.out.println(plateau);
    }

    private void gererDeplacementVersUneProie(int nombreDePas, Plateau plateau) {
        if (nombreDePas <= 0) {
            System.out.println("[Erreur] Le nombre de pas est nul ou négatif. Déplacement annulé.");
            return;
        }

        Coordonne cibleProche = trouverCibleProche(plateau);

        if (cibleProche == null) {
            System.out.println("[Avertissement] Aucune cible proche trouvée. Le monstre ne se déplace pas.");
            return;
        }

        System.out.println("[Info] Cible la plus proche trouvée à : " + cibleProche);

        // Déplacer le monstre pas à pas vers la cible
        while (nombreDePas > 0) {
            nombreDePas = deplacerVersCible(cibleProche, nombreDePas, plateau);

            if (this.position.equals(cibleProche)) {
                System.out.println("[Info] Le monstre a atteint la cible et s'arrête.");
                return;
            }
        }
    }

    private void gererDeplacementVersDeuxProies(int nombreDePas, Plateau plateau) {
        List<Coordonne> deuxCibles = trouverDeuxCiblesLesPlusProches(plateau);

        if (deuxCibles.isEmpty()) {
            System.err.println("[Erreur] Aucune proie trouvée pour la tuile 'deux proies'. Déplacement annulé.");
            return; // Pas de mouvement libre, on arrête directement.
        }

        System.out.println("[Info] Deux cibles les plus proches : " + deuxCibles);

        int proiesMangees = 0;

        // Déplacer pas à pas vers les deux cibles
        for (Coordonne cible : deuxCibles) {
            if (nombreDePas > 0) {
                nombreDePas = deplacerVersCible(cible, nombreDePas, plateau);

                // Si une cible a été atteinte
                if (this.position.equals(cible)) {
                    proiesMangees++;
                    System.out.println("[Info] Le monstre a mangé une proie. Total mangées : " + proiesMangees);

                    if (proiesMangees == 2) {
                        System.out.println("[Info] Le monstre a mangé deux proies et s'arrête.");
                        return; // Arrêt du déplacement après avoir mangé deux proies
                    }
                }
            } else {
                break; // Plus de pas restants
            }
        }
    }


    private int deplacerVersCible(Coordonne cible, int nombreDePas, Plateau plateau) {
        while (nombreDePas > 0) {
            // Calculer la prochaine position en direction de la cible
            Coordonne prochainePosition = trouverProchainePositionVersCible(cible, plateau);

            if (prochainePosition == null) {
                System.out.println("[Erreur] Aucune position valide pour avancer vers la cible.");
                break; // Le déplacement est bloqué
            }

            // Mettre à jour la direction avant le déplacement
            this.direction = calculerDirectionEntre(this.position, prochainePosition);

            // Effectuer le déplacement
            boolean deplacementReussi = avancerDuneCase(plateau);

            if (deplacementReussi) {
                nombreDePas--;

                // Vérifier si un pion est présent sur la position actuelle
                if (pionPresentSurPosition(this.position, plateau)) {
                    System.out.println("[Info] Le monstre a mangé un pion sur la position : " + this.position);
                    mangerProie(plateau, this.position); // Mange la proie si elle est atteinte
                }

                // Si la position actuelle correspond à la cible, le monstre s’arrête (dans les cas à cible)
                if (this.position.equals(cible)) {
                    break; // Arrêt des déplacements après avoir atteint la cible
                }
            } else {
                System.out.println("[Erreur] Déplacement impossible vers la cible !");
                break;
            }
        }

        return nombreDePas; // Retourne le nombre de pas restants (utile si cible non atteinte)
    }

    private boolean pionPresentSurPosition(Coordonne position, Plateau plateau) {
        // Retourne vrai si un pion est présent sur la case actuelle du plateau
        return plateau.getPions().stream()
                .filter(pion -> pion.getPosition() != null) // Vérifie si la position n'est pas nulle
                .anyMatch(pion -> pion.getPosition().equals(this.position));
    }

    private Direction calculerDirectionEntre(Coordonne actuelle, Coordonne prochaine) {
        if (actuelle == null || prochaine == null) {
            throw new IllegalArgumentException("[Erreur] Les coordonnées actuelles ou prochaines sont nulles.");
        }

        if (actuelle.equals(prochaine)) {
            System.out.println("[Avertissement] La position actuelle et la prochaine sont identiques.");
            return this.direction; // On conserve la direction actuelle
        }

        if (prochaine.getX() > actuelle.getX()) {
            return Direction.DROITE;
        } else if (prochaine.getX() < actuelle.getX()) {
            return Direction.GAUCHE;
        } else if (prochaine.getY() > actuelle.getY()) {
            return Direction.BAS;
        } else if (prochaine.getY() < actuelle.getY()) {
            return Direction.HAUT;
        }
        return this.direction; // Cas de bord (aucun changement)
    }

    private Coordonne trouverProchainePositionVersCible(Coordonne cible, Plateau plateau) {
        // Liste des directions possibles : haut, bas, gauche, droite
        List<Direction> directions = List.of(
                Direction.HAUT, Direction.BAS, Direction.GAUCHE, Direction.DROITE
        );

        Coordonne meilleurePosition = null;
        int distanceMinimale = Integer.MAX_VALUE;

        // Parcourir chaque direction
        for (Direction dir : directions) {
            Coordonne prochainePosition = dir.calculerNouvellePosition(this.position);

            // Vérifiez si la position est valide sur le plateau
            if (!plateau.estPositionValide(prochainePosition)) {
                continue; // Ignorer les positions invalides
            }

            // Calculez la distance vers la cible
            int distance = prochainePosition.distance(cible); // Distance de Manhattan

            // Trouver la meilleure position (celle qui réduit la distance)
            if (distance < distanceMinimale) {
                distanceMinimale = distance;
                meilleurePosition = prochainePosition;
            }
        }

        return meilleurePosition; // Retourne la position la plus proche de la cible
    }

    private List<Coordonne> trouverDeuxCiblesLesPlusProches(Plateau plateau) {
        List<Pion> pions = plateau.getPions();
        List<Coordonne> cibles = new ArrayList<>();

        // Utiliser une liste pour trier les pions par distance
        List<Pion> pionsNonSortis = new ArrayList<>();
        for (Pion pion : pions) {
            if (!pion.estSorti() && pion.getPosition() != null) {
                pionsNonSortis.add(pion);
            }
        }

        // Trier les pions par distance croissante
        pionsNonSortis.sort((p1, p2) -> {
            double distance1 = this.position.distance(p1.getPosition());
            double distance2 = this.position.distance(p2.getPosition());
            return Double.compare(distance1, distance2);
        });

        // Ajouter les deux premiers pions les plus proches (s'ils existent)
        for (int i = 0; i < 2 && i < pionsNonSortis.size(); i++) {
            cibles.add(pionsNonSortis.get(i).getPosition());
        }

        return cibles; // Retourne jusqu'à 2 cibles
    }

    /**
     * Calcule la prochaine position à partir de la position actuelle dans une direction donnée,
     * tout en validant la position sur le plateau.
     *
     * @param direction La direction dans laquelle avancer.
     * @param cible     (Optionnel) Une cible vers laquelle se déplacer.
     * @param plateau   Le plateau pour vérifier la validité des positions.
     * @return La prochaine position valide ou null si aucune position n'est trouvée.
     */
    private Coordonne calculerProchainePosition(Direction direction, Coordonne cible, Plateau plateau) {
        Coordonne prochainePosition;

        if (cible != null) {
            // Calcul vers la cible
            int deltaX = cible.getX() - this.position.getX();
            int deltaY = cible.getY() - this.position.getY();

            // Faire un pas vers la cible en priorisant le plus grand delta
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                prochainePosition = new Coordonne(
                        this.position.getX() + Integer.signum(deltaX),
                        this.position.getY()
                );
            } else {
                prochainePosition = new Coordonne(
                        this.position.getX(),
                        this.position.getY() + Integer.signum(deltaY)
                );
            }
        } else if (this.direction != null) {
            // Calcul dans une direction donnée
            prochainePosition = this.direction.calculerNouvellePosition(this.position);
        } else {
            return null; // Aucun déplacement possible (pas de cible ni de direction)
        }

        // Vérifier si la position calculée est valide
        if (plateau.estPositionValide(prochainePosition)) {
            return prochainePosition;
        } else {
            return null; // Position invalide
        }
    }

    private Coordonne trouverCibleProche(Plateau plateau) {
        List<Pion> pions = plateau.getPions();
        Coordonne cibleLaPlusProche = null;
        double distanceMinimale = Double.MAX_VALUE;

        // Rechercher le pion le plus proche
        for (Pion pion : pions) {
            if (!pion.estSorti() && pion.getPosition() != null) { // Ignorer les pions hors plateau ou déjà sortis
                double distance = this.position.distance(pion.getPosition());
                if (distance < distanceMinimale) {
                    distanceMinimale = distance;
                    cibleLaPlusProche = pion.getPosition();
                }
            }
        }
        return cibleLaPlusProche;
    }

    private void mangerProie(Plateau plateau, Coordonne position) {
        List<Pion> pionsSurCase = new ArrayList<>();

        // Rechercher les pions présents sur une case
        for (Pion pion : plateau.getPions()) {
            if (pion.getPosition() != null && pion.getPosition().equals(position)) {
                pionsSurCase.add(pion);
            }
        }

        if (!pionsSurCase.isEmpty()) {
            for (Pion pion : pionsSurCase) {
                pion.sortirPion(pion);
            }
            // Mettre à jour l'état du plateau
            plateau.mettreAJour();
        } else {
            System.out.println("[INFO] Aucun pion détecté à manger sur cette position.");
        }
    }

    public enum Direction {
        HAUT(0, -1), BAS(0, 1), GAUCHE(-1, 0), DROITE(1, 0);

        private final int dx; // Décalage en X
        private final int dy; // Décalage en Y

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public int getDx() {
            return dx;
        }

        public int getDy() {
            return dy;
        }

        // Retourne une coordonnée après un mouvement dans cette direction
        public Coordonne calculerNouvellePosition(Coordonne actuelle) {
            return new Coordonne(actuelle.getX() + dx, actuelle.getY() + dy);
        }

        // Direction suivante à gauche
        public Direction tournerGauche() {
            switch (this) {
                case HAUT:
                    return GAUCHE;
                case GAUCHE:
                    return BAS;
                case BAS:
                    return DROITE;
                case DROITE:
                    return HAUT;
            }
            return this;
        }

        // Direction suivante à droite
        public Direction tournerDroite() {
            switch (this) {
                case HAUT:
                    return DROITE;
                case DROITE:
                    return BAS;
                case BAS:
                    return GAUCHE;
                case GAUCHE:
                    return HAUT;
            }
            return this;
        }
    }

    private Coordonne regarderEtChoisirDirection(Plateau plateau) {
        List<Direction> directionsObservation = List.of(
                this.direction,                // Devant
                this.direction.tournerGauche(), // Gauche
                this.direction.tournerDroite()  // Droite
        );

        Coordonne meilleureCible = null;
        double distanceMinimale = Double.MAX_VALUE;

        // Parcourir les directions (devant, gauche, droite)
        for (Direction dir : directionsObservation) {
            Coordonne prochainePosition = this.position;

            // Recherche linéaire dans la direction actuelle
            while (true) {
                prochainePosition = dir.calculerNouvellePosition(prochainePosition);

                // Arrêter si la position devient invalide
                if (!plateau.estPositionValide(prochainePosition)) {
                    break;
                }

                // Si un pion est trouvé
                if (plateau.contientPion(prochainePosition)) {
                    double distance = this.position.distance(prochainePosition);

                    if (distance < distanceMinimale) {
                        distanceMinimale = distance;
                        meilleureCible = prochainePosition;
                        this.direction = dir; // Mise à jour immédiate de la direction
                    }
                    break; // Arrêter après avoir trouvé un pion
                }
            }
        }

        // Si aucune cible visible, conserver la direction actuelle
        if (meilleureCible == null) {
            System.out.println("[Info] Aucun pion visible. Le monstre continue tout droit.");
        } else {
            System.out.println("[Info] Nouvelle cible détectée à : " + meilleureCible + " dans la direction : " + this.direction);
        }

        return meilleureCible;
    }

    // Cette méthode ajuste la direction du monstre en fonction d'un pion identifié
    private void voitPion(Coordonne positionCible) {
        int deltaX = positionCible.getX() - this.position.getX(); // Différence en X
        int deltaY = positionCible.getY() - this.position.getY(); // Différence en Y

        // Calculer quelle direction prendre
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            // Prioriser le déplacement horizontal (X)
            if (deltaX > 0) {
                this.direction = Direction.DROITE;
            } else {
                this.direction = Direction.GAUCHE;
            }
        } else {
            // Prioriser le déplacement vertical (Y)
            if (deltaY > 0) {
                this.direction = Direction.BAS;
            } else {
                this.direction = Direction.HAUT;
            }
        }

        System.out.println("Monstre ajuste sa direction vers la cible : " + positionCible + ", nouvelle direction : " + this.direction);
    }

    // Cette méthode déplace le monstre case par case selon sa direction actuelle
    private boolean avancerDuneCase(Plateau plateau) {
        // Calculer la prochaine position d'après la direction actuelle
        Coordonne prochainePosition = this.direction.calculerNouvellePosition(this.position);

        // Vérifier si la position est valide
        if (!plateau.estPositionValide(prochainePosition)) {
            System.out.println("[Erreur] Position bloquée ou hors plateau : " + prochainePosition);
            return false;
        }

        // Mettre à jour la position du monstre
        this.position = prochainePosition;
        plateau.setPositionMonstre(this.position);
        plateau.mettreAJour();

        System.out.println("[Info] Monstre déplacé en " + this.position + ", direction : " + this.direction);
        return true;
    }

    private Coordonne trouverPremiereCaseValideDansColonne(int colonne, int departLigne, Plateau plateau) {
        for (int y = departLigne; y < plateau.getHauteur(); y++) {
            Coordonne position = new Coordonne(colonne, y);
            if (plateau.estPositionValide(position)) {
                return position; // Retourne la première case valide trouvée
            }
        }
        System.out.println("[Info] Aucune case valide trouvée dans la colonne " + colonne + " à partir de la ligne " + departLigne);
        return null;
    }

    private Coordonne trouverPremiereCaseValideDansLigne(int departColonne, int ligne, Plateau plateau) {
        for (int x = departColonne; x < plateau.getLargeur(); x++) {
            Coordonne position = new Coordonne(x, ligne);
            if (plateau.estPositionValide(position)) {
                return position; // Retourne la première case valide trouvée
            }
        }
        System.out.println("[Info] Aucune case valide trouvée dans la ligne " + ligne + " à partir de la colonne " + departColonne);
        return null;
    }

    public boolean peutEncoreSeDeplacer() {
        return this.deplacementsRestantsParCycle > 0;
    }

    public void reinitialiserDeplacements() {
        this.deplacementsRestantsParCycle = DEPLACEMENTS_MAX_PAR_CYCLE;
    }

    public Coordonne getPosition() {
        return this.position;
    }
}
