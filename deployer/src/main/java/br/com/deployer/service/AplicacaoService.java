package br.com.deployer.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.deployer.exception.UnexpectedApplicationErrorException;
import br.com.deployer.exception.ValidationException;
import br.com.deployer.model.Aplicacao;
import br.com.deployer.model.Arquivo;
import br.com.deployer.model.ServletContainer;
import br.com.deployer.model.TomcatListObject;
import br.com.deployer.model.Usuario;
import br.com.deployer.repository.AplicacaoRepository;
import br.com.deployer.service.tomcat.TomcatApplicationManager;
import br.com.deployer.view.util.JsfUtil;

@Service
public class AplicacaoService {

	private static final String WAR_EXTENSION = ".war";

	@Autowired
	private AplicacaoRepository repository;

	@Transactional
	public Aplicacao enviar(Aplicacao aplicacao) {
		validar(aplicacao);
		try {
			realizarDeploy(aplicacao);
		} catch (IOException e) {
			throw new UnexpectedApplicationErrorException(
					"Erro ao fazer o deploy, procure a administração do sistema.");
		}
		Usuario usuarioSessao = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("usuarioSessao");
		aplicacao.setDataCadastro(LocalDateTime.now());
		aplicacao.setUsuarioCadastro(usuarioSessao);
		return repository.save(aplicacao);
	}

	@Transactional
	public void undeploy(Aplicacao aplicacao) {
			realizarUndeploy(aplicacao);
			this.repository.delete(aplicacao);
	}

	public void stop(Aplicacao aplicacao) {
			realizarStop(aplicacao);
	}

	public void start(Aplicacao aplicacao) {
			realizarStart(aplicacao);
	}

	public void restart(Aplicacao aplicacao) {
			realizarRestart(aplicacao);
	}

	private void validar(Aplicacao aplicacao) {
		List<String> checks = new ArrayList<String>();

		if (aplicacao.getUsuarioCadastro() == null) {
			checks.add("Selecione um usuário");
		}

		if (aplicacao.getServidor() == null) {
			checks.add("Selecione um servidor");
		}

		if (aplicacao.getCaminhoContexto() == null || aplicacao.getCaminhoContexto().trim().equals("")) {
			checks.add("O campo Caminho do Contexto é obrigatório");
		}

		if (aplicacao.getWar() == null) {
			checks.add("Selecione um arquivo war");
		}

		Aplicacao aplicacaoAuxilixar = repository.findByContexto(aplicacao.getCaminhoContexto());

		if (aplicacaoAuxilixar != null && !aplicacaoAuxilixar.getCaminhoContexto().equals(aplicacao.getId())) {
			checks.add("Caminho Contexto já está em uso");
		}

		if (!checks.isEmpty()) {
			throw new ValidationException(checks);
		}
	}

	public void realizarDeploy(Aplicacao aplicacao) throws IOException {
		Arquivo war = aplicacao.getWar();
		String nomeDoArquivoSemExtensao = obterNomeSemExtensao(war.getNomeArquivo());
		File tempFile = File.createTempFile(nomeDoArquivoSemExtensao, WAR_EXTENSION, new File("C:/deployertemp"));
		FileOutputStream fos = new FileOutputStream(tempFile);
		fos.write(war.getConteudo());

		ServletContainer servletContainerSelecionado = aplicacao.getServletContainer();
		String urlDeploy = aplicacao.getServidor().getUrl() + servletContainerSelecionado.getLabel();
		TomcatApplicationManager tomcatManager = new TomcatApplicationManager(urlDeploy,
				servletContainerSelecionado.getUsuario(), servletContainerSelecionado.getSenha());
		tomcatManager.deploy(aplicacao.getCaminhoContexto(), tempFile);
		fos.close();
		tempFile.delete();
	}

	private String obterNomeSemExtensao(String fileName) {
		if (fileName == null) {
			return null;
		}
		return fileName.substring(0, fileName.lastIndexOf("."));
	}

	public List<Aplicacao> buscarAplicacaoPorUsuario(Aplicacao aplicacao) {
		return repository.findByUsuario(aplicacao.getUsuarioCadastro().getId());
	}

	public void realizarUndeploy(Aplicacao aplicacao) {
		ServletContainer servletContainerSelecionado = aplicacao.getServletContainer();
		String urlUndeploy = aplicacao.getServidor().getUrl() + servletContainerSelecionado.getLabel();
		TomcatApplicationManager tomcatManager = new TomcatApplicationManager(urlUndeploy,
				servletContainerSelecionado.getUsuario(), servletContainerSelecionado.getSenha());
		tomcatManager.undeploy(aplicacao.getCaminhoContexto());
	}

	public void realizarStop(Aplicacao aplicacao) {
		ServletContainer servletContainerSelecionado = aplicacao.getServletContainer();
		String urlStop = aplicacao.getServidor().getUrl() + servletContainerSelecionado.getLabel();
		TomcatApplicationManager tomcatManager = new TomcatApplicationManager(urlStop,
				servletContainerSelecionado.getUsuario(), servletContainerSelecionado.getSenha());
		tomcatManager.stop(aplicacao.getCaminhoContexto());
	}

	public void realizarStart(Aplicacao aplicacao) {
		ServletContainer servletContainerSelecionado = aplicacao.getServletContainer();
		String urlStart = aplicacao.getServidor().getUrl() + servletContainerSelecionado.getLabel();
		TomcatApplicationManager tomcatManager = new TomcatApplicationManager(urlStart,
				servletContainerSelecionado.getUsuario(), servletContainerSelecionado.getSenha());
		tomcatManager.start(aplicacao.getCaminhoContexto());
	}

	public void realizarRestart(Aplicacao aplicacao) {
		ServletContainer servletContainerSelecionado = aplicacao.getServletContainer();
		String urlRestart = aplicacao.getServidor().getUrl() + servletContainerSelecionado.getLabel();
		TomcatApplicationManager tomcatManager = new TomcatApplicationManager(urlRestart,
				servletContainerSelecionado.getUsuario(), servletContainerSelecionado.getSenha());
		tomcatManager.restart(aplicacao.getCaminhoContexto());
	}

	public void carregarInformacoesServidor(Aplicacao aplicacao) {
		ServletContainer servletContainerSelecionado = aplicacao.getServletContainer();
		String contextoAtual = aplicacao.getServidor().getUrl() + servletContainerSelecionado.getLabel();
		
		TomcatApplicationManager tomcatManager = new TomcatApplicationManager(contextoAtual,
													servletContainerSelecionado.getUsuario(), servletContainerSelecionado.getSenha());
		
		List<TomcatListObject> listaAplicacoes = tomcatManager.listObjects();
		TomcatListObject app = TomcatListObject.pesquisarPorContexto(aplicacao.getCaminhoContexto(), listaAplicacoes);
		aplicacao.setInformacoes(app);
	}
}
