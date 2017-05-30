<%@ include file="../jspf/header.jspf"%>

<div class="container">
	<div class="card hovercard">
		<div class="card-info">
			<span class="card-title">${usuario.usuario}</span>
		</div>
	</div>
	<div class="btn-pref btn-group btn-group-justified btn-group-lg"
		role="group" aria-label="...">
		<div class="btn-group" role="group">
			<button type="button" id="favorites" class="btn btn-primery"
				href="#tab1" data-toggle="tab">
				<span class="glyphicon glyphicon-user" aria-hidden="true"></span>
				<div class="hidden-xs">Informaci�n Personal</div>
			</button>
		</div>
		<div class="btn-group" role="group">
			<button type="button" id="stars" class="btn btn-default" href="#tab2"
				data-toggle="tab">
				<span class="glyphicon glyphicon-star" aria-hidden="true"></span>
				<div class="hidden-xs">Valoraci�n</div>
			</button>
		</div>
		<c:if test="${empty visitante}">
			<div class="btn-group" role="group">
				<button type="button" id="following" class="btn btn-default"
					href="#tab3" data-toggle="tab">
					<span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
					<div class="hidden-xs">Ajustes</div>
				</button>
			</div>
		</c:if>
	</div>

	<div class="well margin-top30 fg-form margin">
		<div class="tab-content">
			<div class="tab-pane fade in active" id="tab1">
				<table class="table table-sm">
					<tr>
						<th scope="row">Nombre</th>
						<td>${usuario.nombre}</td>
					</tr>
					<tr>
						<th scope="row">Apellidos</th>
						<td>${usuario.apellidos}</td>
					</tr>
					<tr>
						<th scope="row">E-Mail</th>
						<td>${usuario.email}</td>
					</tr>
					<tr>
						<th scope="row">Provincia</th>
						<td>${usuario.provincia}</td>
					</tr>
				</table>
			</div>
			<div class="tab-pane fade in" id="tab2">
				Valoraci�n media del usuario <input id="input-21e"
					value="<c:out value="${usuario.valoracionMedia}"/>" type="text"
					class="rating" readonly data-min=0 data-max=5 data-step=0.5
					data-size="sm" title="">
				<table class="table table-sm">
					<tbody>
						<c:if test="${visitante ne null}">
							<td>
								<form action="${prefijo}perfil/valorarUsuario"
									id="valorarUsuario" method="POST">
									Valora al usuario (este apartado no aparece a veces)
									<textarea id="opinion" rows="4" cols="50" class="center"
										form="valorarUsuario" name="descripcion"></textarea>
									<input id="input-21e" value="3" type="text" class="rating"
										data-min=0 data-max=5 data-step=1 data-size="xs" name="valor">
									<button type="submit" class="btn btn-sm btn-success right">Valorar</button>
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
									<input type="hidden" name="usuarioValorado" value="${usuario.usuario}" />
								</form>
							</td>
						</c:if>
						<c:forEach items="${usuario.valoracionesRecibidas}"
							var="valoracion">
							<tr>
								<th scope="row">${valoracion.usuarioQueValora.usuario}</th>
								<td>${valoracion.comentario}<br> <input id="input-21e"
									value="<c:out value="${valoracion.valor}"/>" type="text"
									class="rating" readonly data-min=0 data-max=5 data-step=0.5
									data-size="xs" title="valoracion">
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div class="tab-pane fade in" id="tab3">
				<div class="form-group margin-top30 fg-form">
					<form action="${prefijo}perfil/cambiarAjustes" method="post">
						<label for="usernameform">Cambiar nombre</label> 
						<input id="usernameform" class="form-control" name="nombre" value="${usuario.nombre}" /> 
						<label for="surnameform">Cambiar apellidos</label> 
						<input id="surnameform" class="form-control" name="apellidos" value="${usuario.apellidos}" />
					 	<label for="passform">Cambiar contrase�a</label> 
					 	<input id="passform" class="form-control" name="password" /> 
					 	<label for="repassform">Confirmar contrase�a</label> 
					 	<input id="repassform" class="form-control" name="passwordConfirm" /> 
					 	<label for="mailform">E-Mail</label> 
					 	<input id="mailform" class="form-control" name="email" value="${usuario.email}" />
						<label for="provform">Provincia</label> 
						<select id="provform" class="form-control" name="provincia">
							<option selected>${usuario.provincia}</option>
							<option>A Coru�a</option>
							<option>�lava</option>
							<option>Albacete</option>
							<option>Alicante</option>
							<option>Almer�a</option>
							<option>Asturias</option>
							<option>�vila</option>
							<option>Badajoz</option>
							<option>Baleares</option>
							<option>Barcelona</option>
							<option>Burgos</option>
							<option>C�ceres</option>
							<option>C�diz</option>
							<option>Cantabria</option>
							<option>Castell�n</option>
							<option>Ciudad Real</option>
							<option>C�rdoba</option>
							<option>Cuenca</option>
							<option>Girona</option>
							<option>Granada</option>
							<option>Guadalajara</option>
							<option>Gipuzkoa</option>
							<option>Huelva</option>
							<option>Huesca</option>
							<option>Ja�n</option>
							<option>La Rioja</option>
							<option>Las Palmas</option>
							<option>Le�n</option>
							<option>L�rida</option>
							<option>Lugo</option>
							<option>Madrid</option>
							<option>M�laga</option>
							<option>Murcia</option>
							<option>Navarra</option>
							<option>Orense</option>
							<option>Palencia</option>
							<option>Pontevedra</option>
							<option>Salamanca</option>
							<option>Segovia</option>
							<option>Sevilla</option>
							<option>Soria</option>
							<option>Tarragona</option>
							<option>Santa Cruz de Tenerife</option>
							<option>Teruel</option>
							<option>Toledo</option>
							<option>Valencia</option>
							<option>Valladolid</option>
							<option>Vizcaya</option>
							<option>Zamora</option>
							<option>Zaragoza</option>
						</select>
						<button id="settingsButton" type="submit" class="btn btn-default">Guardar cambios</button>
						<input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}" />
					</form>
				</div>

			</div>
		</div>
	</div>
</div>
<%@ include file="../jspf/footer.jspf"%>
