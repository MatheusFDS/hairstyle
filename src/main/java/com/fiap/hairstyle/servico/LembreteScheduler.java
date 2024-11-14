package com.fiap.hairstyle.servico;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.repositorios.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço responsável por agendar e enviar lembretes automáticos
 * para os agendamentos que ocorrerão nas próximas 24 horas.
 */
@Service
public class LembreteScheduler {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    /**
     * Verifica diariamente os agendamentos nas próximas 24 horas e
     * envia lembretes aos clientes.
     *
     * <p>O método é executado automaticamente às 12:00 (meio-dia) todos os dias.</p>
     *
     * <p><b>Cron expression:</b> "0 0 12 * * *" - Executa às 12:00 diariamente.</p>
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void enviarLembretes() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limite = agora.plusHours(24); // Define limite de 24 horas

        // Busca agendamentos que ocorrerão nas próximas 24 horas
        List<Agendamento> agendamentosProximos = agendamentoRepository.findByDataHoraBetween(agora, limite);

        // Envia lembrete para cada agendamento encontrado
        for (Agendamento agendamento : agendamentosProximos) {
            notificacaoService.enviarLembrete(agendamento);
        }
    }
}
