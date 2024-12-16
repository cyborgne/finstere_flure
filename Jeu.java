import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class Jeu {
    private final Plateau plateau; // Plateau du jeu
    private final List<Joueur> joueurs; // Liste des joueurs
    private final Monstre monstre; // Instance du monstre
    private final Scanner scanner; // Scanner pour les entrées utilisateur
    private final List<TuilePierreTombale> tuilesTombales; // Liste des tuiles tombales.
    private final List<TuilePierreTombale> defausseTuilesTombales; // Liste des tuiles défaussées
    private boolean premiereTuileTiree = false; // Indique si c'est le premier tirage
    public static boolean premiereManche = true;  // Commence avec la première manche active
    public static boolean secondeManche = false; // Activée lors de la transition à la seconde manche

    public Jeu() {
        this.tuilesTombales = initialiserTuilesTombales();
        this.defausseTuilesTombales = new ArrayList<>();
        this.plateau = new Plateau();
        this.plateau.initialiser();
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
            // === Vérification des changements de manche ===
            if (premiereManche && tuilesTombales.size() == 1) { // Avant-dernière tuile jouée
                debuterSecondeManche();
            }

            // === Phase Joueur ===
            System.out.println("\n=== Phase Joueur ===");

            for (Joueur joueur : this.joueurs) {
                System.out.println("--- Tour de " + joueur.getNom() + " ---");
                tourJoueur(joueur);

                // Vérifie si la partie est terminée
                if (verifierFin()) {
                    partieEnCours = false;
                    break;
                }
            }

            // === Activation du monstre ===
            if (!monstreActif && tousLesPionsOntEteDeplacesDeuxFois()) {
                monstreActif = true;
                System.out.println("\n***** Le monstre entre en jeu ! *****");

                this.monstre.premierDeplacement(this.plateau.getPositionSortie(), this.plateau);
                this.plateau.setPositionMonstre(this.monstre.getPosition());
                this.plateau.afficher();
            }

            // === Phase Monstre ===
            if (monstreActif && tousLesPionsOntEteDeplacesDeuxFois()) {
                phaseMonstre();

                // Après le déplacement du monstre, réinitialisez les pions pour un nouveau cycle
                reinitialiserPions();
            }
        }

        System.out.println("La partie est terminée !");
    }

    public void reinitialiserPions() {
        for (Joueur joueur : this.joueurs) {
            for (Pion pion : joueur.getPions()) {
                if (pion.estSortiTemporairement()) {
                    pion.reinitialiserPourRejouer(); // Remet en état les pions pour qu'ils puissent revenir
                    System.out.println("[INFO] Le pion " + pion.getId() + " est prêt à être remis en jeu.");
                } else if (!pion.estSorti()) {
                    pion.reinitialiserFace();
                    pion.reinitialiserDeplacements();
                    pion.reinitialiserRetournement();
                }
            }
        }
    }

    private boolean tousLesPionsOntEteDeplacesDeuxFois() {
        for (Joueur joueur : this.joueurs) { // Parcourir tous les joueurs
            for (Pion pion : joueur.getPions()) { // Parcourir les pions de chaque joueur
                // Vérifier que tous les pions actifs (non sortis) ont bien respecté la limite de 2 déplacements
                if (!pion.estSorti() && pion.getDeplacementsEffectuesAvantMonstre() < 2) {
                    return false;
                }
            }
        }
        return true; // Tous les pions encore sur le plateau ont été déplacés 2 fois
    }

    private void phaseMonstre() {
        System.out.println("\n=== Phase Monstre ===");

        // Tirage aléatoire d'une tuile
        TuilePierreTombale tuile = obtenirTuilePourMonstre();

        // Déplacer le monstre selon la tuile
        this.monstre.deplacerSelonTuile(tuile, this.plateau);
        this.plateau.mettreAJour();
        this.plateau.afficher();


        // Vérifier si des pions sont sur la même case que le monstre
        for (Joueur joueur : this.joueurs) {
            for (Pion pion : joueur.getPions()) {
                if (pion.getPosition() != null && pion.getPosition().equals(this.monstre.getPosition())) {
                    if (Jeu.secondeManche) {
                        pion.sortirDefinitivement(); // Sortie définitive en seconde manche
                        System.out.println("[INFO] Le pion " + pion.getId() + " a été mangé définitivement par le monstre.");
                    } else {
                        pion.sortirTemporairement(); // Sortie temporaire en première manche
                        System.out.println("[INFO] Le pion " + pion.getId() + " a été mangé temporairement par le monstre.");
                    }
                    pion.setPosition(null); // Retire le pion du plateau
                }
            }
        }

        // Après tout mouvement du monstre :
        this.plateau.retournerTousLesPions();
        this.plateau.reinitialiserRetournementsPions();
        this.monstre.reinitialiserDeplacements();

        System.out.println("Les pions peuvent de nouveau être retournés.");
    }

    private void initialiserJoueurs() {
        int nombreDeJoueurs;
        char[] symboles = {'A', 'B', 'C', 'D'}; // Symboles pour les joueurs

        // Demande du nombre de joueurs avec validation
        do {
            System.out.print("Combien de joueurs (2-4) ? ");
            while (!this.scanner.hasNextInt()) {
                System.out.println("Veuillez entrer un entier valide !");
                this.scanner.next(); // Consommer l'entrée invalide
            }
            nombreDeJoueurs = this.scanner.nextInt();
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
            this.joueurs.add(joueur);

            System.out.println("Le joueur " + nomJoueur + " a été initialisé avec ses pions (" +
                    nomJoueur + "1, " + nomJoueur + "2, " + nomJoueur + "3).");
        }
    }

    private void tourJoueur(Joueur joueur) {
        System.out.println(joueur.getNom() + ", c'est votre tour !");
        this.plateau.afficher();

        gererRemiseEnJeu(joueur);

        // Affiche l'état des pions du joueur
        while (true) { // Boucle jusqu'à ce qu'un déplacement valide soit effectué
            System.out.println("Voici vos pions, choisissez-en un à déplacer :");
            for (int i = 0; i < joueur.getPions().size(); i++) {
                Pion pion = joueur.getPions().get(i);

                // Exclusion des pions mangés définitivement
                if (pion.estSorti()) {
                    System.out.println((i + 1) + " - " + pion.getId() + " (mangé définitivement, non jouable)");
                    continue;
                }

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

            while (!this.scanner.hasNextInt()) {
                System.out.println("Entrée invalide. Veuillez entrer un numéro valide.");
                this.scanner.next(); // Ignorez l'entrée invalide
            }
            choix = this.scanner.nextInt() - 1;

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
                this.plateau.mettreAJour();
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
            String direction = this.scanner.next().toLowerCase();

            // Vérifie si le joueur décide de s'arrêter
            if (direction.equals("stop")) {
                // Vérification si le pion peut s'arrêter sur cette case finale
                if (!coordonneOccupeeParMonstre(pion.getPosition()) && this.plateau.estOccupationValide(pion.getPosition(), pion)) {
                    System.out.println("Vous avez choisi de vous arrêter en " + pion.getPosition() + ".");
                    break; // Sortir de la boucle et terminer le déplacement
                } else {
                    System.out.println("[Erreur] Vous ne pouvez pas vous arrêter sur la case " +
                            pion.getPosition() + " car elle est soit occupée par le monstre, soit invalide !");
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

            // Vérifie si la prochaine position est valide
            if (this.plateau.estPositionValide(nouvellePosition)) {
                if (deplacementsRestants > 1 || (!coordonneOccupeeParMonstre(nouvellePosition) && this.plateau.estOccupationValide(nouvellePosition, pion))) {
                    // Si le pion peut se déplacer (ou passer temporairement sur une case valide) mais sans s'arrêter sur le monstre
                    pion.setPosition(nouvellePosition);
                    deplacementsRestants--;
                    System.out.println("Pion déplacé à " + nouvellePosition + ". Déplacements restants : " + deplacementsRestants);

                    // Afficher le plateau après chaque déplacement
                    this.plateau.mettreAJour();
                    this.plateau.afficher();
                } else {
                    System.out.println("[Erreur] Vous ne pouvez pas vous arrêter sur la case " + nouvellePosition + " car elle est soit occupée par le monstre, soit invalide !");
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

    private boolean coordonneOccupeeParMonstre(Coordonne coord) {
        return this.plateau.positionMonstre != null && this.plateau.positionMonstre.equals(coord);
    }

    // Entrée sur le plateau d'un pion
    private void entrerPuisDeplacerPion(Joueur joueur, Pion pion) {
        // Vérifiez si le pion est définitivement sorti
        if (pion.estSorti()) {
            System.out.println("[Erreur] Le pion " + pion.getId() + " a été mangé définitivement et ne peut pas entrer sur le plateau.");
            return; // Interrompt l'entrée du pion
        }

        // Réinitialise l'état du pion avant placement sur le plateau
        pion.reinitialiserEntree();

        // Position d'entrée du plateau
        Coordonne entree = new Coordonne(15, 10);

        // Vérifier si la position d'entrée est disponible
        if (!this.plateau.estPositionValide(entree)) {
            System.out.println("[Erreur] La position d'entrée est déjà occupée ou invalide !");
            return;
        }

        // Placer le pion sur la position d'entrée
        pion.setPosition(entree);
        this.plateau.ajouterPion(pion); // Ajoute le pion au plateau

        // Mise à jour et affichage immédiats du plateau
        this.plateau.mettreAJour();
        this.plateau.afficher();

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
    }

    private void gererRemiseEnJeu(Joueur joueur) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Trouver les pions hors plateau du joueur
            List<Pion> pionsHorsPlateau = joueur.getPions().stream()
                    .filter(Pion::peutRevenirEnJeu)
                    .toList();

            // Si aucun pion hors plateau à remettre
            if (pionsHorsPlateau.isEmpty()) {
                System.out.println("[INFO] Aucun pion hors plateau à remettre en jeu.");
                break;
            }

            // Afficher liste des pions hors plateau
            this.plateau.afficherPionsHorsPlateau(joueur);

            // Demander au joueur s'il veut en remettre un en jeu
            System.out.println("Voulez-vous remettre un pion hors plateau en jeu ? (oui/non)");
            String choix = scanner.nextLine();

            if (!choix.equalsIgnoreCase("oui")) {
                break;
            }

            // Demander l'ID du pion
            System.out.println("Entrez l'ID du pion que vous voulez remettre sur le plateau :");
            String idPion = scanner.nextLine();

            Pion pionChoisi = pionsHorsPlateau.stream()
                    .filter(p -> p.getId().equals(idPion))
                    .findFirst()
                    .orElse(null);

            if (pionChoisi == null) {
                System.out.println("[ERREUR] Aucun pion avec cet ID n'est disponible hors plateau.");
                continue;
            }

            // Réintégrer le pion sur une case valide
            System.out.println("Entrez les coordonnées (x y) où vous voulez placer le pion " + pionChoisi.getId() + " :");
            String[] coords = scanner.nextLine().split(" ");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            Coordonne position = new Coordonne(x, y);

            // Valider la position
            if (this.plateau.estPositionValide(position) && !this.plateau.contientPion(position)) {
                pionChoisi.remettreEnJeu(position);
                this.plateau.ajouterPion(pionChoisi);
                System.out.println("[INFO] Pion " + pionChoisi.getId() + " a été remis en jeu sur " + position);
            } else {
                System.out.println("[ERREUR] La position " + position + " est invalide ou déjà occupée.");
            }
        }
    }

    private List<TuilePierreTombale> initialiserTuilesTombales() {
        List<TuilePierreTombale> tuiles = new ArrayList<>();
        //tuiles.add(new TuilePierreTombale(5, "aucune"));
        //tuiles.add(new TuilePierreTombale(7, "aucune"));
        //tuiles.add(new TuilePierreTombale(7, "aucune"));
        //tuiles.add(new TuilePierreTombale(8, "aucune"));
        tuiles.add(new TuilePierreTombale(8, "aucune"));
        tuiles.add(new TuilePierreTombale(10, "aucune"));
        tuiles.add(new TuilePierreTombale(this.monstre.DEPLACEMENTS_MAX_PAR_CYCLE, "une proie"));
        tuiles.add(new TuilePierreTombale(this.monstre.DEPLACEMENTS_MAX_PAR_CYCLE, "deux proieS"));
        return tuiles;
    }

    private TuilePierreTombale obtenirTuilePourMonstre() {
        if (this.tuilesTombales.isEmpty()) { // Si toutes les tuiles ont été utilisées
            System.out.println("[INFO] Toutes les tuiles ont été tirées. Aucune tuile n'est disponible.");
            return null; // Vous pouvez aussi décider de réinitialiser les tuiles
        }

        if (!premiereTuileTiree) {
            // S'assurer que la première tuile tirée ne contient pas de cible
            for (TuilePierreTombale tuile : this.tuilesTombales) {
                if ("aucune".equals(tuile.getCible())) {
                    this.tuilesTombales.remove(tuile); // Retirer cette tuile de la pioche
                    this.defausseTuilesTombales.add(tuile); // Ajouter à la défausse
                    premiereTuileTiree = true; // Marquer que la première tuile a été tirée
                    return tuile;
                }
            }
        }

        // Tirage aléatoire d'une tuile parmi celles restantes
        Random random = new Random();
        int indexTuile = random.nextInt(this.tuilesTombales.size());
        TuilePierreTombale tuile = this.tuilesTombales.remove(indexTuile);
        this.defausseTuilesTombales.add(tuile);

        return tuile;
    }

    // Vérifie si la partie doit se terminer
    private boolean verifierFin() {
        // Vérifier si tous les pions sont sortis (victoire)
        boolean tousSortis = this.joueurs.stream()
                .allMatch(joueur -> joueur.getPions().stream().allMatch(Pion::estSorti));
        if (tousSortis) {
            System.out.println("[INFO] Tous les pions ont été sortis ! Félicitations, la partie est terminée !");
            if (secondeManche) {
                finSecondeManche(); // Appelle la méthode pour retirer les pions restants
            }
            return true;
        }

        // Vérifier si tous les pions restants ont été mangés (défaite)
        boolean pionsActifs = this.joueurs.stream()
                .anyMatch(joueur -> joueur.getPions().stream().anyMatch(p -> !p.estSorti()));
        if (!pionsActifs) {
            System.out.println("[INFO] Tous les pions ont été capturés par le monstre. La partie est perdue !");
            if (secondeManche) {
                finSecondeManche(); // Même cas : aucun pion actif reste
            }
            return true;
        }

        // Si nous sommes en seconde manche et qu'aucun pion n'est sorti
        if (secondeManche && tuilesTombales.isEmpty()) {
            System.out.println("[INFO] La seconde manche est terminée.");
            finSecondeManche();
            return true; // Partie terminée
        }

        return false;
    }

    private void debuterSecondeManche() {
        System.out.println("\n=== Transition vers la seconde manche ===");
        premiereManche = false;
        secondeManche = true;

        System.out.println("[INFO] Début de la seconde manche.");

        // Tous les pions temporairement sortis sont à présent mangés définitivement
        for (Joueur joueur : this.joueurs) {
            for (Pion pion : joueur.getPions()) {
                if (pion.estSortiTemporairement()) {
                    System.out.println("[INFO] Le pion " + pion.getId() + " est maintenant mangé définitivement lors de la transition vers la seconde manche.");
                    pion.sortirDefinitivement(); // Marquer comme sorti définitivement
                }
            }
        }

        // Réintégrer toutes les tuiles tombales
        tuilesTombales.addAll(defausseTuilesTombales);
        defausseTuilesTombales.clear();

        // Mélange des tuiles pour la seconde manche
        java.util.Collections.shuffle(tuilesTombales);

        System.out.println("[INFO] Les tuiles tombales ont été réintégrées et remélangées. La partie continue avec toutes les tuiles disponibles !");
    }

    private void finSecondeManche() {
        System.out.println("\n=== Fin de la seconde manche ===");
        for (Joueur joueur : this.joueurs) {
            for (Pion pion : joueur.getPions()) {
                // Condition : si le pion n'a pas pu sortir
                if (pion.getPosition() != null) {
                    System.out.println("[INFO] Le pion " + pion.getId() + " n'a pas pu sortir. Il est considéré comme dévoré !");
                    pion.sortir(); // Définit `estSorti = true` (mangé définitivement)
                    pion.setPosition(null); // Retire sa position du plateau
                }
            }
        }
        System.out.println("[INFO] Tous les pions restants ont été retirés du plateau.");
    }
}
