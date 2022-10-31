package br.com.vr.autorizadorVr.controller;

import br.com.vr.autorizadorVr.dominio.Cartao;
import br.com.vr.autorizadorVr.dominio.Transacao;
import br.com.vr.autorizadorVr.exceptions.CartaoJaExistenteException;
import br.com.vr.autorizadorVr.exceptions.CartaoNaoExistenteException;
import br.com.vr.autorizadorVr.exceptions.TransacaoException;
import br.com.vr.autorizadorVr.servico.Servico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class Controller {

    @Autowired
    private Servico servico;

    public Controller(Servico servico){
        this.servico = servico;
    }

    @PostMapping("/cartoes")
    public ResponseEntity criarCartao(Cartao cartao){
        ResponseEntity resposta = null;
        try{
            servico.criarCartao(cartao);
            cartao.setSaldo(null);
            resposta = new ResponseEntity(cartao, HttpStatus.CREATED);
        }catch (CartaoJaExistenteException ex){
            cartao.setSaldo(null);
            resposta = new ResponseEntity(cartao, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return resposta;
    }

    @GetMapping("/cartoes/{numeroCartao}")
    public ResponseEntity obterSaldo(@PathVariable String numeroCartao){
        ResponseEntity resposta = null;
        try{
            BigDecimal saldo = servico.obterSaldo(numeroCartao);
            resposta = new ResponseEntity(saldo, HttpStatus.OK);
        }catch (CartaoNaoExistenteException ex){
            resposta = new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return resposta;
    }

    @PostMapping("/transacoes")
    public ResponseEntity realizarTransacao(Transacao transacao){
        ResponseEntity resposta = null;
        try{
            servico.realizarTransacao(transacao);
            resposta = new ResponseEntity("OK", HttpStatus.CREATED);
        }catch(TransacaoException ex){
            resposta = new ResponseEntity(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return resposta;
    }
}
