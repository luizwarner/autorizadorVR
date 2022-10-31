package br.com.vr.autorizadorVr;

import br.com.vr.autorizadorVr.dominio.Cartao;
import br.com.vr.autorizadorVr.dominio.Transacao;
import br.com.vr.autorizadorVr.exceptions.CartaoJaExistenteException;
import br.com.vr.autorizadorVr.exceptions.CartaoNaoExistenteException;
import br.com.vr.autorizadorVr.exceptions.TransacaoException;
import br.com.vr.autorizadorVr.repositorio.CartaoRepositorio;
import br.com.vr.autorizadorVr.servico.Servico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServicoTest {

    @Mock
    private CartaoRepositorio cartaoRepositorio;

    private Servico servico;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        servico = new Servico(cartaoRepositorio);
    }

    @Test
    void deveExecutarCriarCartaoComSucesso() throws CartaoJaExistenteException {
        Cartao cartao = fabricarCartao();
        servico.criarCartao(cartao);
        verify(cartaoRepositorio).save(cartao);
    }

    @Test
    void aoCriarCartaoDeveLancarUmaExcecao(){
        Cartao cartao = fabricarCartao();
        Optional<Cartao> optionalCartao = Optional.of(cartao);
        when(cartaoRepositorio.findById("12345")).thenReturn(optionalCartao);
        try{
            servico.criarCartao(cartao);
        }catch (Exception ex){
            assertEquals("O número do cartão informado já existe em nosso sistema!", ex.getMessage());
            assertInstanceOf(CartaoJaExistenteException.class, ex);
        }
    }

    @Test
    void deveConsultarSaldoComSucesso() throws CartaoNaoExistenteException {
        Cartao cartao = fabricarCartao();
        Optional<Cartao> cartaoOptional = Optional.of(cartao);
        when(cartaoRepositorio.findById(cartao.getNumeroCartao())).thenReturn(cartaoOptional);
        BigDecimal saldo = servico.obterSaldo("12345");
        assertEquals(new BigDecimal("500"), saldo);
    }

    @Test
    void aoConsultarSaldoDeveLancarUmaExcecao() {
        Cartao cartao = fabricarCartao();
        Optional<Cartao> cartaoOptional = Optional.empty();
        when(cartaoRepositorio.findById(cartao.getNumeroCartao())).thenReturn(cartaoOptional);
        try{
            servico.obterSaldo(cartao.getNumeroCartao());
        }catch (Exception ex){
            assertEquals("O número do cartão informado não existe em nosso sistema!", ex.getMessage());
            assertInstanceOf(CartaoNaoExistenteException.class, ex);
        }
    }

    @Test
    void deveRealizarTransacaoComSucesso() throws TransacaoException {
        Transacao transacao = fabricarTransacao();
        Cartao cartao = fabricarCartao();
        Optional<Cartao> cartaoOptional = Optional.of(cartao);
        when(cartaoRepositorio.findById(cartao.getNumeroCartao())).thenReturn(cartaoOptional);
        servico.realizarTransacao(transacao);
        verify(cartaoRepositorio).save(cartao);
    }

    @Test
    void aoRealizarUmaTransacaodeveLancarUmaExecaoDeCartaoInexistente(){
        Transacao transacao = fabricarTransacao();
        Optional<Cartao> cartaoOptional = Optional.empty();
        when(cartaoRepositorio.findById(transacao.getNumeroCartao())).thenReturn(cartaoOptional);
        try{
            servico.realizarTransacao(transacao);
        }catch (Exception ex) {
            assertEquals(TransacaoException.CARTAO_INEXISTENTE, ex.getMessage());
            assertInstanceOf(TransacaoException.class, ex);
        }
    }

    @Test
    void aoRealizarUmaTransacaodeveLancarUmaExecaoDeSenhaInvalida(){
        Transacao transacao = fabricarTransacao();
        Cartao cartao = fabricarCartao();
        cartao.setSenha("444");
        Optional<Cartao> cartaoOptional = Optional.of(cartao);
        when(cartaoRepositorio.findById(transacao.getNumeroCartao())).thenReturn(cartaoOptional);
        try{
            servico.realizarTransacao(transacao);
        }catch (Exception ex) {
            assertEquals(TransacaoException.SENHA_INVALIDA, ex.getMessage());
            assertInstanceOf(TransacaoException.class, ex);
        }
    }

    @Test
    void aoRealizarUmaTransacaodeveLancarUmaExecaoDeSaldoInsuficiente(){
        Transacao transacao = fabricarTransacao();
        transacao.setValor(new BigDecimal("700"));
        Cartao cartao = fabricarCartao();
        Optional<Cartao> cartaoOptional = Optional.of(cartao);
        when(cartaoRepositorio.findById(transacao.getNumeroCartao())).thenReturn(cartaoOptional);
        try{
            servico.realizarTransacao(transacao);
        }catch (Exception ex) {
            assertEquals(TransacaoException.SALDO_INSUFICIENTE, ex.getMessage());
            assertInstanceOf(TransacaoException.class, ex);
        }
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
                .valor(new BigDecimal("100")).build();
    }
}