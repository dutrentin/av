package br.com.avaliacao.supero.managedBean;

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

import br.com.avaliacao.supero.model.Task;
import br.com.avaliacao.supero.utils.BaseBeans;
import br.com.avaliacao.supero.utils.FilterTask;
import br.com.avaliacao.supero.utils.LoadConfigs;
import br.com.avaliacao.supero.utils.UtilsEnum;

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
	
	/**
	 * método usado para preenchimento de datatable com conexão ao Restful baseado nos parametros informados na busca em tela
	 */
	public void updateDataTable(){
		model = new LazyDataModel<Task>(){
			private static final long serialVersionUID = 1L;
			
			/**
			 * método nativo primefaces para carregamento lazy(utlizado para paginamento) de datatable
			 */
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
					System.out.println("Erro ao conectar com restful");
				}
				if(task != null && task.getTasks() != null){
					tasks.removeAll(tasks);
					tasks.addAll(task.getTasks());
				}
				
				setRowCount(task.getTotalSize());
				
				return tasks;
			}
			/**
			 * método usado para validar os filtros e garantir que o titulo passe nulo e não vazio para o Restful
			 * @param SortOrder - Filtros utilizados pela datatable para carater de ordenação
			 */
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
	
	/**
	 * método usado limpeza dos filtros ao acionar botão LIMPAR. Atualizada a datatable sem filters
	 */
	public void cleanFilters(){
		filter = new FilterTask();
		statusSelectedString = "true";
		updateDataTable();
	}
	
	/**
	 * método usado para salvar ou editar um objeto Task. O método valida
	 * e retorna mensagens conforme ação tomada
	 * @return String para redirecionamento de página
	 */
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

	
	/**
	 * método usado para limpeza de objeto em session, atualização de datable e redirecionamento
	 * @return String página de redirecionamento
	 * @throws IOException
	 */
	public String redirectListTask() throws IOException{
		clean();
		statusSelectedString = "true";
		updateDataTable();
		return "/public/task/listTask.faces?faces-redirect=true";
	}
	
	/**
	 * método usado para limpeza de objeto em session e redirecionamento
	 * @return String página de redirecionamento
	 * @throws IOException
	 */
	public String redirectCadTask() throws IOException{
		clean();
		return "/public/task/cadTask.faces?faces-redirect=true";
	}
	
	/**
	 * método usado para redirecionamento de edição de tarefa
	 * @return String página de redirecionamento
	 */
	public String redirectEditTask(){
		return "/public/task/cadTask.faces?faces-redirect=true";
	}
	
	/**
	 * método chamado por saveOrEdit() caso seja um novo objeto do tipo Task
	 * @return boolean para apresentação de mensagem de erro ou sucesso
	 */
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

	/**
	 * método chamado por saveMethod() para validação de tipo de mensagem a ser apresentada
	 * @param Response retorno do restful para informar erro ou sucesso
	 * @return boolean para apresentação de mensagem de erro ou sucesso
	 */
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
	
	/**
	 * método chamado por pelo botão EXCLUIR submetido em tela afim de exclusão direta de Tarefa
	 * @return String redirecionamento para atualização de página
	 */
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
	
	/**
	 * método chamado por edit() caso seja um objeto existente do tipo Task
	 * @param boolean informando se é edição de dados da Task ou será alteração de status (conclusão da Task)
	 * @return boolean para apresentação de mensagem de erro ou sucesso
	 */
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
	
	/**
	 * método chamado por edit(ehEdicao) para validação de tipo de mensagem a ser apresentada
	 * @param Response retorno do restful para informar erro ou sucesso
	 * @return boolean para apresentação de mensagem de erro ou sucesso
	 */
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
	
	/**
	 * método chamado pelo botão Finalizar submetido em tela
	 * @return String para redirecionamento e atualização de página
	 */
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
	
	/**
	 * método de injeção de mensagem no componente messages primefaces
	 */
	private void getMessageAddSuccess() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Tarefa cadastrada com sucesso!",null);
		FacesContext.getCurrentInstance().addMessage("Sucess Message ", msg);
	}
	/**
	 * método de injeção de mensagem no componente messages primefaces
	 */
	private void getMessageConclusionSuccess() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Tarefa concluída com sucesso!",null);
		FacesContext.getCurrentInstance().addMessage("Sucess Message ", msg);
	}
	/**
	 * método de injeção de mensagem no componente messages primefaces
	 */
	private void getMessageDeleteSuccess() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Tarefa removida com sucesso!",null);
		FacesContext.getCurrentInstance().addMessage("Sucess Message ", msg);
	}
	/**
	 * método de injeção de mensagem no componente messages primefaces
	 */
	private void getMessageEditSuccess() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Tarefa editada com sucesso!",null);
		FacesContext.getCurrentInstance().addMessage("Sucess Message ", msg);
	}
	/**
	 * método de injeção de mensagem no componente messages primefaces
	 */
	private void getMessageAddError() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erro ao cadastrar nova Tarefa!", null);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	/**
	 * método de injeção de mensagem no componente messages primefaces
	 */
	private void getMessageDeleteError() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erro ao remover Tarefa com id " + task.getId(), null);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	/**
	 * método de injeção de mensagem no componente messages primefaces
	 */
	private void getMessageEditError() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erro ao editar Tarefa com id " + task.getId(), null);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	/**
	 * método de injeção de mensagem no componente messages primefaces
	 */
	private void getMessageEditStatusError() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erro ao alterar status da Tarefa com id " + task.getId(), null);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	/**
	 * método de injeção de mensagem no componente messages primefaces
	 */
	private void getMessageErrorConnect() {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erro de conexão com o servidor!", null);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	/**
	 * método de injeção de mensagem no componente messages primefaces
	 */
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
