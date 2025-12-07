package br.edu.imepac.clinica.entidades;

import java.util.Set;

public class Perfil {
    private Long id;
    private String nome;
    private Set<EnumFuncionalidades> funcionalidades;

    public Perfil() {
    }

    public Perfil(Long id, String nome, Set<EnumFuncionalidades> funcionalidades) {
        this.id = id;
        this.nome = nome;
        this.funcionalidades = funcionalidades;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<EnumFuncionalidades> getFuncionalidades() {
        return funcionalidades;
    }

    public void setFuncionalidades(Set<EnumFuncionalidades> funcionalidades) {
        this.funcionalidades = funcionalidades;
    }
}
