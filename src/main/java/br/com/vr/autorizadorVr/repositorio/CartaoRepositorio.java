package br.com.vr.autorizadorVr.repositorio;

import br.com.vr.autorizadorVr.dominio.Cartao;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartaoRepositorio extends CrudRepository<Cartao, String> {

}
