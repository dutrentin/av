package br.com.avaliacao.supero.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HibernateUtils {
	static EntityManager em = null;
	
	public static EntityManager getConnection(){
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("avaliacaoSoftplan");
		return factory.createEntityManager();
	}
	
	public static void save(Object obj){
		try{
			em = getConnection();
			em.getTransaction().begin();
			em.persist(obj);
			em.getTransaction().commit();
			em.close();
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Não foi possível salvar o objeto. Verifique os dados de conexão do banco.");
		}
	}
	
	public static void update(Object obj){
		try{
			em = getConnection();
			em.getTransaction().begin();
			em.merge(obj);
			em.getTransaction().commit();
			em.close();
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Não foi possível atualizar o objeto. Verifique os dados de conexão do banco.");
		}
	}
	
	public static <T> void delete(Object obj){
		try{
			em = getConnection();
			em.getTransaction().begin();
			em.remove(obj);
			em.getTransaction().commit();
			em.close();
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Não foi possível excluir o objeto. Verifique os dados de conexão do banco.");
		}
	}

}
