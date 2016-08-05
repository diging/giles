<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<h2>Users</h2>

<table class="table table-striped">
  <tr>
  	<th width="25%">Username</th>
  	<th>Full Name</th>
  	<th width="30%">Email</th>
  	<th></th>
  	<th width="10%">Status</th>
  	<th width="5%"></th>
  </tr>
  
  <c:forEach items="${users}" var="user">
  <tr <c:if test="${user.accountStatus == 'ADDED' }">class="warning"</c:if>>
  	<td>${user.username}</td>
  	<td>${user.fullname}</td>
  	<td>${user.email}</td>
  	<td>
  		<c:if test="${user.roles.contains('ROLE_ADMIN')}"><span class="label label-danger">Admin</span></c:if>
  		<c:if test="${user.roles.contains('ROLE_USER')}"><span class="label label-info">User</span></c:if>	
  	</td>
  	<td>${user.accountStatus}</td>
  	<td>
  		<c:if test="${user.accountStatus == 'ADDED'}">
  		<form action="<c:url value="/users/user/${user.username}/approve" />" method="POST">
  		<button style="padding: 0px;" class="btn-link" type="submit" title="Approve user account"><i class="fa fa-thumbs-o-up" aria-hidden="true"></i></button>
  		</form>
  		
  		</c:if>
  	</td>
  </tr>
  </c:forEach>
  	
</table>