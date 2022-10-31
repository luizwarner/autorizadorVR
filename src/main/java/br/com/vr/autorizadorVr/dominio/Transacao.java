package br.com.vr.autorizadorVr.dominio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class Transacao {
    private String numeroCartao;
    private String senhaCartao;
    private BigDecimal valor;
}
