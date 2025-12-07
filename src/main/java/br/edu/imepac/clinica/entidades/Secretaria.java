package br.edu.imepac.clinica.entidades;

public class Secretaria extends Pessoa {
    private String pis;

    public Secretaria() {
    }

    public Secretaria(Long id, String nome, String telefone, String email, String pis) {
        super(id, nome, telefone, email);
        this.pis = pis;
    }

    public String getPis() {
        return pis;
    }

    public void setPis(String pis) {
        this.pis = pis;
    }
}
