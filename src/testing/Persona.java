package testing;

public class Persona {
    private String nome;

    public Persona(String nome) {
        this.nome = nome;
    }

    public static void main(String[] args) {
        Persona pippo = new Persona("Carlo");
        System.out.println(pippo.nome);
    }


}
