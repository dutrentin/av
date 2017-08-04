package br.com.avaliacao.supero.repositories.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Session;

import br.com.avaliacao.supero.model.Person;
import br.com.avaliacao.supero.repositories.PersonDAO;
import br.com.avaliacao.supero.utils.FilterPerson;
import br.com.avaliacao.supero.utils.HibernateUtils;
import br.com.avaliacao.supero.utils.ValidateUtils;

public class PersonDAOImpl implements PersonDAO{
	
	EntityManager em = null;
	
	
	public void add(Person person) {
		HibernateUtils.save(person);
	}

	public Person findById(Long id) {
		Person person = null;
		try{
			em = HibernateUtils.getConnection();
			person = em.find(Person.class, id);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return person;
	}

	public void remove(Person person) {
		HibernateUtils.delete(person);
	}

	public List<Person> getPersons(FilterPerson filter) {
		List<Person> persons = new ArrayList<Person>();
		StringBuilder hql = new StringBuilder();
		try{
			em = HibernateUtils.getConnection();
			hql.append("from Person as p ");
			hql.append("where p.ativo = true ");
			
			mountFilters(filter, hql);
			
			Query query = em.createQuery(hql.toString());
			query.setFirstResult(filter.getCurrentPage());
			query.setMaxResults(filter.getMaxResults());
			
			if(query.getResultList().size() > 0){
				persons = (ArrayList<Person>) query.getResultList();
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return persons;
	}
	
	private void mountFilters(FilterPerson filter, StringBuilder hql) {
		if(ValidateUtils.validateString(filter.getFilterName())){
			hql.append(" and lower(p.nome) like lower('%" + filter.getFilterName() + "%')");
		}
		if(ValidateUtils.validateString(filter.getFilterCPF())){
			hql.append(" and p.cpf like '%" + filter.getFilterCPF() + "%'");
		}
		if(ValidateUtils.validateString(filter.getFilterEmail())){
			hql.append(" and lower(p.email) like lower('%" + filter.getFilterEmail() + "%')");
		}
		if(ValidateUtils.validateString(filter.getFilterDateString())){
//			postgres
			hql.append(" and to_char(p.dataNascimento,'dd/MM/yyyy') = '" + filter.getFilterDateString()+ "'");
//			mysql
//			hql.append(" and DATE(p.dataNascimento) = '" + filter.getFilterDateString()+ "'");
		}
	}
	
	public int count(FilterPerson filter) {
		StringBuilder hql = new StringBuilder();
		int count = 0;
		try{
			hql.append("select p from Person as p ");
			hql.append("where p.ativo = true ");
			mountFilters(filter, hql);
			
			Query query = em.createQuery(hql.toString());
		    count = query.getResultList().size();
		    System.out.println();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return count ;
	}

	public int countExistingCPF(String cpf, Long id) {
		em = HibernateUtils.getConnection();
		StringBuilder hql = new StringBuilder();
		int count = 0;
		try{
			hql.append("select p from Person as p ");
			hql.append("where p.ativo = true and p.cpf = :CPF and p.id <> :ID ");
			
			Query query = em.createQuery(hql.toString());
			query.setParameter("CPF", cpf);
			query.setParameter("ID", id);
		    count = query.getResultList().size();
		    System.out.println();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return count ;
	}

}
