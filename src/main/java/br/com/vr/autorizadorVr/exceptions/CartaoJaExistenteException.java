package br.com.vr.autorizadorVr.exceptions;

public class CartaoJaExistenteException extends Exception {
    public CartaoJaExistenteException(String mensagem){
        super(mensagem);
    }
}
