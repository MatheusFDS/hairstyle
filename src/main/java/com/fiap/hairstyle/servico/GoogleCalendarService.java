package com.fiap.hairstyle.servico;

import com.fiap.hairstyle.configuracao.GoogleCalendarConfig;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

@Service
public class GoogleCalendarService {

    @Autowired
    private GoogleCalendarConfig googleCalendarConfig;

    public String criarEvento(String resumo, String descricao, LocalDateTime inicio, LocalDateTime fim) throws IOException {
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
    }

    public void atualizarEvento(String eventId, String resumo, LocalDateTime inicio, LocalDateTime fim) throws IOException {
        Calendar service = googleCalendarConfig.getCalendarService();
        Event evento = service.events().get("primary", eventId).execute();

        // Atualiza os detalhes do evento
        evento.setSummary(resumo);

        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(inicio.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                .setTimeZone(TimeZone.getDefault().getID());

        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(fim.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                .setTimeZone(TimeZone.getDefault().getID());

        evento.setStart(start);
        evento.setEnd(end);

        // Atualiza o evento no Google Calendar
        service.events().update("primary", eventId, evento).execute();
    }

    public void deletarEvento(String eventId) throws IOException {
        Calendar service = googleCalendarConfig.getCalendarService();
        service.events().delete("primary", eventId).execute();
    }
}
