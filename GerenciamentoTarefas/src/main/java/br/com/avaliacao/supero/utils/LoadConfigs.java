package br.com.avaliacao.supero.utils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;

public class LoadConfigs {
	
	public static Client client;
	private static String urlApiRest;
	private static WebTarget target;
	
	/**
	 * método usado para leitura de arquivo config.properties com parametro hotname_api_restful
	 * o qual informa o host do restful que será consumido e será carregado no client para submissão
	 * @return objeto WebTarget classe de requisição ao restful
	 */	
	public static WebTarget loadConfigs(){
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());
		client = ClientBuilder.newClient(config);
		urlApiRest = UtilResource.getResourceProperty("config", "hotname_api_restful");
		return target = client.target(urlApiRest);
	}

}
