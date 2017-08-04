package br.com.avaliacao.supero.repositories.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.avaliacao.supero.model.Task;
import br.com.avaliacao.supero.repositories.TaskDAO;
import br.com.avaliacao.supero.utils.FilterTask;
import br.com.avaliacao.supero.utils.HibernateUtils;
import br.com.avaliacao.supero.utils.ValidateUtils;

public class TaskDAOImpl implements TaskDAO{

	EntityManager em = null;
	
	public void add(Task task) {
		HibernateUtils.save(task);
	}

	public Task findById(Long id) {
		Task task = null;
		try{
			em = HibernateUtils.getConnection();
			task = em.find(Task.class, id);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return task;
	}

	public void remove(Task task) {
		HibernateUtils.delete(task);
	}

	public List<Task> getTasks(FilterTask filter) {
		List<Task> Tasks = new ArrayList<Task>();
		StringBuilder hql = new StringBuilder();
		try{
			em = HibernateUtils.getConnection();
			hql.append("from Task as t ");
			hql.append("where t.status = true ");
			
			mountFilters(filter, hql);
			
			Query query = em.createQuery(hql.toString());
			query.setFirstResult(filter.getCurrentPage());
			query.setMaxResults(filter.getMaxResults());
			
			if(query.getResultList().size() > 0){
				Tasks = (ArrayList<Task>) query.getResultList();
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return Tasks;
	}

	private void mountFilters(FilterTask filter, StringBuilder hql) {
		if(ValidateUtils.validateString(filter.getFilterTitle())){
			hql.append(" and lower(p.title) like lower('%" + filter.getFilterTitle() + "%')");
		}
	}
	
	public int count(FilterTask filter) {
		StringBuilder hql = new StringBuilder();
		int count = 0;
		try{
			hql.append("select t from Task as t ");
			hql.append("where t.status = true ");
			mountFilters(filter, hql);
			
			Query query = em.createQuery(hql.toString());
		    count = query.getResultList().size();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return count ;
	}

}
