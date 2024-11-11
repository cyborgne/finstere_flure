public class Pion {
    private Coordonne position;
    private int valeur;

    public void retourner() {
        // Logique pour retourner le pion (s'il a une face à changer)
    }

    public void deplacer(Coordonne nouvellePosition) {
        this.position = nouvellePosition;
    }

    public boolean estVisible() {
        // Vérifie si le pion est visible
        return true; // ou une condition spécifique
    }

    public boolean estDevore() {
        // Logique pour déterminer si le pion est dévoré
        return false; // ou une condition spécifique
    }

    // Getters et Setters
}
