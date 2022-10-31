package br.com.vr.autorizadorVr.servico;

import br.com.vr.autorizadorVr.dominio.Cartao;
import br.com.vr.autorizadorVr.dominio.Transacao;
import br.com.vr.autorizadorVr.exceptions.CartaoJaExistenteException;
import br.com.vr.autorizadorVr.exceptions.CartaoNaoExistenteException;
import br.com.vr.autorizadorVr.exceptions.TransacaoException;
import br.com.vr.autorizadorVr.repositorio.CartaoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class Servico {

    @Autowired
    CartaoRepositorio cartaoRepositorio;

    public Servico(CartaoRepositorio cartaoRepositorio){
        this.cartaoRepositorio = cartaoRepositorio;
    }

    public void criarCartao(Cartao cartao)throws CartaoJaExistenteException {
        verificarCartaoExisteste(cartao);
        cartaoRepositorio.save(cartao);
    }

    public BigDecimal obterSaldo(String numeroCartao)throws CartaoNaoExistenteException {
        Optional<Cartao> cartao = cartaoRepositorio.findById(numeroCartao);
        verificarCartaoNaoExistente(cartao);
        return cartao.get().getSaldo();
    }

    public void realizarTransacao(Transacao transacao)throws TransacaoException {
        Optional<Cartao> cartao = cartaoRepositorio.findById(transacao.getNumeroCartao());

        verificarTransacaoCartaoInexistente(cartao);
        verificarTransacaoSenhaInvalida(cartao.get(), transacao.getSenhaCartao());
        verificarTransacaoSaldoInsuficiente(cartao.get(), transacao.getValor());

        BigDecimal novoSaldo = cartao.get().getSaldo();
        novoSaldo = novoSaldo.subtract(transacao.getValor());
        cartao.get().setSaldo(novoSaldo);
        cartaoRepositorio.save(cartao.get());
    }

    private void verificarCartaoExisteste(Cartao cartao)throws CartaoJaExistenteException{
        Optional<Cartao> listaCartoes = cartaoRepositorio.findById(cartao.getNumeroCartao());
        if(listaCartoes.isPresent()){
            throw new CartaoJaExistenteException("O número do cartão informado já existe em nosso sistema!");
        }
    }

    private void verificarCartaoNaoExistente(Optional<Cartao> cartao)throws CartaoNaoExistenteException{
        if(cartao.isEmpty()){
            throw new CartaoNaoExistenteException("O número do cartão informado não existe em nosso sistema!");
        }
    }

    private void verificarTransacaoSaldoInsuficiente(Cartao cartao, BigDecimal valorADecrementar)throws TransacaoException{
        //Verifica se o valor a decrementar é maior que o saldo em conta.
        if(valorADecrementar.compareTo(cartao.getSaldo()) == 1){
            throw new TransacaoException(TransacaoException.SALDO_INSUFICIENTE);
        }
    }

    private void verificarTransacaoSenhaInvalida(Cartao cartao, String senhaTransacao)throws TransacaoException{
        if(!cartao.getSenha().equals(senhaTransacao)){
            throw new TransacaoException(TransacaoException.SENHA_INVALIDA);
        }
    }

    private void verificarTransacaoCartaoInexistente(Optional<Cartao> cartao) throws TransacaoException{
        if(cartao.isEmpty()){
            throw new TransacaoException(TransacaoException.CARTAO_INEXISTENTE);
        }
    }
}
