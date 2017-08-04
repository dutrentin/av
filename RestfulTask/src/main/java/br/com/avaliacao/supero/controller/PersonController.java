package br.com.avaliacao.supero.controller;

import java.util.List;

import org.springframework.stereotype.Controller;

import br.com.avaliacao.supero.model.Person;
import br.com.avaliacao.supero.repositories.PersonDAO;
import br.com.avaliacao.supero.repositories.impl.PersonDAOImpl;
import br.com.avaliacao.supero.utils.FilterPerson;
import br.com.avaliacao.supero.utils.HibernateUtils;

@Controller
public class PersonController{

	PersonDAO personDAO = new PersonDAOImpl();
	
	public Person save(Person person) {
		HibernateUtils.save(person);
		return person;
	}
	
	public Person update(Person person){
		HibernateUtils.update(person);
		return person;
	}

	public Person findById(Long id) {
		return personDAO.findById(id);
	}

	public void remove(Long personId) {
		HibernateUtils.delete(new Person(personId));
	}

	public List<Person> getPersons(FilterPerson filter) {
		return personDAO.getPersons(filter);
	}
	
	public int count(FilterPerson filter){
		return personDAO.count(filter);
	}
	
	public int countExistingCPF(String cpf, Long id){
		return personDAO.countExistingCPF(cpf, id);
	}

}
