<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    
    <link rel="stylesheet" href="<c:url value="/resources/font-awesome-4.6.3/css/font-awesome.min.css" />" />
    <link href='https://fonts.googleapis.com/css?family=Raleway' rel='stylesheet' type='text/css'>
    
    <title>Giles</title>

    <!-- Bootstrap core CSS -->
    <link href="<c:url value="/resources/bootstrap/css/bootstrap.min.css" />" rel="stylesheet">

    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <link href="<c:url value="/resources/bootstrap/assets/css/ie10-viewport-bug-workaround.css" />" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="<c:url value="/resources/bootstrap/grid.css" />" rel="stylesheet">

    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]><script src="<c:url value="/resources/bootstrap/assets/js/ie8-responsive-file-warning.js" />"></script><![endif]-->
    <script src="<c:url value="/resources/bootstrap/assets/js/ie-emulation-modes-warning.js" />"></script>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
 	<script src="<c:url value="/resources/bootstrap/js/bootstrap.min.js" />"></script>
	
  </head>

  <body>
    <div class="container">

      <div class="page-header">
      <nav>
          <ul class="nav nav-pills pull-right">
          <sec:authorize access="not isAuthenticated()">
            <li role="presentation">
            	<form action="<c:url value="/signin/github" />" method="POST">
            		<button class="btn btn-link" type="submit">
            			<i class="fa fa-github" aria-hidden="true"></i> Login
            		</button>
            	</form>
            </li>
          </sec:authorize>
          <sec:authorize access="isAuthenticated()">
          	<li role="presentation">
          		<a href="<c:url value="/" />" >Dashboard</a>
          	</li>
          	<li role="presentation">
          		<a href="<c:url value="/files/upload" />">Upload Files</a>
          	</li>
         	 <li role="presentation">
				<a href="<c:url value="/logout" />"><i class="fa fa-sign-out" aria-hidden="true"></i>
				Logout</a>
         	 </li>
          </sec:authorize>
          </ul>
        </nav>
        
        <h1>Giles</h1>
        <p class="lead">Guarding Digilib</p>   
      </div>
      
      <sec:authorize access="isAuthenticated()">
      <p class="text-right">
      Logged in as <i class="fa fa-github" aria-hidden="true"></i> 
         			<sec:authentication
							property="principal.username" />
	  </p>
	  </sec:authorize>

	  <c:if test="${show_alert}" >
	  <div class="alert alert-${alert_type}" role="alert">${alert_msg}</div>
	  </c:if>
      <tiles:insertAttribute name="content" />

    </div> <!-- /container -->

     </body>
</html>
