package br.com.deployer.view.bean.aplicacao;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import br.com.deployer.exception.ValidationException;
import br.com.deployer.model.Aplicacao;
import br.com.deployer.model.Usuario;
import br.com.deployer.service.AplicacaoService;
import br.com.deployer.service.UsuarioService;
import br.com.deployer.view.util.JsfUtil;
import lombok.extern.slf4j.Slf4j;

@ManagedBean
@ViewScoped
@Slf4j
public class ListagemAplicaçõesBean {

	private Aplicacao entidade;
	private List<Aplicacao> resultado;
	private List<Usuario> usuarios;

	@ManagedProperty("#{aplicacaoService}")
	private AplicacaoService service;
	
	@ManagedProperty("#{usuarioService}")
	private UsuarioService usuarioService;

	@PostConstruct
	public void inicializa() {
		if (entidade != null) {
		}
		this.entidade = new Aplicacao();
	}
	
	public void btnStart(Aplicacao aplicacao) {
		try {
			this.service.realizarStart(aplicacao);
			aplicacao.getInformacoes().setStatus("running");
			JsfUtil.addSucessMessage("Start realizado com sucesso!");
		} catch (ValidationException e) {
			JsfUtil.addErrorMessage("Erro ao realizar o Start");
		} 
	}
	
	public void btnStop(Aplicacao aplicacao) {
		try {
			this.service.realizarStop(aplicacao);
			aplicacao.getInformacoes().setStatus("stopped");
			JsfUtil.addSucessMessage("Stop realizado com sucesso!");
		} catch (ValidationException e) {
			JsfUtil.addErrorMessage("Erro ao realizar o Stop");
		}
	}
	public void btnUndeploy(Aplicacao aplicacao) {
		try {
			this.service.undeploy(aplicacao);
			this.resultado.remove(aplicacao);
			JsfUtil.addSucessMessage("Undeploy realizado com sucesso!");
		} catch (ValidationException e) {
			JsfUtil.addErrorMessage("Erro ao realizar Undeploy");
		}
	}
	
	public void btnRestart(Aplicacao aplicacao) {
		try {
			this.service.restart(aplicacao);
			JsfUtil.addSucessMessage("Restart realizado com sucesso!");
		} catch (ValidationException e) {
			JsfUtil.addErrorMessage("Erro ao realizar Restart");
		}
	}
	
	public void consultar() {
		this.resultado = service.buscarAplicacaoPorUsuario(getEntidade());
		if (resultado.isEmpty()) {
			JsfUtil.addAlert("Nenhum Registro Encontrado");
		}else {
			this.resultado.forEach( r -> service.carregarInformacoesServidor(r) );
		}
	}
	
	public List<Usuario> getUsuarios() {
		if(this.usuarios == null ) {
			this.usuarios = usuarioService.listarUsuarios();
		}
		return this.usuarios;
	}

	public Aplicacao getEntidade() {
		return entidade;
	}

	public void setEntidade(Aplicacao entidade) {
		this.entidade = entidade;
	}

	public List<Aplicacao> getResultado() {
		return resultado;
	}

	public void setResultado(List<Aplicacao> resultado) {
		this.resultado = resultado;
	}

	public AplicacaoService getService() {
		return service;
	}

	public void setService(AplicacaoService service) {
		this.service = service;
	}

	public UsuarioService getUsuarioService() {
		return usuarioService;
	}

	public void setUsuarioService(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
}
