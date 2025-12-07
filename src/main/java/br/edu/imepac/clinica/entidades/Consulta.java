package br.edu.imepac.clinica.entidades;

import java.time.LocalDateTime;

public class Consulta {
    private Long id;
    private Medico medico;
    private Pessoa paciente;
    private Convenio convenio;
    private Secretaria secretaria; // Who scheduled
    private LocalDateTime dataHora;
    private boolean isRetorno;
    private String status; // AGENDADA, REALIZADA, CANCELADA
    private String motivo;

    public Consulta() {
    }

    public Consulta(Long id, Medico medico, Pessoa paciente, Convenio convenio, Secretaria secretaria,
            LocalDateTime dataHora, boolean isRetorno, String status, String motivo) {
        this.id = id;
        this.medico = medico;
        this.paciente = paciente;
        this.convenio = convenio;
        this.secretaria = secretaria;
        this.dataHora = dataHora;
        this.isRetorno = isRetorno;
        this.status = status;
        this.motivo = motivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Pessoa getPaciente() {
        return paciente;
    }

    public void setPaciente(Pessoa paciente) {
        this.paciente = paciente;
    }

    public Convenio getConvenio() {
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }

    public Secretaria getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(Secretaria secretaria) {
        this.secretaria = secretaria;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public boolean isRetorno() {
        return isRetorno;
    }

    public void setRetorno(boolean isRetorno) {
        this.isRetorno = isRetorno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
