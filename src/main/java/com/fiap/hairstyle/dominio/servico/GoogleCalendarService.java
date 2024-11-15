package com.fiap.hairstyle.dominio.servico;

import com.fiap.hairstyle.configuracao.GoogleCalendarConfig;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

@Service
public class GoogleCalendarService {

    @Autowired
    private GoogleCalendarConfig googleCalendarConfig;

    private boolean enabled;

    public GoogleCalendarService() {
        this.enabled = initializeCalendarService();
    }

    private boolean initializeCalendarService() {
        try {
            googleCalendarConfig.getCalendarService(); // Tenta inicializar o serviço
            return true;
        } catch (Exception e) { // Captura qualquer exceção inesperada
            return false;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String criarEvento(String resumo, String descricao, LocalDateTime inicio, LocalDateTime fim) {
        if (!isEnabled()) {
            throw new IllegalStateException("Google Calendar não está configurado ou habilitado.");
        }

        try {
            Calendar service = googleCalendarConfig.getCalendarService();

            Event evento = new Event()
                    .setSummary(resumo)
                    .setDescription(descricao);

            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(inicio.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                    .setTimeZone(TimeZone.getDefault().getID());

            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(fim.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                    .setTimeZone(TimeZone.getDefault().getID());

            evento.setStart(start);
            evento.setEnd(end);

            Event createdEvent = service.events().insert("primary", evento).execute();
            return createdEvent.getId();

        } catch (Exception e) {
            throw new IllegalStateException("Erro ao criar evento no Google Calendar.", e);
        }
    }

    public void atualizarEvento(String eventId, String resumo, LocalDateTime inicio, LocalDateTime fim) {
        if (!isEnabled()) {
            throw new IllegalStateException("Google Calendar não está configurado ou habilitado.");
        }

        try {
            Calendar service = googleCalendarConfig.getCalendarService();
            Event evento = service.events().get("primary", eventId).execute();

            evento.setSummary(resumo);

            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(inicio.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                    .setTimeZone(TimeZone.getDefault().getID());

            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(fim.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                    .setTimeZone(TimeZone.getDefault().getID());

            evento.setStart(start);
            evento.setEnd(end);

            service.events().update("primary", eventId, evento).execute();

        } catch (Exception e) { // Captura exceções genéricas
            throw new IllegalStateException("Erro ao atualizar evento no Google Calendar.", e);
        }
    }

    public void deletarEvento(String eventId) {
        if (!isEnabled()) {
            throw new IllegalStateException("Google Calendar não está configurado ou habilitado.");
        }

        try {
            Calendar service = googleCalendarConfig.getCalendarService();
            service.events().delete("primary", eventId).execute();
        } catch (Exception e) { // Captura exceções genéricas
            throw new IllegalStateException("Erro ao deletar evento no Google Calendar.", e);
        }
    }
}
