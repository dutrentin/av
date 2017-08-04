package br.com.avaliacao.supero.controller;

import java.util.List;

import org.springframework.stereotype.Controller;

import br.com.avaliacao.supero.model.Task;
import br.com.avaliacao.supero.repositories.TaskDAO;
import br.com.avaliacao.supero.repositories.impl.TaskDAOImpl;
import br.com.avaliacao.supero.utils.FilterTask;
import br.com.avaliacao.supero.utils.HibernateUtils;

@Controller
public class TaskController {

    TaskDAO taskDAO = new TaskDAOImpl();
	
	public Task save(Task Task) {
		HibernateUtils.save(Task);
		return Task;
	}
	
	public Task update(Task Task){
		HibernateUtils.update(Task);
		return Task;
	}

	public Task findById(Long id) {
		return taskDAO.findById(id);
	}

	public void remove(Long TaskId) {
		HibernateUtils.delete(new Task(TaskId));
	}

	public List<Task> getTasks(FilterTask filter) {
		return taskDAO.getTasks(filter);
	}
	
	public int count(FilterTask filter){
		return taskDAO.count(filter);
	}
	
	
}
