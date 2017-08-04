package br.com.avaliacao.supero.repositories;

import java.util.List;

import br.com.avaliacao.supero.model.Task;
import br.com.avaliacao.supero.utils.FilterTask;

public interface TaskDAO {

    public void add(Task task);
	
	public Task findById(Long id);
	
	public void remove(Task task);
	
	public List<Task> getTasks(FilterTask filter);
	
	public int count(FilterTask filter);
	
}
