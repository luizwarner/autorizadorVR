package br.com.vr.autorizadorVr;

import br.com.vr.autorizadorVr.controller.Controller;
import br.com.vr.autorizadorVr.dominio.Cartao;
import br.com.vr.autorizadorVr.dominio.Transacao;
import br.com.vr.autorizadorVr.exceptions.CartaoJaExistenteException;
import br.com.vr.autorizadorVr.exceptions.CartaoNaoExistenteException;
import br.com.vr.autorizadorVr.exceptions.TransacaoException;
import br.com.vr.autorizadorVr.servico.Servico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class ControllerTest {

    @Mock
    private Servico servico;
    private Controller controller;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        controller = new Controller(servico);
    }

    @Test
    void deveCriarCartaoComSucessoERetornar201(){
        Cartao cartao = fabricarCartao();
        ResponseEntity<Cartao> resposta = controller.criarCartao(cartao);
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
    }

    @Test
    void deveRetornarUmaRespostaComStatusCode422() throws CartaoJaExistenteException {
        Cartao cartao = fabricarCartao();
        doThrow(CartaoJaExistenteException.class).when(servico).criarCartao(cartao);
        ResponseEntity<Cartao> resposta = controller.criarCartao(cartao);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resposta.getStatusCode());
    }

    @Test
    void aoConsultarSaldoDeveObterSaldoComSucesso() throws CartaoNaoExistenteException {
        when(servico.obterSaldo("12345")).thenReturn(new BigDecimal("500"));
        ResponseEntity resposta = controller.obterSaldo("12345");
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(new BigDecimal("500"), (BigDecimal) resposta.getBody());
    }

    @Test
    void aoConsultarSaldoDoCartaoDeveRetornar404() throws CartaoNaoExistenteException {
        Cartao cartao = fabricarCartao();
        doThrow(CartaoNaoExistenteException.class).when(servico).obterSaldo("12345");
        ResponseEntity resposta = controller.obterSaldo("12345");
        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    void realizarUmaTransacaoComSucesso(){
        Transacao transacao = fabricarTransacao();
        ResponseEntity resposta = controller.realizarTransacao(transacao);
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
    }

    @Test
    void aoTentarRealizarUmaTransacaoDeveRetornar422() throws TransacaoException {
        Transacao transacao = fabricarTransacao();
        doThrow(TransacaoException.class).when(servico).realizarTransacao(transacao);
        ResponseEntity resposta = controller.realizarTransacao(transacao);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resposta.getStatusCode());
    }

    private Cartao fabricarCartao(){
        return Cartao.builder()
                .numeroCartao("12345")
                .senha("123")
                .saldo(new BigDecimal("500")).build();
    }

    private Transacao fabricarTransacao(){
        return Transacao.builder()
                .numeroCartao("12345")
                .senhaCartao("123")
                .valor(new BigDecimal("500")).build();
    }
}
