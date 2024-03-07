package main;

public class Persona {
    private String nome;

    public Persona(String nome) {
        this.nome = nome;
    }

    public String getName(){
        return this.nome;
    }

    public void setName(String name){
        this.nome = name;
    }

}
