package br.com.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;

@WebFilter(urlPatterns={"/*"})
public class FilterAutenticacao implements Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override //executa em todas as requisicoes
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		System.err.println("Invocando filter");
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();
		
		String usuarioLogado = (String) session.getAttribute("usuarioLogado");
		
		String url = req.getServletPath(); //endereço da url
		
		if(!url.equalsIgnoreCase("index.jsf") && usuarioLogado == null){
			RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsf");
			dispatcher.forward(request, response); //termina a requisicao
			return; //para parar a execucao do java
		} else {
			//executa as acoes do request e response
			chain.doFilter(request, response);
		}
		
	}

	@Override //executa quano está subindo o servidor
	public void init(FilterConfig arg0) throws ServletException {
		JPAUtil.getEntityManager();
	}

}
