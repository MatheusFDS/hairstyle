package com.fiap.hairstyle.dominio.portas.saida;

public interface GoogleCalendarPort {
    boolean isEnabled();
    String criarEvento(String titulo, String descricao, java.time.LocalDateTime inicio, java.time.LocalDateTime fim);
    void deletarEvento(String eventId);
}
