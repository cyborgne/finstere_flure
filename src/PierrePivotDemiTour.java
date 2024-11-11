public class PierrePivotDemiTour extends ElementDecor {

    public PierrePivotDemiTour(Coordonne position) {
        super(position);
    }

    @Override
    public void interagir(Monstre monstre) {
        // Logique pour l'interaction avec le monstre
        // Forcer le monstre à faire demi-tour
        monstre.changerDirection();
        System.out.println("Le monstre fait un demi-tour grâce à la PierrePivotDemiTour !");
    }
}
