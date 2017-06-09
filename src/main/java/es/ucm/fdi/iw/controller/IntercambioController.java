package es.ucm.fdi.iw.controller;

import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.model.CartaPropia;
import es.ucm.fdi.iw.model.Intercambio;
import es.ucm.fdi.iw.model.Usuario;

@Controller
@RequestMapping("intercambio")
public class IntercambioController {

	@Autowired
	private EntityManager entityManager;

	// Incluimos ${prefix} en todas las páginas
	@ModelAttribute
	public void addAttributes(Model m) {
		m.addAttribute("prefix", "../static/");
		m.addAttribute("prefijo", "../");
	}

	@RequestMapping(value = "/{id}")
	public String root(@PathVariable("id") long usuarioExchange,Model model, Principal principal,
		HttpSession session) {
			
		Usuario usuarioIntercambio = entityManager.find(Usuario.class, usuarioExchange);

		añadirCSSyJSAlModelo(model);
		model.addAttribute("usuarioIntercambio", usuarioIntercambio);
		
		return "intercambio";
	}
	
	@PostMapping("/ofrecer")
	@Transactional
	public String ofrecerIntercambio (@RequestParam("quantityO[]") Integer[] cantidadCartasOfrecidas,
	@RequestParam("cartasO[]") long[] cartasOfrecidas,
	@RequestParam("quantityP[]") Integer[] cantidadCartasPido,
	@RequestParam("cartasP[]") long[] cartasPido,
	@RequestParam("usuarioQuePido") String usuarioQuieroIntercambio,
	Principal principal, HttpSession session)
	{

		//Usuarios
		Usuario usuarioActual = (Usuario) session.getAttribute("user");
		Usuario usuarioIntercambio = (Usuario) entityManager.createNamedQuery("userByUserField")
				.setParameter("userParam",usuarioQuieroIntercambio).getSingleResult();
				
		//Listas
		List<CartaPropia> listaCartasOfrecidas = new  ArrayList<CartaPropia>();
		List<CartaPropia> listaCartasPedidas = new  ArrayList<CartaPropia>();
		
		List<CartaPropia> listaCartasPropiasUsuarioActual = usuarioActual.getCartasPropias();
		List<CartaPropia> listaCartasPropiasUsuarioIntercambio = usuarioIntercambio.getCartasPropias();
		
		//Rellenamos la lista de cartas Ofrecidas
		for(int i=0; i < cartasOfrecidas.length; i++){
			CartaPropia carta =  (CartaPropia) entityManager.find(CartaPropia.class, cartasOfrecidas[i]);
			
			int index = busquedaEnLista(listaCartasPropiasUsuarioActual,carta);
			
			//Ofrezco menos de las que tengo
			if(cantidadCartasOfrecidas[i] < carta.getCantidad())
			{
				//Carta duplicada
				CartaPropia cartaDuplicada = duplicateCard(carta);
				cartaDuplicada.setCantidad(carta.getCantidad() - cantidadCartasOfrecidas[i]);
				entityManager.persist(cartaDuplicada);
				entityManager.flush();
				
				//Actualiza la carta a InExchange y su cantidad
				listaCartasPropiasUsuarioActual.get(index).setCantidad(cantidadCartasOfrecidas[i]);
				listaCartasPropiasUsuarioActual.get(index).setInExchange(true);

				//Añado a la lista del usuario tras la división
				listaCartasPropiasUsuarioActual.add(cartaDuplicada);
			}else{
				//Actualiza la carta a InExchange
				listaCartasPropiasUsuarioActual.get(index).setInExchange(true);
				
				entityManager.merge(listaCartasPropiasUsuarioActual.get(index));
				entityManager.flush();

			}
			//Añado a la lista de cartas intercambio
				listaCartasOfrecidas.add(listaCartasPropiasUsuarioActual.get(index));
		}		
		
		//Rellenamos la lista de cartas Pedidas
		for(int j=0; j<cartasPido.length; j++){
			CartaPropia carta =  (CartaPropia) entityManager.find(CartaPropia.class, cartasPido[j]);
			
			int index = busquedaEnLista(listaCartasPropiasUsuarioIntercambio,carta);
			
			//Piden menos de las que tengo
			if(cantidadCartasPido[j] < carta.getCantidad())
			{
				//Carta duplicada
				CartaPropia cartaDuplicada = duplicateCard(carta);
				cartaDuplicada.setCantidad(carta.getCantidad() - cantidadCartasPido[j]);
				entityManager.persist(cartaDuplicada);
				entityManager.flush();
				
				//Actualiza la carta a InExchange y su cantidad
				listaCartasPropiasUsuarioIntercambio.get(index).setCantidad(cantidadCartasPido[j]);
				listaCartasPropiasUsuarioIntercambio.get(index).setInExchange(true);
				
				//Añado a la lista del usuario tras la división
				listaCartasPropiasUsuarioIntercambio.add(cartaDuplicada);
			}else{
				//Actualiza la carta a InExchange
				listaCartasPropiasUsuarioIntercambio.get(index).setInExchange(true);
			}
			entityManager.merge(listaCartasPropiasUsuarioIntercambio.get(index));
			entityManager.flush();
			
			listaCartasPedidas.add(carta);	
		} 
		
		//Actualizo las cartasPropias de los usuarios del intercambio
		usuarioActual.setCartasPropias(listaCartasPropiasUsuarioActual);
		usuarioIntercambio.setCartasPropias(listaCartasPropiasUsuarioIntercambio);
		entityManager.merge(usuarioActual);
		entityManager.merge(usuarioIntercambio);
		entityManager.flush();
		
		//Creo el intercambio
		Intercambio intercambio = new Intercambio(usuarioActual,usuarioIntercambio,"Pendiente",new Date(Calendar.getInstance().getTime().getTime()),usuarioActual);
		entityManager.persist(intercambio);
		entityManager.flush();	
		intercambio.setCartasOfrecidas(listaCartasOfrecidas);
		intercambio.setCartasRecibidas(listaCartasPedidas);	
		entityManager.persist(intercambio);
		entityManager.flush();

		//Debido a que lo he modificado
		actualizaUsuarioSesion(session,usuarioActual);
		
		return "redirect:../historial";
	}
	
	@RequestMapping(value = "/contraOferta/{id}")
	public String contraoferta(@PathVariable("id") long intercambioId,Model model, Principal principal,
		HttpSession session) 
	{
		Intercambio inter = entityManager.find(Intercambio.class, intercambioId);
		Usuario actual = (Usuario) session.getAttribute("user");
		
		//Control de errores y/o usuario se equivoca con la ruta o quiere hacer trampas
		if(!inter.getEstadoIntercambio().equals("Pendiente") || (
		inter.getUsuarioOfrece().getId() != actual.getId() &&
		inter.getUsuarioRecibe().getId() != actual.getId()))
		{
			return "redirect:../historial";
		}
		model.addAttribute("intercambio",inter);
		añadirCSSyJSAlModelo(model);
		model.addAttribute("contraOferta",true);
	

		Usuario usuarioContrario = actual.getId() == inter.getUsuarioRecibe().getId() ? inter.getUsuarioOfrece() : inter.getUsuarioRecibe();
		model.addAttribute("usuarioIntercambio", usuarioContrario);
		
		model.addAttribute("prefijo", "../../");
		return "intercambio";
	}
	
	@PostMapping("/reofrecer")
	@Transactional
	public String reofrecerIntercambio (@RequestParam("quantityO[]") Integer[] cantidadCartasOfrecidas,
	@RequestParam("cartasO[]") long[] cartasOfrecidas,
	@RequestParam("quantityP[]") Integer[] cantidadCartasPido,
	@RequestParam("cartasP[]") long[] cartasPido,
	@RequestParam("usuarioQuePido") String usuarioQuieroIntercambio,
	@RequestParam("intercambioID") long intercambioId,
	Principal principal, HttpSession session)
	{
		
		Intercambio inter = entityManager.find(Intercambio.class, intercambioId);
		
		Usuario actual = (Usuario) session.getAttribute("user");
		Usuario contrario = inter.getUsuarioOfrece().getId() == actual.getId() ? inter.getUsuarioRecibe() : inter.getUsuarioOfrece();
		
		List<CartaPropia> listaCartasOfrecidas = new ArrayList<CartaPropia>();
		List<CartaPropia> listaCartasPedidas = new ArrayList<CartaPropia>();
		

		for(int i=0; i < cartasOfrecidas.length; i++){
			
			CartaPropia cartaOfrecida =  (CartaPropia) entityManager.find(CartaPropia.class, cartasOfrecidas[i]);
			if(actual.getId() == inter.getUsuarioOfrece().getId())
			{
				if(cartaOfrecida.isInExchange())
				{
					cartaOfrecida.setCantidad(cantidadCartasOfrecidas[i]);
					entityManager.persist(cartaOfrecida);
					entityManager.flush();
				}else{
					
				}
			}
			else{
				if(cartaOfrecida.isInExchange())
				{
					
				}else{
					
				}
			}
			
			listaCartasOfrecidas.add(cartaOfrecida);
		}
		
		for(int i=0; i < cartasPido.length; i++) {
		
			CartaPropia cartaPedida =  (CartaPropia) entityManager.find(CartaPropia.class, cartasPido[i]);
			
			listaCartasPedidas.add(cartaPedida);
		}
		
		
		inter.setCartasOfrecidas(listaCartasOfrecidas);
		inter.setCartasRecibidas(listaCartasPedidas);
		inter.setUsuarioRealizaUltimaAccion(actual);

		entityManager.merge(actual);
		entityManager.merge(contrario);
		entityManager.merge(inter);
		entityManager.flush();
		
		actualizaUsuarioSesion(session, actual);
		
		return "redirect:../historial";
	}
	
	public static void añadirCSSyJSAlModelo(Model model) {
		List<String> listaCSS = new ArrayList<String>();
		listaCSS.add("intercambioStyles.css");

		List<String> listaJS = new ArrayList<String>();
		listaJS.add("jquery-3.1.1.min.js");
		listaJS.add("bootstrap.min.js");
		
		model.addAttribute("pageExtraCSS", listaCSS);
		model.addAttribute("pageExtraScripts", listaJS);
	}
	
	  private void actualizaUsuarioSesion(HttpSession session, Usuario u) { 
	 // Actualizo el usuario de la sesión 
		  session.setAttribute("user", entityManager.find(Usuario.class, u.getId()));
	}
	  
	  /*Método para duplicar cartas. Usado para cuando se realiza un intercambio y queremos
	   * diferenciar entre cartas en intercambios y las que no
	   * */
	  private CartaPropia duplicateCard(CartaPropia c)
	  {
		  return new CartaPropia(c.getCarta(),c.getEstadoCarta(),c.getCantidad(),c.getUsuarioPropietario(),false);
	  }
	  
	  private int busquedaEnLista(List<CartaPropia> lista, CartaPropia c)
	  {
		  for(CartaPropia aux : lista)
		  {
			  if(aux.getId() == c.getId())
			  {
				  return lista.indexOf(aux);
			  }
		  }
		  return -1;
	  }
	 
	  	/*Método para realizar la acción contraria a duplicateCard
	  	 * */
		@Transactional
		public boolean juntarDosCartasIguales(List<CartaPropia> listaCartas,CartaPropia copia, Object object)
		{
			for(CartaPropia original : listaCartas)
			{
				if(original.getCarta().getId() == copia.getCarta().getId() &&
						original.getEstadoCarta() == copia.getEstadoCarta() &&
						original.getUsuarioPropietario().getId() == copia.getUsuarioPropietario().getId() &&
						original.isInExchange()== copia.isInExchange() &&
								original.getId() != copia.getId()
						)
				{
					original.setCantidad(original.getCantidad() + copia.getCantidad());
					entityManager.persist(original);
					entityManager.remove(copia);
					entityManager.flush();
					return true;
				}
			}
			return false;
		}
}
