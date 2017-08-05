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

	@Path("/findAll/{max}/{page}/{filterTitle}/{filterStatus}/{creationDate}/{conclusionDate}/{orderBy}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Task findAlltasks(@PathParam("max") int maxResults, @PathParam("page") int page, 
			 @PathParam("filterTitle") String filterTitle, @PathParam("filterStatus") boolean filterStatus,
			 @PathParam("creationDate") String creationDate,@PathParam("conclusionDate") String conclusionDate,
			 @PathParam("orderBy") String orderBy) throws ParseException{
		
		FilterTask filter = new FilterTask();
		
		String dateFormatCreation = "";
		if(!creationDate.equals("null")){
			dateFormatCreation = creationDate.replaceAll("_", "/");
		}
		
		String dateFormatConclusion = "";
		if(!conclusionDate.equals("null")){
			dateFormatConclusion = conclusionDate.replaceAll("_", "/");
		}
		
		fillsInData(maxResults, page, filterTitle,filterStatus, dateFormatCreation, dateFormatConclusion, orderBy, filter);
		
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
			task.setCreationDate(new Date());
			try{
			 uri = URI.create("/tasks/" + taskController.save(task).getId());
			}catch(Exception ex){
				ex.printStackTrace();
				return Response.status(Response.Status.CONFLICT).build();
			}
		}
		return Response.created(uri).build();
    }
	
	@Path("{id}")
	@DELETE
	public Response remove(@PathParam("id") long id) {
		Task taskRemove = taskController.findById(id);
		if(taskRemove != null){
			try{
				taskController.remove(taskRemove);
			}catch(Exception ex){
				ex.printStackTrace();
				Response.status(Response.Status.CONFLICT).build();
			}
			return Response.ok().build();
		}
		return Response.status(Response.Status.CONFLICT).build();
	}
	
	@PUT
	public Response edittask(Task taskEdit) {
		if(taskEdit != null){
			if(taskEdit.isStatus()){
				taskEdit.setDateConclusion(null);
			}else{
				taskEdit.setDateConclusion(new Date());
			}
			
			taskEdit.setDateLastEdited(new Date());	
			taskController.update(taskEdit);
			return Response.ok().build();
		}
		return Response.status(Response.Status.CONFLICT).build();
	}
	
	private void fillsInData(int maxResults, int page, String filterTitle, boolean filterStatus, String createDate, String conclusionDate,
			String orderBy,	FilterTask filter) {
		filter.setCurrentPage(page);
		filter.setMaxResults(maxResults);
		filter.setFilterTitle(filterTitle);
		filter.setFilterStatus(filterStatus);
		filter.setCreationDateString(createDate);
		filter.setDateConclusionString(conclusionDate);
		filter.setOrder(orderBy);
	}
}
