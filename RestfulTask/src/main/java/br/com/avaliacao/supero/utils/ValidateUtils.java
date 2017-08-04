package br.com.avaliacao.supero.utils;

public class ValidateUtils {
	
	public static boolean validateString(String stringValidade){
		if(stringValidade != null && !stringValidade.equals("") && !stringValidade.equals("null") ){
			if(stringValidade.equals(".")){
				return false;
			}
			return true;
		}
		return false;
	}

}
