<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:choose>
	<c:when test="${userLogin}">
		<%@ include file="../jspf/header.jspf" %>
	</c:when>
	<c:otherwise>
		<%@ include file="../jspf/header2.jspf" %>
	</c:otherwise>
</c:choose>



<div class="container">
	<div id="sobrePagina">
		<h3>T�rminos y condiciones de uso del servicio</h3>
		<br>
		<p>
			<b>CardEx.com</b> es una plataforma web registrada como tal bajo �copyrigth en Facultad de Inform�tica UCM. Calle del Prof. Jos� Garc�a Santesmases, 9, 28040, Madrid.
		</p>
		<p>Todo usuario debe incluir de manera veraz las cartas que tiene en su poder, en caso contrario puede denegarse dicha cuenta de usuario.</p>
		<p>En cuanto a los intercambios, los usuarios una vez acepten el intercambio, deber�n ponerse de acuerdo de manera externa a la empresa para hacer el intercambio. En caso de fraude en la transacci�n, el usuario disgustado podr� realizar una valoraci�n negativa hacia la otra persona repercutiendo negativamente en la valoraci�n de dicha persona.</p>
		<hr></hr>
		<h3>Obligaciones del Usuario</h3>
		<p>El usuario registrado debe garantizar que sus datos son correctos en todo momento para que las transacciones entre usuarios est� regida bajo los t�rminos del servicio</p>
		<p>En caso contrario la empresa puede denegar e invalidar toda cuenta de usuario que no cumpla con estos requisitos.</p>
	</div>
</div>

<%@ include file="../jspf/footer.jspf" %>
