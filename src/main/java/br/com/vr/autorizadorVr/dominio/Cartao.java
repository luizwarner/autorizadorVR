package br.com.vr.autorizadorVr.dominio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cartao {

    @Id
    private String numeroCartao;
    private String senha;
    private BigDecimal saldo = new BigDecimal("500");
}
