package br.com.deployer.view.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import br.com.deployer.model.Usuario;
import br.com.deployer.service.UsuarioService;
import br.com.deployer.view.util.JsfUtil;

@ManagedBean
@ViewScoped
public class LoginBean {

	private Usuario usuarioLogin;

	@ManagedProperty("#{usuarioService}")
	private UsuarioService usuarioService;
	
	public LoginBean() {
		this.usuarioLogin = new Usuario();
	}

	public String login() {
		usuarioLogin = usuarioService.autenticarLoginUsuario(getUsuarioLogin());

		if (usuarioLogin == null) {
			JsfUtil.addAlert("Usuario e/ou senha incorreto(s)");
			return null;
		} else {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuarioSessao", usuarioLogin);
			return "/index.xhtml?faces-redirect=true";
		}
	}

	public Usuario getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(Usuario usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}

	public UsuarioService getUsuarioService() {
		return usuarioService;
	}

	public void setUsuarioService(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}
}
