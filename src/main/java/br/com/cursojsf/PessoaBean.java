package br.com.cursojsf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped; //mantem o objeto, até que o cliente navegue para a proxima tela
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import br.com.dao.DaoGeneric;
import br.com.entidades.Pessoa;
import br.com.repository.IDaoPessoa;
import br.com.repository.IDaoPessoaImpl;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {
	
	private Pessoa pessoa = new Pessoa();
	private DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();
	
	public String salvar(){
		pessoa = daoGeneric.merge(pessoa);
		carregarPessoas();
		
		return "";
	}
	
	public String novo(){
		pessoa = new Pessoa();
		return "";
	}
	
	public String remove(){
		daoGeneric.deletePorId(pessoa);
		pessoa = new Pessoa();
		carregarPessoas();
		return "";
	}
	
	@PostConstruct //faz carregar automaticamente
	public void carregarPessoas(){
		pessoas = daoGeneric.getListEntity(Pessoa.class);
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public DaoGeneric<Pessoa> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Pessoa> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}
	
	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}
	
	public List<Pessoa> getPessoas() {
		return pessoas;
	}
	
	public String logar(){
		
		System.out.println(pessoa.getLogin() + "  " + pessoa.getSenha());
		
		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());
		
		if(pessoaUser != null){//achou usuario
			//adicionar o usuário na sessão usuarioLogado
			FacesContext context = FacesContext.getCurrentInstance(); //para pegar alguma informação do ambiente de execução do jsf
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", pessoaUser.getLogin()); 
			
			
			return "primeirapagina.jsf";
		}
		
		return "index.jsf";
	}
	
	
}
