package br.com.avaliacao.supero.resource;

import java.net.URI;
import java.text.ParseException;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.avaliacao.supero.controller.TaskController;
import br.com.avaliacao.supero.model.Task;
import br.com.avaliacao.supero.utils.FilterTask;

@Path("tasks")
public class TaskResource {
	
	TaskController taskController = new TaskController();

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Task find(@PathParam("id") long id) {
		return taskController.findById(id);
	}

	@Path("/findAll/{max}/{page}/{filterTitle}/{orderBy}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Task findAlltasks(@PathParam("max") int maxResults, @PathParam("page") int page, 
			 @PathParam("filterTitle") String filterTitle, @PathParam("orderBy") String orderBy) throws ParseException{
		
		FilterTask filter = new FilterTask();
		
		fillsInData(maxResults, page, filterTitle, orderBy, filter);
		
		Task task = new Task();
		task.setTasks(taskController.getTasks(filter));
		task.setTotalSize(taskController.count(filter));
		return task;
	}


	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	 public Response add(Task task) {
		URI uri = null ;
		if(task != null){
			if(task.getId() != null && task.getId() == 0){
				task.setId(null);
			}
			 uri = URI.create("/tasks/" + taskController.save(task).getId());
		}
		return Response.created(uri).build();
    }
	
	@Path("{id}")
	@DELETE
	public Response remove(@PathParam("id") long id) {
		Task taskRemove = taskController.findById(id);
		if(taskRemove != null){
			taskRemove.setStatus(false);
			taskController.update(taskRemove);
		}
		return Response.ok().build();
	}
	
	@PUT
	public Response editRemovetask(Task taskEdit) {
		taskController.update(taskEdit);
		return Response.ok().build();
	}
	
	private void fillsInData(int maxResults, int page, String filterTitle,
			String orderBy,	FilterTask filter) {
		filter.setCurrentPage(page);
		filter.setMaxResults(maxResults);
		filter.setFilterTitle(filterTitle);
		filter.setOrder(orderBy);
	}
}
