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

/**
 * Serviço para integração com o Google Calendar, permitindo criar, atualizar
 * e deletar eventos diretamente no calendário do usuário.
 */
@Service
public class GoogleCalendarService {

    @Autowired
    private GoogleCalendarConfig googleCalendarConfig;

    /**
     * Cria um evento no Google Calendar com os detalhes fornecidos.
     *
     * @param resumo      título do evento
     * @param descricao   descrição do evento
     * @param inicio      data e hora de início do evento
     * @param fim         data e hora de término do evento
     * @return            ID do evento criado no Google Calendar
     * @throws IOException se ocorrer um erro de comunicação com a API
     */
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

    /**
     * Atualiza um evento existente no Google Calendar.
     *
     * @param eventId   ID do evento a ser atualizado
     * @param resumo    novo título do evento
     * @param inicio    nova data e hora de início do evento
     * @param fim       nova data e hora de término do evento
     * @throws IOException se ocorrer um erro de comunicação com a API
     */
    public void atualizarEvento(String eventId, String resumo, LocalDateTime inicio, LocalDateTime fim) throws IOException {
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
    }

    /**
     * Deleta um evento do Google Calendar.
     *
     * @param eventId   ID do evento a ser deletado
     * @throws IOException se ocorrer um erro de comunicação com a API
     */
    public void deletarEvento(String eventId) throws IOException {
        Calendar service = googleCalendarConfig.getCalendarService();
        service.events().delete("primary", eventId).execute();
    }
}
