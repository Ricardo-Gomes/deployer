package br.com.deployer.view.bean.aplicacao;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.com.deployer.exception.UnexpectedApplicationErrorException;
import br.com.deployer.exception.ValidationException;
import br.com.deployer.model.Aplicacao;
import br.com.deployer.model.Arquivo;
import br.com.deployer.model.Servidor;
import br.com.deployer.model.ServletContainer;
import br.com.deployer.model.Usuario;
import br.com.deployer.service.AplicacaoService;
import br.com.deployer.service.ServidorService;
import br.com.deployer.service.ServletContainerService;
import br.com.deployer.service.UsuarioService;
import br.com.deployer.view.util.JsfUtil;
import lombok.extern.slf4j.Slf4j;

@ManagedBean
@ViewScoped
@Slf4j
public class DeployBean {

	private Aplicacao entidade;
	private List<Usuario> usuarios;
	private List<Servidor> servidores;
	private List<ServletContainer> servletContainersServidorSelecionado;
	
	@ManagedProperty("#{aplicacaoService}")
	private AplicacaoService service;

	@ManagedProperty("#{usuarioService}")
	private UsuarioService usuarioService;

	@ManagedProperty("#{servidorService}")
	private ServidorService servidorService;

	@ManagedProperty("#{servletContainerService}")
	private ServletContainerService servletContainerService;

	@PostConstruct
	public void inicializa() {
		this.entidade = new Aplicacao();
	}

	public void fileUploadListener(FileUploadEvent event) {
		UploadedFile uploadFile = event.getFile();
		getEntidade().setWar(new Arquivo(uploadFile.getContents(), uploadFile.getFileName(), uploadFile.getSize()));
	}

	public void enviar() {
		try {
			this.service.enviar(entidade);
			JsfUtil.addSucessMessage("Deploy realizado com sucesso!");
		} catch (UnexpectedApplicationErrorException e) {
			JsfUtil.addErrorMessage(e.getMessage());
		} catch (ValidationException e) {
			e.getErrors().forEach(JsfUtil::addErrorMessage);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			JsfUtil.addErrorMessage("Ocorreu um erro ao processar a requisição. Entre em contato com a administração caso o erro persista.");
		}
	}

	public void listarServletContainers() {
		Servidor servidorSelecionado = getEntidade().getServidor();
		servletContainersServidorSelecionado = getServletContainerService().listarServletContainersPorServidor(servidorSelecionado);
	}

	public List<Usuario> getUsuarios() {
		if(this.usuarios == null) {
			this.usuarios = usuarioService.listarUsuarios();
		}
		return this.usuarios;
	}

	public List<Servidor> getServidores() {
		if(this.servidores == null) {
			this.servidores = servidorService.listarServidores();
		}
		return this.servidores;
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

	public Aplicacao getEntidade() {
		return entidade;
	}

	public void setEntidade(Aplicacao entidade) {
		this.entidade = entidade;
	}

	public ServidorService getServidorService() {
		return servidorService;
	}

	public void setServidorService(ServidorService servidorService) {
		this.servidorService = servidorService;
	}

	public ServletContainerService getServletContainerService() {
		return servletContainerService;
	}

	public void setServletContainerService(ServletContainerService servletContainerService) {
		this.servletContainerService = servletContainerService;
	}

	
	public List<ServletContainer> getServletContainersServidorSelecionado() {
		return servletContainersServidorSelecionado;
	}

	public void setServletContainersServidorSelecionado(List<ServletContainer> servletContainersServidorSelecionado) {
		this.servletContainersServidorSelecionado = servletContainersServidorSelecionado;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public void setServidores(List<Servidor> servidores) {
		this.servidores = servidores;
	}
}
