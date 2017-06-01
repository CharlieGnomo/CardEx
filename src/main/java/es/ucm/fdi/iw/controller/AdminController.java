package es.ucm.fdi.iw.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.crawler.MagicTgIoAPI;
import es.ucm.fdi.iw.model.Carta;
import es.ucm.fdi.iw.model.Edicion;
import es.ucm.fdi.iw.model.Usuario;

@Controller	
@RequestMapping("admin")
public class AdminController {
	@Autowired
	private EntityManager entityManager; 
	
	// Incluimos ${prefix} en todas las páginas
	@ModelAttribute
	public void addAttributes(Model m) {
		m.addAttribute("prefix", "../static/");
		m.addAttribute("prefijo", "../");
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping({"", "/"})
	public String root(Model model,HttpSession session) {
		List<String> listaCSS = new ArrayList<String>();
		listaCSS.add("adminStyles.css");
 
		List<String> listaJS = new ArrayList<String>();
		listaJS.add("jquery-3.1.1.min.js");
		listaJS.add("bootstrap.min.js");
 
		model.addAttribute("pageExtraCSS", listaCSS);
		model.addAttribute("pageExtraScripts", listaJS);
		
		if (session.getAttribute("user") == null) {
			return "redirect:index";
		}
		
		List<Usuario> usuarios = entityManager.createNamedQuery("getUsers").getResultList();
		List<Edicion> ediciones = entityManager.createNamedQuery("getSets").getResultList();
		
		model.addAttribute("usuarios",usuarios);
		model.addAttribute("ediciones",ediciones);
	
		return "admin";
	}
	
	@PostMapping("/updateCardSet")
	@Transactional
	public String actualizaCartasEdicion(@RequestParam("code") String code){
		MagicTgIoAPI api = new MagicTgIoAPI();
		Collection<Carta> cartasAPI = api.getCartasPorEdicion(code);
		
		for(Carta c : cartasAPI){
			if(!existeEnBD(c.getMultiverseid()))
				entityManager.persist(c);
		}
		
		return "redirect:";
	}
	
	@PostMapping("/updateSets")
	@Transactional
	public String actualizaEdiciones(){
		MagicTgIoAPI api = new MagicTgIoAPI();
		Collection<Edicion> edicionesAPI = api.getEdiciones();
		Collection<Edicion> edicionesBD = entityManager.createNamedQuery("getSets").getResultList();
 		
		for(Edicion edicion : edicionesAPI){
			if(!existeEnBD(edicionesBD, edicion.getCode()))
				entityManager.persist(edicion);
		}
		
		return "redirect:";
	}
	
	private boolean existeEnBD(String multiverseId){
		Query c = entityManager.createNamedQuery("findCardByMultiverseID").setParameter("paramMultiverse", multiverseId);
		
		if(c.getFirstResult() == 0)
			return false;
		else
			return true;
	}
	
	private boolean existeEnBD(Collection<Edicion> edicionesBD, String code){
		for(Edicion edicion : edicionesBD){
			if(edicion.getCode().equals(code))
				return true;
		}
		
		return false;
	}
}
