package br.com.vr.autorizadorVr.exceptions;

public class TransacaoException extends Exception {

    public static String SALDO_INSUFICIENTE = "SALDO_INSUFICIENTE";
    public static String SENHA_INVALIDA = "SENHA_INVALIDA";
    public static String CARTAO_INEXISTENTE = "CARTAO_INEXISTENTE";
    
    public TransacaoException(String mensagem){
        super(mensagem);
    }
}
