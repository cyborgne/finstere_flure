public class Monstre {
    private Coordonne position;
    private String direction; // N, S, E, O, etc.

    public void deplacer(Coordonne nouvellePosition) {
        // Logique de déplacement
    }

    public void devorer(Pion pion) {
        // Logique pour dévorer un pion
    }
    le jeu lalalal

    public void pousser() {
        // Logique pour pousser
    }

    public boolean voirPion(Pion pion) {
        // Vérifie si le monstre peut voir le pion
        return true;
    }

    public void changerDirection() {
        // Change la direction selon des règles spécifiques
    }

    public Coordonne getPosition() {
        return position;
    }

    public void setPosition(Coordonne position) {
        this.position = position;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
