public class Coordonne {
    private int x;
    private int y;

    public boolean estAdjacent(Coordonne autre) {
        // Détermine si cette coordonnée est adjacente à une autre
        return Math.abs(this.x - autre.x) <= 1 && Math.abs(this.y - autre.y) <= 1;
    }

    // Getters et Setters
}
