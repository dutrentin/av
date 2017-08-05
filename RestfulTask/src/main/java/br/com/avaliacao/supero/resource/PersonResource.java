package br.com.avaliacao.supero.resource;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import br.com.avaliacao.supero.controller.PersonController;
import br.com.avaliacao.supero.model.Person;
import br.com.avaliacao.supero.utils.FilterPerson;

@Path("persons")
public class PersonResource {
	
	PersonController personController = new PersonController();

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Person find(@PathParam("id") long id) {
		return personController.findById(id);
	}

	@Path("/findAll/{max}/{page}/{filterName}/{filterCPF}/{filterEmail}/{orderBy}/{filterDate}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Person findAllPersons(@PathParam("max") int maxResults, @PathParam("page") int page, 
			 @PathParam("filterName") String filterName, @PathParam("filterCPF") String filterCPF, 
			 @PathParam("filterEmail") String filterEmail, @PathParam("orderBy") String orderBy, @PathParam("filterDate") String filterDate) throws ParseException{
		
		FilterPerson filter = new FilterPerson();
		
		String dateFormat = "";
		if(!filterDate.equals("null")){
			 dateFormat = filterDate.replaceAll("_", "/");
		}
		
		fillsInData(maxResults, page, filterName, filterCPF, filterEmail,
				orderBy, filter, dateFormat);
		
		
		Person person = new Person();
		person.setPersons(personController.getPersons(filter));
		person.setTotalSize(personController.count(filter));
		return person;
	}


	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	 public Response add(Person person) {
		URI uri = null ;
		if(person != null){
			if(person.getId() != null && person.getId() == 0){
				person.setId(null);
			}
			if(person.getCpf() != null){
				int countExistingCPF = personController.countExistingCPF(person.getCpf(), 0L);
				if(countExistingCPF > 0){
					return Response.status(Response.Status.CONFLICT).build();
				}
			}
			 uri = URI.create("/persons/" + personController.save(person).getId());
		}
		return Response.created(uri).build();
    }
	
	@Path("{id}")
	@DELETE
	public Response remove(@PathParam("id") long id) {
		Person personRemove = personController.findById(id);
		if(personRemove != null){
			personRemove.setAtivo(false);
			personController.update(personRemove);
		}
		return Response.ok().build();
	}
	
	@PUT
	public Response editRemovePerson(Person personEdit) {
		if(personEdit.isAtivo()){
			if(personEdit.getCpf() != null){
				int countExistingCPF = personController.countExistingCPF(personEdit.getCpf(), personEdit.getId());
				if(countExistingCPF > 0){
					return Response.status(Response.Status.CONFLICT).build();
				}
			}
		}
		personController.update(personEdit);
		return Response.ok().build();
	}
	
	private void fillsInData(int maxResults, int page, String filterName,
			String filterCPF, String filterEmail, String orderBy,
			FilterPerson filter, String dateFormat) {
		filter.setCurrentPage(page);
		filter.setMaxResults(maxResults);
		filter.setFilterName(filterName);
		filter.setFilterCPF(filterCPF);
		filter.setFilterEmail(filterEmail);
		filter.setOrder(orderBy);
		filter.setFilterDateString(dateFormat);
	}
}
