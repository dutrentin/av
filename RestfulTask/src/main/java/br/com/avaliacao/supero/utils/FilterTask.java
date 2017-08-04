package br.com.avaliacao.supero.utils;

import java.io.Serializable;

public class FilterTask implements Serializable{
	
	private static final long serialVersionUID = 7833360363874843256L;
	
	private String filterTitle;
	private boolean filterStatus;
	private int maxResults;
	private int currentPage;
	private String order;
	
	
	public String getFilterTitle() {
		return filterTitle;
	}
	public void setFilterTitle(String filterTitle) {
		this.filterTitle = filterTitle;
	}
	public boolean isFilterStatus() {
		return filterStatus;
	}
	public void setFilterStatus(boolean filterStatus) {
		this.filterStatus = filterStatus;
	}
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	
	
     
}
