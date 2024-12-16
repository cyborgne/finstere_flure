import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Plateau {
    private final int largeur; // Nombre de colonnes
    private final int hauteur; // Nombre de lignes
    private final String[][] grille;
    private final List<Pion> pions;
    private final List<Coordonne> casesInterdites;
    public Coordonne positionMonstre; // Position actuelle du monstre
    private Coordonne positionSortie;  // Position de la sortie (fixée au coin haut gauche)
    private final Map<Coordonne, Coordonne> teleportation; // Associe une sortie à une entrée correspondante

    // Constructeur
    public Plateau() {
        this.largeur = 16; // 16 colonnes (de 0 à 15)
        this.hauteur = 11; // 11 lignes (de 0 à 10)
        this.grille = new String[hauteur][largeur];
        this.pions = new ArrayList<>();
        this.casesInterdites = new ArrayList<>();
        this.positionMonstre = null; // Par défaut, le monstre n'a pas de position initiale
        this.positionSortie = new Coordonne(0, 0);
        this.teleportation = new HashMap<>(); // Initialisation du mécanisme de téléportation
        initialiserCasesInterdites();
        initialiserMursDeSortie(); // Initialiser les bords de sortie/entrée
    }

    private void initialiserMursDeSortie() {
        // Haut ↔ Bas
        for (int x = 0; x < largeur; x++) {
            teleportation.put(new Coordonne(x, 0), new Coordonne(x, hauteur - 1)); // Haut → Bas
            teleportation.put(new Coordonne(x, hauteur - 1), new Coordonne(x, 0)); // Bas → Haut
        }

        // Gauche ↔ Droite
        for (int y = 0; y < hauteur; y++) {
            teleportation.put(new Coordonne(0, y), new Coordonne(largeur - 1, y)); // Gauche → Droite
            teleportation.put(new Coordonne(largeur - 1, y), new Coordonne(0, y)); // Droite → Gauche
        }
    }

    // Initialise les cases interdites
    private void initialiserCasesInterdites() {
        // Coin en haut à droite
        this.casesInterdites.add(new Coordonne(12, 0));
        this.casesInterdites.add(new Coordonne(13, 0));
        this.casesInterdites.add(new Coordonne(14, 0));
        this.casesInterdites.add(new Coordonne(15, 0));
        this.casesInterdites.add(new Coordonne(13, 1));
        this.casesInterdites.add(new Coordonne(14, 1));
        this.casesInterdites.add(new Coordonne(15, 1));
        this.casesInterdites.add(new Coordonne(14, 2));
        this.casesInterdites.add(new Coordonne(15, 2));
        this.casesInterdites.add(new Coordonne(15, 3));

        // Coin en bas à gauche
        this.casesInterdites.add(new Coordonne(0, 7));
        this.casesInterdites.add(new Coordonne(0, 8));
        this.casesInterdites.add(new Coordonne(1, 8));
        this.casesInterdites.add(new Coordonne(0, 9));
        this.casesInterdites.add(new Coordonne(1, 9));
        this.casesInterdites.add(new Coordonne(2, 9));
        this.casesInterdites.add(new Coordonne(0, 10));
        this.casesInterdites.add(new Coordonne(1, 10));
        this.casesInterdites.add(new Coordonne(2, 10));
        this.casesInterdites.add(new Coordonne(3, 10));
    }

    public void reinitialiserRetournementsPions() {
        for (Pion pion : this.pions) {
            pion.reinitialiserRetournement(); // Devrait réinitialiser `retournementsRestants`.
        }
    }

    // Vérifie si une position est éligible pour la téléportation
    public boolean estMurDeSortie(Coordonne coord) {
        return teleportation.containsKey(coord);
    }

    // Retourne la position correspondante après traversée
    public Coordonne getCoordonneTeleportation(Coordonne coord) {
        return teleportation.get(coord);
    }

    // Initialise le plateau avec des cases vides, interdites, pions et monstre
    public void initialiser() {
        for (int i = 0; i < this.hauteur; i++) {
            for (int j = 0; j < this.largeur; j++) {
                this.grille[i][j] = "."; // Une case vide par défaut
            }
        }

        // Ajouter les cases interdites
        for (Coordonne caseInterdite : this.casesInterdites) {
            this.grille[caseInterdite.getY()][caseInterdite.getX()] = "X";
        }

        // Ajouter la sortie
        if (this.positionSortie != null) {
            this.grille[this.positionSortie.getY()][this.positionSortie.getX()] = "S";
        }

        // Ajouter le monstre, s'il existe
        if (this.positionMonstre != null) {
            this.grille[this.positionMonstre.getY()][this.positionMonstre.getX()] = "M";
        }

        // Ajouter les pions, s'il y en a
        for (Pion pion : this.pions) {
            Coordonne pos = pion.getPosition();
            if (pos != null) {
                this.grille[pos.getY()][pos.getX()] = "P";
            }
        }
    }

    public boolean estCoordonneValide(Coordonne coord) {
        if (coord == null) {
            System.out.println("[Erreur] Les coordonnées ne peuvent pas être nulles !");
            return false;
        }

        // Cas particulier : Position d'entrée
        if (coord.getX() == 16 && coord.getY() == 11) {
            return true;
        }

        // Validation générale
        if (coord.getX() < 0 || coord.getX() >= this.largeur || coord.getY() < 0 || coord.getY() >= this.hauteur) {
            return false;
        }
        return true;
    }

    // Ajoute un pion au plateau
    public void ajouterPion(Pion pion) {
        if (pion.getPosition() == null) {
            throw new IllegalArgumentException("Un pion avec une position null ne peut pas être ajouté.");
        }
        this.pions.add(pion);
    }

    public boolean estPositionValide(Coordonne coord) {
        if (!estCoordonneValide(coord)) {
            return false;
        }
        if (this.casesInterdites.contains(coord)) {
            return false;
        }
        return true;
    }

    public boolean contientPion(Coordonne position) {
        for (Pion pion : this.pions) {
            if (pion.getPosition() != null && pion.getPosition().equals(position) && !pion.estSorti()) {
                return true; // Un pion est visible sur cette case
            }
        }
        return false;
    }

    // Vérifie si la position est valide ET contient une cible
    public boolean estPositionValideAvecCible(Coordonne position) {
        if (!estPositionValide(position)) {
            return false; // Vérifie les limites et les contraintes des cases
        }

        return contientCible(position); // Vérifie spécifiquement la présence d'une cible
    }

    // Vérifie si une cible existante (comme un pion) est présente sur une position donnée
    private boolean contientCible(Coordonne coord) {
        for (Pion pion : this.pions) {
            if (pion.getPosition() != null && pion.getPosition().equals(coord)) {
                return true; // Un pion se trouve sur la position donnée
            }
        }
        return false; // Aucun pion ne se trouve sur cette position
    }

    // Met à jour la grille pour refléter les nouvelles positions des éléments
    public void mettreAJour() {
        for (int i = 0; i < this.hauteur; i++) {
            for (int j = 0; j < this.largeur; j++) {
                this.grille[i][j] = "."; // Réinitialise avec des cases vides
            }
        }

        // Ajouter les cases interdites
        for (Coordonne caseInterdite : this.casesInterdites) {
            this.grille[caseInterdite.getY()][caseInterdite.getX()] = "X";
        }

        // Ajouter la sortie, si elle existe
        if (this.positionSortie != null) {
            this.grille[this.positionSortie.getY()][this.positionSortie.getX()] = "S";
        }

        // Placer les pions sur le plateau
        for (Pion pion : this.pions) {
            Coordonne pos = pion.getPosition();
            if (pos != null && !pion.estSorti()) { // Ne pas afficher les pions sortis
                this.grille[pos.getY()][pos.getX()] = pion.getId();
            }
        }

        // Placer le monstre
        if (this.positionMonstre != null) {
            this.grille[this.positionMonstre.getY()][this.positionMonstre.getX()] = "M"; // Assurez-vous que "M" est bien affiché
        }
    }

    // Affiche le plateau actuel dans la console
    public void afficher() {
        for (int i = 0; i < this.hauteur; i++) {
            for (int j = 0; j < this.largeur; j++) {
                // Vérifiez si une case est null et remplacez par "."
                if (this.grille[i][j] == null) {
                    this.grille[i][j] = ".";
                }
                System.out.print(this.grille[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Vérifie si un pion peut s'arrêter sur une case
    public boolean estOccupationValide(Coordonne coord, Pion pionEnDeplacement) {
        if (!estPositionValide(coord)) { // Vérifie les limites et les cases interdites
            return false;
        }

        // Vérifie que la case n'est pas occupée par un autre pion
        for (Pion pion : this.pions) {
            if (!pion.equals(pionEnDeplacement) && pion.getPosition() != null) {
                if (pion.getPosition().equals(coord)) {
                    return false; // Case occupée par un autre pion
                }
            }
        }

        // Vérifie que la case n'est pas occupée par le monstre
        if (this.positionMonstre != null && coord.equals(this.positionMonstre)) {
            return false; // Case occupée par le monstre
        }

        return true; // Toutes les vérifications passées, la case est libre pour le pion
    }


    // Définit ou change la position initiale du monstre
    public void setPositionMonstre(Coordonne position) {
        // Vérifie si la nouvelle position est valide
        if (!estPositionValide(position)) {
            System.out.println("[Erreur] Tentative de positionner le monstre hors plateau : " + position);
            return;
        }

        // Mettre à jour la position actuelle du monstre
        this.positionMonstre = position;

        // Appeler la mise à jour de la grille automatiquement
        mettreAJour();
        System.out.println("Le monstre est positionné à : " + position);
    }

    public void retournerTousLesPions() {
        for (Pion pion : this.pions) {
            if (pion.getPosition() != null && !pion.estSorti()) { // Ignore les pions déjà sortis
                pion.retournerPion(); // Retourne la face visible du pion
                System.out.println("Le pion " + pion.getId() + " a retourné sa face. Nouvelle face visible : " + pion.getValeurFaceA());
            }
        }
        // Mettre à jour le plateau après les modifications
        mettreAJour();
    }

    public Pion getPionSurCase(Coordonne coord) {
        for (Pion pion : this.pions) {
            if (pion.getPosition() != null && pion.getPosition().equals(coord) && !pion.estSorti()) {
                return pion; // Retourne le pion trouvé sur la case
            }
        }
        return null; // Aucun pion trouvé
    }
    public void retirerPion(Pion pion) {
        if (pion != null) {
            pion.sortir(); // Marque le pion comme sorti
            pion.setPosition(null); // Retire le pion du plateau (sa position devient null)
            System.out.println("[Info] Le pion " + pion.getId() + " a été retiré du plateau définitivement.");
        }
    }

    public void retirerPionTemporairement(Pion pion) {
        if (pion != null) {
            pion.sortirTemporairement(); // Marquer le pion comme mangé temporairement
            pion.setPosition(null);      // Enlever sa position sur le plateau

            System.out.println("[INFO] Le pion " + pion.getId() + " a été mangé temporairement. Il est maintenant hors plateau.");
        }
    }

    public void afficherPionsHorsPlateau(Joueur joueur) {
        List<Pion> pionsHorsPlateau = joueur.getPions().stream()
                .filter(Pion::peutRevenirEnJeu) // Filtrer uniquement les pions temporairement sortis
                .toList();

        if (pionsHorsPlateau.isEmpty()) {
            System.out.println("[INFO] Aucun pion n'est actuellement hors plateau.");
            return;
        }

        System.out.println("[INFO] Pions actuellement hors plateau :");
        for (Pion pion : pionsHorsPlateau) {
            System.out.println("- Pion " + pion.getId());
        }
    }

    // Retourne une liste de pions actuels sur le plateau
    public List<Pion> getPions() {
        return this.pions;
    }
    public Coordonne getPositionSortie() {
        return this.positionSortie;
    }

    public int getLargeur() {
        return this.largeur;
    }

    public int getHauteur() {
        return this.hauteur;
    }
}
