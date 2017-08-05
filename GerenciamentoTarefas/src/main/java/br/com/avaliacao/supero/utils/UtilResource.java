package br.com.avaliacao.supero.utils;

import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

public class UtilResource {
	
	/**
	 * método usado para leitura de arquivo config.properties com parametros passado
	 * @param String de caminho do arquivo, String nome da propriedades lida
	 * @return String retorna endereço de acesso do restful
	 */	
	public static String getResourceProperty(String resource, String label) {
		FacesContext fc = FacesContext.getCurrentInstance();
		Application application = fc.getApplication();
		ResourceBundle bundle = application.getResourceBundle(fc, resource);

		return bundle.getString(label);
	}

}
