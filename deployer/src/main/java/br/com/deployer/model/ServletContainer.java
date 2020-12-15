package br.com.deployer.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "servlet_container", schema = "deployer")
@ToString
public class ServletContainer implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String nome;
	
	@Column
	private String label;
	
	@Column
	private String usuario;
	
	@Column
	private String senha;
	
	@ManyToOne
	@JoinColumn(name = "fk_servidor", referencedColumnName = "id")
	private Servidor servidor;
	
	@ManyToOne
	@JoinColumn(name = "fk_usuario_cadastro", referencedColumnName = "id")
	private Usuario usuarioCadastro;
	
    @Column(name = "dt_cadastro")
    private LocalDateTime dataCadastro;
}
