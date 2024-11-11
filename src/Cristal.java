public class Cristal extends ElementDecor {

    public Cristal(Coordonne position) {
        super(position);
    }

    @Override
    public void interagir(Monstre monstre) {
        // Désoriente le monstre (par exemple, lui fait faire demi-tour)
        monstre.changerDirection();
        System.out.println("Le monstre est désorienté par le Cristal et fait demi-tour !");
    }
}
