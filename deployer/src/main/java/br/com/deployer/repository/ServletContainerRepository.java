package br.com.deployer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.deployer.model.ServletContainer;

public interface ServletContainerRepository extends JpaRepository<ServletContainer, Long>{

		@Query("Select s from ServletContainer s where s.servidor.id = :servidorid")
		List<ServletContainer> findByServidorId(@Param("servidorid") Long id);
}
