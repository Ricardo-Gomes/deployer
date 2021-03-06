package br.com.deployer.view.bean.usuario;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import br.com.deployer.exception.ValidationException;
import br.com.deployer.model.Usuario;
import br.com.deployer.service.UsuarioService;
import br.com.deployer.view.util.JsfUtil;

@ManagedBean
@ViewScoped
public class UsuarioForm {

	private Usuario usuario;
	private Long idEdicao;

	@ManagedProperty("#{usuarioService}")
	private UsuarioService service;

	public void inicializa() {
		if (idEdicao != null) {
			this.usuario = service.buscarPorId(idEdicao);
		}

		if (this.usuario == null) {
			this.usuario = new Usuario();
		}
	}
	
	public void cadastro() {
		try {
			service.salvar(usuario);
			this.setUsuario(new Usuario());
			JsfUtil.addSucessMessage("Salvo com sucesso!");
		} catch (ValidationException e) {
			List<String> errors = e.getErrors();
			errors.forEach(msgErro -> JsfUtil.addErrorMessage(msgErro));
		} catch (Exception e) {
			JsfUtil.addMessage("Erro ao salvar", FacesMessage.SEVERITY_ERROR);
		}
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public UsuarioService getService() {
		return service;
	}

	public void setService(UsuarioService service) {
		this.service = service;
	}

	public Long getIdEdicao() {
		return idEdicao;
	}

	public void setIdEdicao(Long idEdicao) {
		this.idEdicao = idEdicao;
	}
}
