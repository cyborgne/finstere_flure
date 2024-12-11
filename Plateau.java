import java.util.ArrayList;
import java.util.List;

public class Plateau {
    private final int largeur; // Nombre de colonnes
    private final int hauteur; // Nombre de lignes
    private final String[][] grille;
    private final List<Pion> pions;
    private final List<Coordonne> casesInterdites;
    public Coordonne positionMonstre; // Position actuelle du monstre
    public Coordonne positionSortie;  // Position de la sortie (fixée au coin haut gauche)

    // Constructeur
    public Plateau() {
        this.largeur = 16; // 16 colonnes (de 0 à 15)
        this.hauteur = 11; // 11 lignes (de 0 à 10)
        this.grille = new String[hauteur][largeur];
        this.pions = new ArrayList<>();
        this.casesInterdites = new ArrayList<>();
        this.positionMonstre = null; // Par défaut, le monstre n'a pas de position initiale
        this.positionSortie = new Coordonne(0, 0); // La sortie est fixée au coin haut gauche
        initialiserCasesInterdites();
        System.out.println("[DEBUG] Position de sortie définie sur : " + positionSortie);
    }

    // Initialise les cases interdites
    private void initialiserCasesInterdites() {
        // Coin en haut à droite
        casesInterdites.add(new Coordonne(12, 0));
        casesInterdites.add(new Coordonne(13, 0));
        casesInterdites.add(new Coordonne(14, 0));
        casesInterdites.add(new Coordonne(15, 0));
        casesInterdites.add(new Coordonne(13, 1));
        casesInterdites.add(new Coordonne(14, 1));
        casesInterdites.add(new Coordonne(15, 1));
        casesInterdites.add(new Coordonne(14, 2));
        casesInterdites.add(new Coordonne(15, 2));
        casesInterdites.add(new Coordonne(15, 3));

        // Coin en bas à gauche
        casesInterdites.add(new Coordonne(0, 7));
        casesInterdites.add(new Coordonne(0, 8));
        casesInterdites.add(new Coordonne(1, 8));
        casesInterdites.add(new Coordonne(0, 9));
        casesInterdites.add(new Coordonne(1, 9));
        casesInterdites.add(new Coordonne(2, 9));
        casesInterdites.add(new Coordonne(0, 10));
        casesInterdites.add(new Coordonne(1, 10));
        casesInterdites.add(new Coordonne(2, 10));
        casesInterdites.add(new Coordonne(3, 10));
    }

    // Initialise le plateau avec des cases vides, interdites, pions et monstre
    public void initialiser() {
        for (int i = 0; i < hauteur; i++) {
            for (int j = 0; j < largeur; j++) {
                grille[i][j] = "."; // Une case vide par défaut
            }
        }

        // Ajouter les cases interdites
        for (Coordonne caseInterdite : casesInterdites) {
            grille[caseInterdite.getY()][caseInterdite.getX()] = "X";
        }

        // Ajouter la sortie
        if (positionSortie != null) {
            grille[positionSortie.getY()][positionSortie.getX()] = "S";
        }

        // Ajouter le monstre, s'il existe
        if (positionMonstre != null) {
            grille[positionMonstre.getY()][positionMonstre.getX()] = "M";
        }

        // Ajouter les pions, s'il y en a
        for (Pion pion : pions) {
            Coordonne pos = pion.getPosition();
            if (pos != null) {
                grille[pos.getY()][pos.getX()] = "P";
            }
        }
    }

    private boolean estCoordonneValide(Coordonne coord) {
        if (coord == null) {
            System.out.println("[Erreur] Les coordonnées ne peuvent pas être nulles !");
            return false;
        }

        // Cas particulier : Position d'entrée
        if (coord.getX() == 16 && coord.getY() == 11) {
            return true;
        }

        // Validation générale
        if (coord.getX() < 0 || coord.getX() >= largeur || coord.getY() < 0 || coord.getY() >= hauteur) {
            System.out.println("[Erreur] Les coordonnées " + coord + " sont hors limites !");
            return false;
        }
        return true;
    }

    // Ajoute un pion au plateau
    public void ajouterPion(Pion pion) {
        pions.add(pion);
    }

    public boolean estPositionValide(Coordonne coord) {
        if (!estCoordonneValide(coord)) {
            return false; // Déjà hors limites ou erreur
        }

        // Vérifie que la case n'est pas interdite
        for (Coordonne caseInterdite : casesInterdites) {
            if (caseInterdite.equals(coord)) {
                System.out.println("[Erreur] La position " + coord + " est interdite !");
                return false;
            }
        }
        return true; // La position est correcte
    }
    // Met à jour la grille pour refléter les nouvelles positions des éléments
    public void mettreAJour() {
        // Nettoie intégralement la grille
        for (int i = 0; i < hauteur; i++) {
            for (int j = 0; j < largeur; j++) {
                grille[i][j] = "."; // Case vide par défaut
            }
        }

        // Ajout des cases interdites
        for (Coordonne caseInterdite : casesInterdites) {
            grille[caseInterdite.getY()][caseInterdite.getX()] = "X";
        }

        // Ajout de la case de sortie
        if (positionSortie != null) {
            grille[positionSortie.getY()][positionSortie.getX()] = "S";
        }

        // Ajout des pions présents sur le plateau
        for (Pion pion : pions) {
            Coordonne pos = pion.getPosition();
            if (pos != null) {
                grille[pos.getY()][pos.getX()] = pion.getId(); // Place l'identifiant unique du pion (A1, B2, etc.)
            }
        }

        // Ajout du monstre, s'il est sur le plateau
        if (positionMonstre != null) {
            grille[positionMonstre.getY()][positionMonstre.getX()] = "M";
        }
    }

    // Affiche le plateau actuel dans la console
    public void afficher() {
        for (int i = 0; i < hauteur; i++) {
            for (int j = 0; j < largeur; j++) {
                // Vérifiez si une case est null et remplacez par "."
                if (grille[i][j] == null) {
                    grille[i][j] = ".";
                }
                System.out.print(grille[i][j] + " ");
            }
            System.out.println();
        }
    }
    // Vérifie si un pion peut s'arrêter sur une case
    public boolean estOccupationValide(Coordonne coord, Pion pionEnDeplacement) {
        for (Pion pion : pions) {
            if (pion.getPosition() != null // Vérifie que le pion est bien sur le plateau
                    && pion.getPosition().equals(coord) // Vérifie qu'il occupe bien la case
                    && !pion.equals(pionEnDeplacement)) { // Ce n'est pas le pion en déplacement
                return false; // La case est occupée, arrêt interdit
            }
        }
        return true; // La case est libre pour un arrêt
    }

    // Définit ou change la position initiale du monstre
    public void setPositionMonstre(Coordonne position) {
        this.positionMonstre = position;
        mettreAJour();
    }

    // Retourne une liste de pions actuels sur le plateau
    public List<Pion> getPions() {
        return pions;
    }
}