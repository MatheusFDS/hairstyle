package com.fiap.hairstyle.adaptadores.saida.adpter;

import com.fiap.hairstyle.dominio.portas.saida.GoogleCalendarPort;
import com.fiap.hairstyle.dominio.servico.GoogleCalendarService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GoogleCalendarAdapter implements GoogleCalendarPort {

    private final GoogleCalendarService googleCalendarService;

    public GoogleCalendarAdapter(GoogleCalendarService googleCalendarService) {
        this.googleCalendarService = googleCalendarService;
    }

    @Override
    public boolean isEnabled() {
        return googleCalendarService.isEnabled();
    }

    @Override
    public String criarEvento(String titulo, String descricao, LocalDateTime inicio, LocalDateTime fim) {
        return googleCalendarService.criarEvento(titulo, descricao, inicio, fim);
    }

    @Override
    public void deletarEvento(String eventId) {
        googleCalendarService.deletarEvento(eventId);
    }
}
