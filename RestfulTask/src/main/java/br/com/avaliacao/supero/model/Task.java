package br.com.avaliacao.supero.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "task")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Task implements Serializable{
	private static final long serialVersionUID = 4048407229498398444L;
	
//	@Id
//	@SequenceGenerator( name = "id", sequenceName = "TASK_SEQ", allocationSize = 1 )
//	@GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "id" )
	
	@Id
	@GeneratedValue
	private Long id;
	@Column
	private String title;
	@Column
	private String description;
	@Column
	private Date creationDate;
	@Column
	private Date dateLastEdited;
	@Column
	private Date dateConclusion;
	
	@Column
	private boolean status;
	
	@Transient
	private List<Task> tasks;
	@Transient
	private int totalSize;
	
	public Task(){
		
	}
	
    public Task(Long id){
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getDateLastEdited() {
		return dateLastEdited;
	}
	public void setDateLastEdited(Date dateLastEdited) {
		this.dateLastEdited = dateLastEdited;
	}
	public Date getDateConclusion() {
		return dateConclusion;
	}
	public void setDateConclusion(Date dateConclusion) {
		this.dateConclusion = dateConclusion;
	}
	
}
