public abstract class ElementDecor {
    protected Coordonne position;

    public ElementDecor(Coordonne position) {
        this.position = position;
    }

    public abstract void interagir(Monstre monstre);

    // Getter
    public Coordonne getPosition() {
        return position;
    }
}
