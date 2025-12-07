package br.edu.imepac.clinica.entidades;

public class Usuario {
    private Long id;
    private String login;
    private String senha;
    private EnumStatusUsuario status;
    private Perfil perfil;
    private Pessoa funcionario; // Can be Medico, Secretaria, or just Pessoa

    public Usuario() {
    }

    public Usuario(Long id, String login, String senha, EnumStatusUsuario status, Perfil perfil, Pessoa funcionario) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.status = status;
        this.perfil = perfil;
        this.funcionario = funcionario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public EnumStatusUsuario getStatus() {
        return status;
    }

    public void setStatus(EnumStatusUsuario status) {
        this.status = status;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public Pessoa getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Pessoa funcionario) {
        this.funcionario = funcionario;
    }
}
