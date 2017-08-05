package br.com.avaliacao.softplan.managedBean;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.avaliacao.softplan.model.Person;
import br.com.avaliacao.softplan.model.Task;
import br.com.avaliacao.softplan.utils.BaseBeans;
import br.com.avaliacao.softplan.utils.FilterTask;
import br.com.avaliacao.softplan.utils.LoadConfigs;
import br.com.avaliacao.softplan.utils.UtilsEnum;

@SessionScoped
@ManagedBean(name="taskMB")
public class TaskMB extends BaseBeans{
	
	private static final long serialVersionUID = -2559664938405246442L;
	
	private List<Task> tasks;
	private WebTarget target;
	private FilterTask filter;
	private String statusSelectedString;
	private boolean isEdit;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
	
	private LazyDataModel<Task> model;
	Task task;
	
	@PostConstruct
	public void onLoad() {
		target = LoadConfigs.loadConfigs();
		if(statusSelectedString == null){
			statusSelectedString = "true";
		}
		if(tasks == null){
			tasks = new ArrayList<Task>();
		}
		if(filter == null){
			filter = new FilterTask();
		}
		if(task == null){
			task = new Task();
		}
		if(!isEdit){
			task = new Task();
		}
		updateDataTable();
	}
	
	public void updateDataTable(){
		model = new LazyDataModel<Task>(){
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Task> load(int first, int maxResults, String sortField,
					SortOrder sortOrder, Map<String, String> filters) {
				
				System.out.println("Nova requisição de tarefas");
				if(statusSelectedString.equals("true")){
					filter.setFilterStatus(true);
				}else{
					filter.setFilterStatus(false);
				}
				
				validateEmptyFilters(sortOrder);
				StringBuilder uri = new StringBuilder();
				uri.append("/tasks/findAll/");
				uri.append(maxResults + "/");
				uri.append(first + "/");
				uri.append(filter.getFilterTitle() + "/");
				uri.append(filter.isFilterStatus() + "/");
				String dateFormatCreation = null;
				if(filter.getCreationDate() != null){
					dateFormatCreation = formatter.format(filter.getCreationDate());
				}
				uri.append(dateFormatCreation + "/");
				String dateFormatConclusion = null;
				if(filter.getDateConclusion() != null){
					dateFormatConclusion = formatter.format(filter.getDateConclusion());
				}
				uri.append(dateFormatConclusion + "/");
				uri.append(filter.getOrder() + "/");
				
				try{
					task = target.path(uri.toString()).request().get(Task.class);
				}catch(Exception ex){
					getMessageErrorConnect();
					System.out.println();
				}
				if(task != null && task.getTasks() != null){
					tasks.removeAll(tasks);
					tasks.addAll(task.getTasks());
				}
				
				setRowCount(task.getTotalSize());
				
				return tasks;
			}
			
			private void validateEmptyFilters(SortOrder sortOrder) {
				if(SortOrder.ASCENDING.equals(sortOrder)){
					filter.setOrder("ASC");
				}else{
					filter.setOrder("DESC");
				}
				
				if(filter.getFilterTitle() != null){
					if(filter.getFilterTitle().equals("")){
						filter.setFilterTitle(null);
					}
				}
			}
		};
	}
	
	public void cleanFilters(){
		filter = new FilterTask();
		updateDataTable();
	}
	
	public String saveOrEdit(){
		boolean returnMethod = true;
		if(task != null){
			if(task.getId() == null || task.getId() == 0){
				returnMethod = saveMethod();
			}else{
				returnMethod = edit(true);
			}
		}
		if(returnMethod){
			clean();
		}
		return "/public/task/listTask.faces?faces-redirect=true";
	}

	
	public String redirectListTask() throws IOException{
		clean();
		statusSelectedString = "true";
		updateDataTable();
		return "/public/task/listTask.faces?faces-redirect=true";
	}
	
	public String redirectCadTask() throws IOException{
		clean();
		return "/public/task/cadTask.faces?faces-redirect=true";
	}
	
	public String redirectEditTask(){
		return "/public/task/cadTask.faces?faces-redirect=true";
	}
	
	private boolean saveMethod() {
		task.setStatus(true);
		Entity<Task> entity = Entity.entity(task, MediaType.APPLICATION_XML);
		Response response = null;
		try{
			response = target.path("/tasks").request().post(entity);
		}catch(Exception ex){
			getMessageErrorConnect();
			return false;
		}
		
		boolean returnValue = validateReturn(response);
			return returnValue;
	}

	private boolean validateReturn(Response response) {
		if(response != null){
			if(response.getStatus() == UtilsEnum.OK.value || response.getStatus() == UtilsEnum.CRIADO.value){
				getMessageAddSuccess();
			}else{
				getMessageAddError();
				return false;
			}
		}
		return true;
	}
	
	public String delete(){
		Response response = null;
		if(task != null && task.getId() != null){
			try{
				response = target.path("/tasks/" + task.getId()).request().delete();
			}catch(Exception ex){
				getMessageErrorConnect();
			}
		}else{
			getMessageDeleteError();
		}
		
		if(response != null){
			if(response.getStatus() == UtilsEnum.OK.value){
				getMessageDeleteSuccess();
			}else{
				getMessageDeleteError();
			}
		}
		return "/public/task/listTask.faces?faces-redirect=true";
	}
	
	public boolean edit(boolean ehEdicao){
		Response response  = null;
		Entity<Task> entity = null;
		try{
			if(task.getId() != null){
				if(ehEdicao){
					entity = Entity.entity(task, MediaType.APPLICATION_XML);
					response = target.path("/tasks").request().put(entity);
				}else{
					task.setStatus(false);
					entity = Entity.entity(task, MediaType.APPLICATION_XML);
					response = target.path("/tasks").request().put(entity);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			if(ehEdicao){
				getMessageEditError();
			}else{
				getMessageDeleteError();
			}
			return false;
		}
		
		return returnEditMethod(ehEdicao, response);
	}
	
	public String changeStatus(){
		Response response  = null;
		Entity<Task> entity = null;
		try{
			if(task.getId() != null){
				if(task.isStatus()){
					task.setStatus(false);
				}else{
					task.setStatus(true);
				}
					entity = Entity.entity(task, MediaType.APPLICATION_XML);
					response = target.path("/tasks").request().put(entity);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			getMessageEditStatusError();
		}
		getMessageConclusionSuccess();
		return "/public/listTask.faces?faces-redirect=true";
	}
	

	private boolean returnEditMethod(boolean ehEdicao, Response response) {
		if(response != null){
			if(response.getStatus() == UtilsEnum.OK.value || response.getStatus() == UtilsEnum.CRIADO.value){
				if(ehEdicao){
					getMessageEditSuccess();
				}else{
					getMessageDeleteSuccess();
				}
			}else{
				if(ehEdicao){
					getMessageEditError();
				}else{
					getMessageDeleteError();
				}
				return false;
			}
		}
		return true;
	}
	
	
	private void getMessageAddSuccess() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Tarefa cadastrada com sucesso!",null);
		FacesContext.getCurrentInstance().addMessage("Sucess Message ", msg);
	}
	
	private void getMessageConclusionSuccess() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Tarefa concluída com sucesso!",null);
		FacesContext.getCurrentInstance().addMessage("Sucess Message ", msg);
	}
	
	private void getMessageDeleteSuccess() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Tarefa removida com sucesso!",null);
		FacesContext.getCurrentInstance().addMessage("Sucess Message ", msg);
	}
	
	private void getMessageEditSuccess() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Tarefa editada com sucesso!",null);
		FacesContext.getCurrentInstance().addMessage("Sucess Message ", msg);
	}
	
	private void getMessageAddError() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erro ao cadastrar nova Tarefa!", null);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	private void getMessageDeleteError() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erro ao remover Tarefa com id " + task.getId(), null);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	private void getMessageEditError() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erro ao editar Tarefa com id " + task.getId(), null);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	private void getMessageEditStatusError() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erro ao alterar status da Tarefa com id " + task.getId(), null);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	private void getMessageErrorConnect() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erro de conexão com o servidor!", null);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public void clean(){
		filter = new FilterTask();
		task = new Task();
	}
	
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	public FilterTask getFilter() {
		return filter;
	}
	public void setFilter(FilterTask filter) {
		this.filter = filter;
	}
	public LazyDataModel<Task> getModel() {
		return model;
	}
	public void setModel(LazyDataModel<Task> model) {
		this.model = model;
	}
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public String getStatusSelectedString() {
		return statusSelectedString;
	}
	public void setStatusSelectedString(String statusSelectedString) {
		this.statusSelectedString = statusSelectedString;
	}
	public boolean isEdit() {
		return isEdit;
	}
	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}
}
