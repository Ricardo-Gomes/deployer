package br.com.deployer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.deployer.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	@Query(" select u from Usuario u where upper(u.nome) like upper(:nome) and upper(u.login) like upper(:login) ")
	List<Usuario> findByNomeAndLogin( @Param("nome") String nome, @Param("login") String login);
	
	@Query("select u from Usuario u where u.login = :login")
	Usuario findByLogin (@Param("login")String login);

	@Query(" select u from Usuario u where u.login =:login and u.senha =:senha ")
	Usuario findByAutenticarLoginUsuario(@Param("login") String login, @Param("senha") String senha);

}
