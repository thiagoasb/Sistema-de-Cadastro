package br.com.cursojsf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped; //mantem o objeto, até que o cliente navegue para a proxima tela
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

import br.com.dao.DaoGeneric;
import br.com.entidades.Cidades;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;
import br.com.repository.IDaoPessoa;
import br.com.repository.IDaoPessoaImpl;
import br.com.entidades.Estados;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {
	
	private Pessoa pessoa = new Pessoa();
	private DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();
	
	private List<SelectItem> estados;
	
	private List<SelectItem> cidades;
	
	public String salvar(){
		pessoa = daoGeneric.merge(pessoa);
		carregarPessoas();
		mostrarMsg("Cadastrado com sucesso!");
		
		return "";
	}
	
	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
		
	}

	public String novo(){
		pessoa = new Pessoa();
		return "";
	}
	
	public String remove(){
		daoGeneric.deletePorId(pessoa);
		pessoa = new Pessoa();
		carregarPessoas();
		mostrarMsg("Removido com sucesso!");
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
		
		//System.out.println(pessoa.getLogin() + "  " + pessoa.getSenha());
		
		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());
		
		if(pessoaUser != null){//achou usuario
			//adicionar o usuário na sessão usuarioLogado
			FacesContext context = FacesContext.getCurrentInstance(); //para pegar alguma informação do ambiente de execução do jsf
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", pessoaUser); 
			
			
			return "primeirapagina.jsf";
		}
		
		return "index.jsf";
	}
	
	public String deslogar(){
		
		FacesContext context = FacesContext.getCurrentInstance(); //para pegar alguma informação do ambiente de execução do jsf
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("usuarioLogado"); 
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) context.getExternalContext().getRequest();
		
		httpServletRequest.getSession().invalidate();
		
		
		return "index.jsf";
	}
	
	public boolean permiteAcesso(String acesso){
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa)externalContext.getSessionMap().get("usuarioLogado"); 
		
		return pessoaUser.getPerfilUser().equals(acesso);
	}
	
	public void pesquisaCep(AjaxBehaviorEvent event){
		try {
			URL url = new URL("https://viacep.com.br/ws/" + pessoa.getCep() + "/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			String cep = "";
			StringBuilder jsonCep = new StringBuilder();
			
			while((cep = br.readLine()) != null){
				jsonCep.append(cep);
			}
			
			Pessoa gsonAux = new Gson().fromJson(jsonCep.toString(), Pessoa.class); //recebe o json e a classe q ele vai converter
			
			pessoa.setCep(gsonAux.getCep());
			pessoa.setLogradouro(gsonAux.getLogradouro());
			pessoa.setComplemento(gsonAux.getComplemento());
			pessoa.setBairro(gsonAux.getBairro());
			pessoa.setLocalidade(gsonAux.getLocalidade());
			pessoa.setUf(gsonAux.getUf());
			pessoa.setIbge(gsonAux.getIbge());
			pessoa.setGia(gsonAux.getGia());
			
			
			System.out.println(gsonAux);
			
		} catch (Exception e) {
			mostrarMsg("Erro ao consultar o CEP");
		}
	}
	
	public List<SelectItem> getEstados() {
		estados = iDaoPessoa.listaEstados();
		return estados;
	}
	
	public void carregaCidades(AjaxBehaviorEvent event){
		System.out.println("código do evento dos Estados: " + event.getComponent().getAttributes().get("submittedValue"));
		
		String codigoEstado = (String) event.getComponent().getAttributes().get("submittedValue");
		if(codigoEstado != null){
			Estados estado = JPAUtil.getEntityManager().find(Estados.class, Long.parseLong(codigoEstado));
			
			if(estado != null){
				pessoa.setEstados(estado);
				
				List<Cidades> cidades = JPAUtil.getEntityManager().createQuery(" from Cidades where estados.id = " + codigoEstado).getResultList();
				List<SelectItem> selectItensCidades = new ArrayList<SelectItem>();
				
				for (Cidades cidade : cidades) {
					selectItensCidades.add(new SelectItem(cidade.getId(), cidade.getNome()));
				}
				
				setCidades(selectItensCidades);
			}
		}
		
	}

	public List<SelectItem> getCidades() {
		return cidades;
	}

	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
	}
	
	
}
