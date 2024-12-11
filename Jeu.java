import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Jeu {
    private final Plateau plateau; // Plateau du jeu
    private final List<Joueur> joueurs; // Liste des joueurs
    private final Monstre monstre; // Instance du monstre
    private final Scanner scanner; // Scanner pour les entrées utilisateur

    public Jeu() {
        this.plateau = new Plateau();
        plateau.initialiser();
        this.joueurs = new ArrayList<>();
        this.monstre = new Monstre(); // Position initiale du monstre
        this.scanner = new Scanner(System.in);
    }

    public void lancer() {
        System.out.println("Bienvenue dans Finstere Flure !");
        initialiserJoueurs();

        boolean partieEnCours = true;
        boolean monstreActif = false;

        while (partieEnCours) {
            // === Phase Joueur ===
            System.out.println("\n=== Phase Joueur ===");

            for (Joueur joueur : joueurs) {
                System.out.println("--- Tour de " + joueur.getNom() + " ---");
                tourJoueur(joueur);

                // Vérifie si la partie est terminée
                if (verifierFin()) {
                    partieEnCours = false;
                    break;
                }
            }

            // === Activation du Monstre ===
            // Vérifiez si TOUS les pions ont été déplacés 2 fois avant que le monstre ne bouge
            if (!monstreActif && tousLesPionsOntEteDeplacesDeuxFois()) {
                monstreActif = true;
                System.out.println("\n***** Le monstre entre en jeu ! *****");

                // Placez le monstre sur la sortie pour son premier tour
                monstre.premierDeplacement(plateau.positionSortie);
                plateau.setPositionMonstre(monstre.getPosition());
                plateau.afficher();
            }

            // === Phase Monstre ===
            if (monstreActif && tousLesPionsOntEteDeplacesDeuxFois()) {
                System.out.println("\n=== Phase Monstre ===");
                phaseMonstre();

                // Après le déplacement du monstre, réinitialisez les pions pour un nouveau cycle
                reinitialiserPions();
            }
        }

        System.out.println("La partie est terminée !");
    }

    // Trouver la cible la plus proche pour le monstre
    private Coordonne trouverCibleProche(Plateau plateau) {
        List<Pion> pions = plateau.getPions();
        Coordonne positionMonstre = plateau.positionMonstre;

        Coordonne cibleLaPlusProche = null;
        double distanceLaPlusProche = Double.MAX_VALUE;

        for (Pion pion : pions) {
            if (!pion.estSorti()) { // Vérifie que le pion n'est pas déjà sorti
                Coordonne positionPion = pion.getPosition();

                // Calcul de la distance entre le monstre et ce pion
                double distance = Math.sqrt(Math.pow(positionMonstre.getX() - positionPion.getX(), 2) +
                        Math.pow(positionMonstre.getY() - positionPion.getY(), 2));

                // Mettre à jour la cible si une distance plus courte est trouvée
                if (distance < distanceLaPlusProche) {
                    distanceLaPlusProche = distance;
                    cibleLaPlusProche = positionPion;
                }
            }
        }

        return cibleLaPlusProche; // Retourne la position de la cible la plus proche
    }

    private void reinitialiserPions() {
        for (Joueur joueur : joueurs) {
            for (Pion pion : joueur.getPions()) {
                if (!pion.estSorti()) { // Ne réinitialisez pas les pions ayant quitté la partie
                    pion.reinitialiserDeplacements(); // Remet les déplacements restants à leur maximum (basé sur valeurFaceA)
                    pion.setDeplacementsEffectuesAvantMonstre(0); // IMPORTANT : Permet de recommencer le cycle de déplacements
                    pion.reinitialiserRetournements(); // Fixe les retournements restants à 2 par phase joueur
                }
            }
        }
        System.out.println("Tous les pions ont été réinitialisés. Début d'un nouveau cycle !");
    }

    private boolean tousLesPionsOntEteDeplacesDeuxFois() {
        for (Joueur joueur : joueurs) { // Parcourir tous les joueurs
            for (Pion pion : joueur.getPions()) { // Parcourir les pions de chaque joueur
                // Vérifier que tous les pions actifs (non sortis) ont bien respecté la limite de 2 déplacements
                if (!pion.estSorti() && pion.getDeplacementsEffectuesAvantMonstre() < 2) {
                    return false;
                }
            }
        }
        return true; // Tous les pions encore sur le plateau ont été déplacés 2 fois
    }

    private void phaseJoueur() {
        System.out.println("\n=== Phase Joueur ===");
        for (Joueur joueur : joueurs) {
            System.out.println("--- Tour de " + joueur.getNom() + " ---");
            tourJoueur(joueur);

            // Vérifie si la partie peut se terminer après cette action
            if (verifierFin()) {
                System.out.println("Un joueur a atteint la condition de victoire !");
                return;
            }
        }
    }

    private void phaseMonstre() {
        System.out.println("Le monstre se déplace");
    }

    private void initialiserJoueurs() {
        int nombreDeJoueurs;
        char[] symboles = {'A', 'B', 'C', 'D'}; // Symboles pour les joueurs

        // Demande du nombre de joueurs avec validation
        do {
            System.out.print("Combien de joueurs (2-4) ? ");
            while (!scanner.hasNextInt()) {
                System.out.println("Veuillez entrer un entier valide !");
                scanner.next(); // Consommer l'entrée invalide
            }
            nombreDeJoueurs = scanner.nextInt();
            if (nombreDeJoueurs < 2 || nombreDeJoueurs > 4) {
                System.out.println("Veuillez entrer un nombre de joueurs valide entre 2 et 4 !");
            }
        } while (nombreDeJoueurs < 2 || nombreDeJoueurs > 4);

        // Initialisation des joueurs avec 3 pions chacun
        for (int i = 0; i < nombreDeJoueurs; i++) {
            String nomJoueur = Character.toString(symboles[i]); // Nom et symbole du joueur
            Joueur joueur = new Joueur(nomJoueur, nomJoueur);

            // Création des 3 pions pour chaque joueur
            joueur.ajouterPion(new Pion(joueur, nomJoueur + "1", null, 1, 6));
            joueur.ajouterPion(new Pion(joueur, nomJoueur + "2", null, 2, 5));
            joueur.ajouterPion(new Pion(joueur, nomJoueur + "3", null, 3, 4));

            // Ajout du joueur à la liste des joueurs
            joueurs.add(joueur);

            System.out.println("Le joueur " + nomJoueur + " a été initialisé avec ses pions (" +
                    nomJoueur + "1, " + nomJoueur + "2, " + nomJoueur + "3).");
        }
    }

    private void tourJoueur(Joueur joueur) {
        System.out.println(joueur.getNom() + ", c'est votre tour !");
        plateau.afficher();

        // Affiche l'état des pions du joueur
        while (true) { // Boucle jusqu'à ce qu'un déplacement valide soit effectué
            System.out.println("Voici vos pions, choisissez-en un à déplacer :");
            for (int i = 0; i < joueur.getPions().size(); i++) {
                Pion pion = joueur.getPions().get(i);
                if (pion.getPosition() == null) {
                    System.out.println((i + 1) + " - " + pion.getId() + " (hors plateau, faces : " +
                            pion.getValeurFaceA() + "-" + pion.getValeurFaceB() + ")");
                } else {
                    System.out.println((i + 1) + " - " + pion.getId() + " (position : " +
                            pion.getPosition() + ", face visible : " + pion.getValeurFaceA() + ")");
                }
            }

            // Demande le pion à déplacer
            System.out.println("Entrez le numéro du pion à déplacer :");
            int choix = -1;

            while (!scanner.hasNextInt()) {
                System.out.println("Entrée invalide. Veuillez entrer un numéro valide.");
                scanner.next(); // Ignorez l'entrée invalide
            }
            choix = scanner.nextInt() - 1;

            if (choix >= 0 && choix < joueur.getPions().size()) {
                Pion pionChoisi = joueur.getPions().get(choix);

                // Vérifier si le pion peut encore être déplacé avant l'arrivée du monstre
                if (!pionChoisi.peutEncoreDeplacerAvantMonstre()) {
                    System.out.println("[Erreur] Le pion " + pionChoisi.getId() +
                            " ne peut plus être déplacé avant l'arrivée du monstre !");
                    continue; // Recommencer la boucle pour permettre de choisir un autre pion
                }

                if (pionChoisi.getPosition() == null) {
                    // Entrée sur le plateau et déplacement
                    entrerPuisDeplacerPion(joueur, pionChoisi);
                } else {
                    // Déplacement dynamique du pion
                    deplacerPionDynamique(pionChoisi, pionChoisi.getValeurFaceA());
                }

                // Incrémenter le compteur des déplacements avant l'arrivée du monstre
                pionChoisi.incrementerDeplacementsAvantMonstre();

                // Toujours retourner la face visible automatiquement en fin de tour
                pionChoisi.retournerPion();
                plateau.mettreAJour();
                System.out.println("Fin du tour, la face visible du pion choisi a changé.");
                // Sort de la boucle après un déplacement valide
                break;
            } else {
                System.out.println("Choix invalide. Veuillez entrer un numéro correspondant à un pion existant.");
            }
        }
    }

    // Déplacement dynamique du pion
    private void deplacerPionDynamique(Pion pion, int deplacementsRestants) {
        System.out.println("Le pion " + pion.getId() + " commence avec un maximum de " + deplacementsRestants + " déplacements.");

        while (deplacementsRestants > 0) {
            System.out.print("Pion actuellement à " + pion.getPosition() +
                    ". Entrez la direction (haut, bas, gauche, droite) ou tapez 'stop' pour arrêter : ");
            String direction = scanner.next().toLowerCase();

            // Vérifie si le joueur décide de s'arrêter
            if (direction.equals("stop")) {
                // Vérification si le pion peut s'arrêter sur cette case finale
                if (plateau.estOccupationValide(pion.getPosition(), pion)) {
                    System.out.println("Vous avez choisi de vous arrêter en " + pion.getPosition() + ".");
                    break; // Sortir de la boucle et terminer le déplacement
                } else {
                    System.out.println("[Erreur] Vous ne pouvez pas vous arrêter sur la case " +
                            pion.getPosition() + " car elle est déjà occupée !");
                    continue; // Retour au choix de déplacement
                }
            }

            Coordonne nouvellePosition = null;
            switch (direction) {
                case "haut":
                    nouvellePosition = new Coordonne(pion.getPosition().getX(), pion.getPosition().getY() - 1);
                    break;
                case "bas":
                    nouvellePosition = new Coordonne(pion.getPosition().getX(), pion.getPosition().getY() + 1);
                    break;
                case "gauche":
                    nouvellePosition = new Coordonne(pion.getPosition().getX() - 1, pion.getPosition().getY());
                    break;
                case "droite":
                    nouvellePosition = new Coordonne(pion.getPosition().getX() + 1, pion.getPosition().getY());
                    break;
                default:
                    System.out.println("[Erreur] Direction non valide. Essayez encore.");
                    continue; // Réessaie une direction
            }

            // Vérifie si la position est valide pour le déplacement
            if (plateau.estPositionValide(nouvellePosition)) {
                if (deplacementsRestants > 1 || plateau.estOccupationValide(nouvellePosition, pion)) {
                    // Passage ou arrêt autorisé (si la case est libre ou mouvement temporaire)
                    pion.setPosition(nouvellePosition);
                    deplacementsRestants--;
                    System.out.println("Pion déplacé à " + nouvellePosition + ". Déplacements restants : " + deplacementsRestants);

                    // Afficher le plateau après chaque déplacement
                    plateau.mettreAJour();
                    plateau.afficher();
                } else {
                    // Tentative d'arrêt sur une case occupée
                    System.out.println("[Erreur] Vous ne pouvez pas vous arrêter sur la case " + nouvellePosition + " car elle est déjà occupée !");
                }
            } else {
                System.out.println("[Erreur] La position " + nouvellePosition + " est invalide.");
            }
        }

        if (deplacementsRestants > 0) {
            System.out.println("Déplacement arrêté avant d'atteindre le maximum.");
        } else {
            System.out.println("Le pion " + pion.getId() + " a terminé tous ses déplacements.");
        }
    }

    // Entrée sur le plateau d'un pion
    // Entrée sur le plateau d'un pion
    private void entrerPuisDeplacerPion(Joueur joueur, Pion pion) {
        // Position d'entrée du plateau
        Coordonne entree = new Coordonne(15, 10);

        // Vérifier si la position d'entrée est disponible
        if (!plateau.estPositionValide(entree)) {
            System.out.println("[Erreur] La position d'entrée est déjà occupée ou invalide !");
            return;
        }

        // Placer le pion sur la position d'entrée
        pion.setPosition(entree);
        plateau.ajouterPion(pion); // Ajoute le pion au plateau

        // Mise à jour et affichage immédiats du plateau
        plateau.mettreAJour();
        plateau.afficher();

        System.out.println("Le pion " + pion.getId() + " est entré en " + entree + ".");

        // Décompte du premier déplacement à l'entrée
        int deplacementsRestants = pion.getValeurFaceA() - 1;

        if (deplacementsRestants > 0) {
            System.out.println("Il reste " + deplacementsRestants + " déplacement(s) pour le pion " + pion.getId() + ".");

            // Effectuer les déplacements restants dynamiquement
            deplacerPionDynamique(pion, deplacementsRestants);
        } else {
            System.out.println("Le pion n'a plus de déplacements restants après l'entrée.");
        }

        // L'appel à `retournerPion` a été supprimé d'ici
    }

    // Vérifie si la partie doit se terminer
    private boolean verifierFin() {
        boolean tousSortis = joueurs.stream()
                .allMatch(joueur -> joueur.getPions().stream().allMatch(Pion::estSorti));

        if (tousSortis) {
            System.out.println("Tous les pions sont sortis ! Félicitations !");
            return true;
        }

        boolean aucunPionRestant = joueurs.stream()
                .allMatch(joueur -> joueur.getPions().isEmpty());
        if (aucunPionRestant) {
            System.out.println("Tous les pions ont été capturés. Vous avez perdu !");
            return true;
        }

        return false; // Continuer le jeu
    }
}