package com.fiap.hairstyle.servico;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.repositorios.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LembreteScheduler {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    // Executa todos os dias às 8:00 para verificar agendamentos próximos
    @Scheduled(cron = "0 0 12 * * *")
    public void enviarLembretes() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limite = agora.plusHours(24); // Verifica agendamentos nas próximas 24 horas

        // Busca agendamentos nas próximas 24 horas
        List<Agendamento> agendamentosProximos = agendamentoRepository.findByDataHoraBetween(agora, limite);

        for (Agendamento agendamento : agendamentosProximos) {
            notificacaoService.enviarLembrete(agendamento);
        }
    }
}
