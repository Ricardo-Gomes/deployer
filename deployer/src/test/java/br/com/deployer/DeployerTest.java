package br.com.deployer;

import java.util.List;

import br.com.deployer.model.TomcatListObject;
import br.com.deployer.service.tomcat.TomcatApplicationManager;

public class DeployerTest {

    public static void main(String[] args) {

        TomcatApplicationManager tam = new TomcatApplicationManager("http://localhost:8080", "tomcat-script", "tomcat");
        
        //tam.deploy("/teste", new File("C:\\Users\\ricardo\\Desktop\\teste\\teste.war"));

        //tam.stop("/teste");
       
        //tam.undeploy("/teste");
        
        List<TomcatListObject> response = tam.listObjects();
        
        TomcatListObject app = TomcatListObject.pesquisarPorContexto("/deployer", response);
        System.out.println("Status do Deployer: "+app.getStatus());
        
        System.out.println();
        System.out.println(response);
    }
    
}
