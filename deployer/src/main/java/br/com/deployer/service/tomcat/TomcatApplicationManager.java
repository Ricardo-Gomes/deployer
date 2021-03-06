package br.com.deployer.service.tomcat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import br.com.deployer.model.TomcatListObject;


public class TomcatApplicationManager extends AbstractTomcatApplicationManager {

    private String host;
    private String tomcatUser;
    private String tomcatPassword;

    private RestTemplate restTemplate;

    public TomcatApplicationManager(String host, String tomcatUser, String tomcatPassword) {
        this.host = host;
        this.tomcatUser = tomcatUser;
        this.tomcatPassword = tomcatPassword;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ResponseEntity<String> deploy(String applicationPath, File warFile) {
        String deployURI = getDeployApplicationURI(host);
        deployURI += "?"+TomcatManagerApplicationActions.PARAMS.PATH.getParam()+"="+applicationPath;

        HttpHeaders headers = createBasicAutenticationHeader(tomcatUser, tomcatPassword);

        //add file
        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add(TomcatManagerApplicationActions.PARAMS.WAR.getParam(), new FileSystemResource(warFile));

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        return restTemplate.exchange(deployURI, HttpMethod.PUT, requestEntity, String.class);
    }

    @Override
    public ResponseEntity<String> undeploy(String applicationPath) {
        String undeployURI = getUndeployApplicationURI(host);
        undeployURI += "?"+TomcatManagerApplicationActions.PARAMS.PATH.getParam()+"="+applicationPath;

        HttpHeaders headers = createBasicAutenticationHeader(tomcatUser, tomcatPassword);

        HttpEntity<String> request = new HttpEntity<String>(headers);
        return restTemplate.exchange(undeployURI, HttpMethod.GET, request, String.class);
    }

    @Override
    public ResponseEntity<String> start(String applicationPath) {
        String startURI = getStartApplicationURI(host);
        startURI += "?"+TomcatManagerApplicationActions.PARAMS.PATH.getParam()+"="+applicationPath;

        HttpHeaders headers = createBasicAutenticationHeader(tomcatUser, tomcatPassword);

        HttpEntity<String> request = new HttpEntity<String>(headers);
        return restTemplate.exchange(startURI, HttpMethod.GET, request, String.class);
    }

    @Override
    public ResponseEntity<String> stop(String applicationPath) {
        String stopURI = getStopApplicationURI(host);
        stopURI += "?"+TomcatManagerApplicationActions.PARAMS.PATH.getParam()+"="+applicationPath;

        HttpHeaders headers = createBasicAutenticationHeader(tomcatUser, tomcatPassword);

        HttpEntity<String> request = new HttpEntity<String>(headers);
        return restTemplate.exchange(stopURI, HttpMethod.GET, request, String.class);
    }

    @Override
    public ResponseEntity<String> restart(String applicationPath) {
        String restartURI = getRestartApplicationURI(host);
        restartURI += "?"+TomcatManagerApplicationActions.PARAMS.PATH.getParam()+"="+applicationPath;

        HttpHeaders headers = createBasicAutenticationHeader(tomcatUser, tomcatPassword);

        HttpEntity<String> request = new HttpEntity<String>(headers);
        return restTemplate.exchange(restartURI, HttpMethod.GET, request, String.class);
    }
    
    public ResponseEntity<String> list() {
    	String listURI = getListApplicationURI(host);
    	
    	HttpHeaders headers = createBasicAutenticationHeader(tomcatUser, tomcatPassword);
    	
    	HttpEntity<String> request = new HttpEntity<String>(headers);
        return restTemplate.exchange(listURI, HttpMethod.GET, request, String.class);
    }
    
    public List<TomcatListObject> listObjects() {
    	ResponseEntity<String> response = list();
    	
    	String responseString = response.getBody();
    	if (responseString == null || responseString.isEmpty()) return null;
    	
    	String[] lines = responseString.split(System.getProperty("line.separator"));
    	if (!lines[0].startsWith("OK")) {
    		throw new RuntimeException("Não foi possível listar as aplicações. Retorno: "+responseString);
    	}
    	if (lines.length < 2) return null; // Quando não há pelo menos 2 linhas significa que não há nenhuma aplicação publicada.
    	
    	List<TomcatListObject> listaRetorno = new ArrayList<>();
    	TomcatListObject tomcatListObject;
    	for(int i = 1 ; i < lines.length ; i++) {
    		String linha = lines[i];
    		String[] values = linha.split(":");
    		if (values == null || values.length == 0) continue;
    		if (values.length < 4) throw new RuntimeException("A linha de retorno da listagem não possui os 4 elementos definidos na documentação.");
    		
    		Integer numeroSessoes;
    		try {
    			numeroSessoes = Integer.parseInt(values[2]);
    		} catch (NumberFormatException e) {
				throw new RuntimeException("Não foi possível converter a quantidade de sessões ativas.", e);
			}
    		
    		tomcatListObject = new TomcatListObject();
    		tomcatListObject.setContextPath(values[0]);
    		tomcatListObject.setStatus(values[1]);
    		tomcatListObject.setNumeroSessoesAtivas(numeroSessoes);
    		tomcatListObject.setFolderName(values[3]);
    			
    		listaRetorno.add(tomcatListObject);
    	}
    	
    	return listaRetorno;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTomcatUser() {
        return tomcatUser;
    }

    public void setTomcatUser(String tomcatUser) {
        this.tomcatUser = tomcatUser;
    }

    public String getTomcatPassword() {
        return tomcatPassword;
    }

    public void setTomcatPassword(String tomcatPassword) {
        this.tomcatPassword = tomcatPassword;
    }

}
