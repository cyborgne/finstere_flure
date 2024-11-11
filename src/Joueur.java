public class Joueur {
    private String nom;
    private int nombreDePion;

    public Joueur(String nom) {
        this.nom = nom;
        this.nombreDePion = 0; // Valeur par défaut, peut être définie plus tard si nécessaire
    }

    public void passerTour() {
        // Logique pour passer le tour
    }

    public void sortiePion() {
        // Logique pour la sortie d'un pion
    }

    // Getters et Setters

    public int getNombreDePion() {
        return nombreDePion;
    }

    public void setNombreDePion(int nombreDePion) {
        this.nombreDePion = nombreDePion;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
