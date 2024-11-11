public class FlaqueHemoglobine extends ElementDecor {

    public FlaqueHemoglobine(Coordonne position) {
        super(position);
    }

    @Override
    public void interagir(Monstre monstre) {
        // Logique pour l'interaction avec le monstre
        // Par exemple, la flaque pourrait changer le comportement du monstre
        System.out.println("Le monstre devient plus agressif en marchant sur une FlaqueHemoglobine !");
    }
}
