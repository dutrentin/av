package br.com.avaliacao.softplan.utils;

public enum UtilsEnum {
	OK(200),CRIADO(201),CONFLITO(409);

	public int value;
	
	UtilsEnum(int valueReturn) {
		value = valueReturn;
	}

}
