import java.util.ArrayList;
import java.util.List;

public class Joueur {
    private final String nom; // Nom du joueur (ex : "A", "B", "C", "D")
    private final String symbole; // Symbole utilisé pour identifier le joueur (unique)
    private final List<Pion> pions; // Liste des pions appartenant au joueur
    private int actionsEffectuees; // Suivi du nombre d'actions effectuées par ce joueur (joué par ses pions)

    // Constructeur
    public Joueur(String nom, String symbole) {
        this.nom = nom;
        this.symbole = symbole;
        this.pions = new ArrayList<>();
        this.actionsEffectuees = 0; // Aucun pion n'a été utilisé au départ
    }

    // Getters
    public String getNom() {
        return nom;
    }

    public List<Pion> getPions() {
        return pions;
    }

    public String getSymbole() {
        return symbole;
    }

    // Méthode pour ajouter un pion au joueur
    public void ajouterPion(Pion pion) {
        this.pions.add(pion);
    }

    // Méthode appelée lorsque le joueur joue un de ses pions
    public void incrementerActions() {
        this.actionsEffectuees++;
    }

    // Retourne si tous les pions du joueur ont été joués deux fois
    public boolean aUtiliseTousSesPionsDeuxFois() {
        return actionsEffectuees >= pions.size() * 2;
    }

    @Override
    public String toString() {
        return nom + " (" + symbole + ") avec " + pions.size() + " pions.";
    }
}