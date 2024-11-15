package com.fiap.hairstyle.aplicacao.casosdeuso;

import com.fiap.hairstyle.aplicacao.casosdeuso.CriarAgendamentoCasoDeUsoImpl;
import com.fiap.hairstyle.aplicacao.portas.saida.AgendamentoSaidaPort;
import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.entidades.Cliente;
import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.dominio.entidades.Servico;
import com.fiap.hairstyle.dominio.servico.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CriarAgendamentoCasoDeUsoTest {

    @Mock
    private AgendamentoSaidaPort agendamentoSaidaPort;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private CriarAgendamentoCasoDeUsoImpl criarAgendamentoCasoDeUso;

    private Agendamento agendamento;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Configura as entidades para o teste
        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());

        Profissional profissional = new Profissional();
        profissional.setId(UUID.randomUUID());

        Servico servico = new Servico();
        servico.setId(UUID.randomUUID());
        servico.setDuracao(60);

        agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setProfissional(profissional);
        agendamento.setServico(servico);
        agendamento.setDataHora(LocalDateTime.now().plusDays(1));  // Agendamento para o futuro
    }

    @Test
    public void deveCriarAgendamentoComSucesso() {
        when(agendamentoSaidaPort.salvar(any(Agendamento.class))).thenReturn(agendamento);

        Agendamento agendamentoCriado = criarAgendamentoCasoDeUso.executar(agendamento);

        assertNotNull(agendamentoCriado);
        assertEquals(agendamento.getCliente(), agendamentoCriado.getCliente());
        assertEquals(agendamento.getProfissional(), agendamentoCriado.getProfissional());

        verify(notificacaoService, times(1)).enviarConfirmacao(agendamentoCriado);
    }

    @Test
    public void deveLancarExcecaoSeHorarioIndisponivel() {
        when(agendamentoSaidaPort.existeAgendamentoNoHorario(any(UUID.class), any(LocalDateTime.class)))
                .thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            criarAgendamentoCasoDeUso.executar(agendamento);
        });

        assertEquals("Horário indisponível para o profissional.", exception.getMessage());
    }
}
