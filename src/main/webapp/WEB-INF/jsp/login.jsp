<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" >
<head>
  <title>Login</title>
  <link rel="stylesheet" type="text/css"
        href="../main.css"/>
        
   <!-- <link rel="stylesheet" type="text/css"
        href="webjars/bootstrap/4.3.1/css/bootstrap.min.css"/>
  <script type="text/javascript"
          src="webjars/bootstrap/4.3.1/js/bootstrap.min.js"></script> -->
</head>
<body>
<div class="container-fluid">
  <div class="row">
    <div class="col-sm-12 col-md-6 vertical-center">
      <div class="message-error" th:if="<%=request.getParameter("error") %>.equals('')">  
      	<%String error = request.getParameter("error");
      	if ( error != null) {%>
			<h3 class="pos-message-error">El usuario o la contraseña no son correctos.</h3> 
			<%}%>
      </div>
      <form method="post" th:action="@{/login}">
        <h2>Login</h2>
        <div class="form-group">
          <label for="username">Username</label>
          <input th:autofocus type="text"  class="form-control underline" id="username" name="username" />
        </div>
        <div class="form-group">
          <label for="password">Password</label>
          <input type="password" class="form-control underline" id="password" name="password" />
        </div>
        <div class="form-group">
          <button type="submit" class="btn btn-primary btn-lg pull-right btn-log">Log In</button>
        </div>
      
      </form>
    </div>
  </div>
</div>
</body>
</html>