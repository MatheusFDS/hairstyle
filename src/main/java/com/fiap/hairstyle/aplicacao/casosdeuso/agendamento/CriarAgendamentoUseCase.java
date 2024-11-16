package com.fiap.hairstyle.aplicacao.casosdeuso.agendamento;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;
import com.fiap.hairstyle.dominio.entidades.Servico;
import com.fiap.hairstyle.dominio.portas.saida.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class CriarAgendamentoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CriarAgendamentoUseCase.class);

    private final AgendamentoPort agendamentoPort;
    private final HorarioDisponivelPort horarioDisponivelPort;
    private final ServicoPort servicoPort;
    private final GoogleCalendarPort googleCalendarPort;
    private final NotificacaoPort notificacaoPort;

    public CriarAgendamentoUseCase(AgendamentoPort agendamentoPort,
                                   HorarioDisponivelPort horarioDisponivelPort,
                                   ServicoPort servicoPort,
                                   GoogleCalendarPort googleCalendarPort,
                                   NotificacaoPort notificacaoPort) {
        this.agendamentoPort = agendamentoPort;
        this.horarioDisponivelPort = horarioDisponivelPort;
        this.servicoPort = servicoPort;
        this.googleCalendarPort = googleCalendarPort;
        this.notificacaoPort = notificacaoPort;
    }

    public Agendamento executar(Agendamento agendamento) {
        logger.info("Tentando criar agendamento para o cliente {} no horário {}",
                agendamento.getCliente().getId(), agendamento.getDataHora());

        LocalDateTime dataHora = agendamento.getDataHora();
        UUID profissionalId = agendamento.getProfissional().getId();
        DayOfWeek diaSemana = dataHora.getDayOfWeek();
        LocalTime hora = dataHora.toLocalTime();

        Servico servico = servicoPort.buscarPorId(agendamento.getServico().getId())
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado."));

        if (servico.getDuracao() == null || servico.getDuracao() <= 0) {
            throw new IllegalArgumentException("Duração do serviço inválida.");
        }

        List<HorarioDisponivel> horariosDisponiveis = horarioDisponivelPort.buscarPorProfissionalId(profissionalId);

        if (!horariosDisponiveis.isEmpty() && horariosDisponiveis.stream().noneMatch(h ->
                h.getDiaSemana().equals(diaSemana) &&
                        !hora.isBefore(h.getHoraInicio()) && !hora.isAfter(h.getHoraFim()))) {
            throw new IllegalArgumentException("Horário indisponível para o profissional.");
        }

        if (agendamentoPort.buscarPorProfissionalEHorario(profissionalId, dataHora).isPresent()) {
            throw new IllegalArgumentException("Profissional já possui um agendamento nesse horário.");
        }

        if (agendamentoPort.buscarPorClienteEHorario(agendamento.getCliente().getId(), dataHora).isPresent()) {
            throw new IllegalArgumentException("Cliente já possui um agendamento nesse horário.");
        }

        agendamento.setServico(servico);
        Agendamento novoAgendamento = agendamentoPort.salvar(agendamento);

        if (googleCalendarPort.isEnabled()) {
            try {
                String eventId = googleCalendarPort.criarEvento(
                        "Agendamento - " + servico.getNome(),
                        "Cliente: " + agendamento.getCliente().getNome(),
                        agendamento.getDataHora(),
                        agendamento.getDataHora().plusMinutes(servico.getDuracao())
                );
                novoAgendamento.setGoogleCalendarEventId(eventId);
                novoAgendamento = agendamentoPort.salvar(novoAgendamento);
            } catch (Exception e) {
                logger.warn("Erro ao sincronizar com o Google Calendar. Ignorando integração.", e);
            }
        }

        notificacaoPort.enviarConfirmacao(novoAgendamento);
        return novoAgendamento;
    }
}
