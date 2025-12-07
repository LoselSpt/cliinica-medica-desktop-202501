package br.edu.imepac.clinica.entidades;

public class Prontuario {
    private Long idConsulta; // PK/FK
    private String historico;
    private String receituario;
    private String exames;
    private Consulta consulta; // Reference back to consulta if needed

    public Prontuario() {
    }

    public Prontuario(Long idConsulta, String historico, String receituario, String exames) {
        this.idConsulta = idConsulta;
        this.historico = historico;
        this.receituario = receituario;
        this.exames = exames;
    }

    public Long getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(Long idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getReceituario() {
        return receituario;
    }

    public void setReceituario(String receituario) {
        this.receituario = receituario;
    }

    public String getExames() {
        return exames;
    }

    public void setExames(String exames) {
        this.exames = exames;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }
}
