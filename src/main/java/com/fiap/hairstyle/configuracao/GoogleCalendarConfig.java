package com.fiap.hairstyle.configuracao;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class GoogleCalendarConfig {

    @Value("${google.credentials.file-path}")
    private String credentialsFilePath;

    public Calendar getCalendarService() {
        try {
            // Obtém o caminho absoluto para garantir que funcione em diferentes ambientes
            String absolutePath = Paths.get(credentialsFilePath).toAbsolutePath().toString();

            GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(absolutePath))
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

            return new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential
            ).setApplicationName("Hairstyle Scheduling App").build();

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Erro ao configurar o serviço do Google Calendar: " + e.getMessage(), e);
        }
    }
}
