public class BlocPierre extends ElementDecor {

    public BlocPierre(Coordonne position) {
        super(position);
    }

    @Override
    public void interagir(Monstre monstre) {
        // Logique pour l'interaction avec le monstre
        // Par exemple, le monstre ne peut pas avancer s'il rencontre un BlocPierre
        System.out.println("Le monstre est bloqué par un BlocPierre !");
    }
}
