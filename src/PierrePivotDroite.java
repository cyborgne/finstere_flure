public class PierrePivotDroite extends ElementDecor {

    public PierrePivotDroite(Coordonne position) {
        super(position);
    }

    @Override
    public void interagir(Monstre monstre) {
        // Logique pour l'interaction avec le monstre
        // Par exemple, forcer le monstre à tourner à droite
        monstre.changerDirection();
        System.out.println("Le monstre tourne à droite grâce à la PierrePivotDroite !");
    }
}
