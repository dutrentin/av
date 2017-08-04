package br.com.avaliacao.supero.repositories;

import java.util.List;

import br.com.avaliacao.supero.model.Person;
import br.com.avaliacao.supero.utils.FilterPerson;

public interface PersonDAO {

	public void add(Person person);
	
	public Person findById(Long id);
	
	public void remove(Person person);
	
	public List<Person> getPersons(FilterPerson filter);
	
	public int count(FilterPerson filter);
	
	public int countExistingCPF(String cpf, Long id);
}
