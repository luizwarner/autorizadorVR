package br.com.vr.autorizadorVr.exceptions;

public class CartaoNaoExistenteException extends Exception {
    public CartaoNaoExistenteException(String mensagem){
        super(mensagem);
    }
}
