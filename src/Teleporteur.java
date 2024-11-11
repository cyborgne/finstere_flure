public class Teleporteur extends ElementDecor {

    public Teleporteur(Coordonne position) {
        super(position);
    }

    @Override
    public void interagir(Monstre monstre) {
        // Logique pour l'interaction avec le monstre
        // Téléportation vers une autre position (exemple de nouvelle position aléatoire)
        Coordonne nouvellePosition = new Coordonne();
        monstre.deplacer(nouvellePosition);
        System.out.println("Le monstre est téléporté à une nouvelle position !");
    }
}
