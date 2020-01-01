package br.com.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;

public class IDaoPessoaImpl implements IDaoPessoa {

	@Override
	public Pessoa consultarUsuario(String login, String senha) {
		
		Pessoa pessoa = null;
		try{
			

			EntityManager entityManager = JPAUtil.getEntityManager();
			EntityTransaction entityTransaction = entityManager.getTransaction();
			entityTransaction.begin();

			pessoa = (Pessoa) entityManager.createQuery("select u from Pessoa u where u.login = '" + login + "' and u.senha = '" + senha + "'").getSingleResult();

			entityTransaction.commit();
			entityManager.close();

			
		}catch(NoResultException e){
			System.out.println("LOGIN INCORRETO, DEPOIS MONTAR UMA LÓGICA");
		}
		return pessoa;
	}

}
