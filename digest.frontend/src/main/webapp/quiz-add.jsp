<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="org.json.*"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Add Quiz</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<style>
body {
	background-color: #f5f5f5;
}

@media ( min-width : 768px) {
	.sidebar-nav {
		padding: 12px;
	}
	.sidebar-nav .navbar .navbar-collapse {
		padding: 0;
		max-height: none;
	}
	.sidebar-nav .navbar ul {
		float: none;
		display: block;
	}
	.sidebar-nav .navbar li {
		float: none;
		display: block;
	}
	.sidebar-nav .navbar li a {
		font-family: Helvetica Neue;
		padding-top: 12px;
		padding-bottom: 12px;
	}
	.sidebar-nav .navbar .panel {
		padding-left: 12px;
		padding-write: 12px;
		height: 200px;
		overflow-y: auto;
	}
}

#menu-outer {
	height: 84px;
	width: 100%;
	background: white;
	position: fixed;
	bottom: 0;
}

#content {
	position: relative;
	width: 100%;
	overflow: auto;
	margin-bottom: 84px;
}

#form-aligned {
	border: 1px solid white;
	padding: 10px;
}

ul#horizontal-list {
	min-width: 696px;
	list-style: none;
	padding-top: 20px;
}

ul#horizontal-list li, ul#horizontal-list a {
	display: inline;
	float: left;
	color: white;
}

ul#horizontal-list a:hover {
	text-decoration: none;
	color: white;
}

.navbar-inverse {
    background-color: #377bb5;
    border-color: #377bb5;
}

.navbar-inverse .navbar-brand {
    color: white;
}

.navbar-inverse .navbar-nav > li > a {
    color: white;
}

.panel{

	background-color: #white
}
.panel-header {
	font-family: Helvetica Neue;
}
.list-group-item-heading{
	font-family: Helvetica Neue;
}


</style>
<script>
	$(document).ready(function() {

		$('#add-question').validate({ // initialize the plugin
			rules :{
				question : {
					required : true

				},
				option1 : {
					required : true

				},
				option2 : {
					required : true

				},
			}
		});
		
		
		$('#add-quiz').validate({ // initialize the plugin
			rules :{
				quiz-name : {
					required : true

				},
				
			}
		});

	});
</script>

</head>
<body>
	<%
		session = request.getSession();
		Object sessionID = session.getAttribute("session");

		if (sessionID == null) {
	%>

	<nav class="navbar navbar-inverse">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target="#myNavbar">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="MainServlet">DIGest <span><img 
						alt="digest-icon" src="img/logo.jpg" height=35 width=35 style="margin:0 0 0 10px "> </span></a>
			</div>
			<div class=" collapse navbar-collapse" id="myNavbar">
				<div class="col-sm-6 pull">
					<form action="_search" method="POST" class="navbar-form"
						role="search">
						<div class="input-group col-sm-12">
							<input type="text" class="form-control" placeholder="Search"
								name="searchterm" id="srch-term">
							<div class="input-group-btn">
								<button class="btn btn-default" type="submit">
									<i class="glyphicon glyphicon-search"></i>
								</button>
							</div>
						</div>
					</form>
				</div>
				<ul class="nav navbar-nav navbar-right">

					<li><a href="signup.jsp"><span
							class="glyphicon glyphicon-user"></span> Sign Up</a></li>

					<li><a href="login.jsp"><span
							class="glyphicon glyphicon-log-in"></span> Login</a></li>

				</ul>
			</div>
		</div>
	</nav>

	<h1>You have to be signed up and login to the system to create a
		topic!!!</h1>
	<%
		} else {
	%>
	<nav class="navbar navbar-inverse">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target="#myNavbar">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="MainServlet">DIGest <span><img 
						alt="digest-icon" src="img/logo.jpg" height=35 width=35 style="margin:0 0 0 10px "> </span></a>
			</div>
			<div class=" collapse navbar-collapse" id="myNavbar">
				<div class="col-sm-3 pull">
					<form action="_search" method="POST" class="navbar-form"
						role="search">
						<div class="input-group">
							<input type="text" class="form-control" placeholder="Search"
								name="searchterm" id="srch-term">
							<div class="input-group-btn">
								<button class="btn btn-default" type="submit">
									<i class="glyphicon glyphicon-search"></i>
								</button>
							</div>
						</div>
					</form>
				</div>
				<ul class="nav navbar-nav navbar-right">
					<li><a href="#"><span class="glyphicon glyphicon-envelope"></span>
							Messages</a></li>
					<li><a href="#"><span class="glyphicon glyphicon-cog"></span>
							Settings</a></li>
					<li><a href="#"><span class="glyphicon glyphicon-th-list"></span>
							Notifications</a></li>
					<li><a href="#"><span class="glyphicon glyphicon-log-out"></span>
							Logout</a></li>
				</ul>
			</div>
		</div>
	</nav>
	<div class="row col-sm-12" id="content">
		<div class="row">
			<div class="col-sm-3">
				<div class="sidebar-nav">
					<div class="navbar navbar-default" role="navigation">
						<div class="navbar-header">
							<button type="button" class="navbar-toggle"
								data-toggle="collapse" data-target=".sidebar-navbar-collapse">
								<span class="sr-only">Toggle navigation</span> <span
									class="icon-bar"></span> <span class="icon-bar"></span> <span
									class="icon-bar"></span>
							</button>
							<span class="visible-xs navbar-brand">Sidebar menu</span>
						</div>
						<div class="navbar-collapse collapse sidebar-navbar-collapse">
							<ul class="nav navbar-nav">
								<li><a href="MainServlet"><span
										class="glyphicon glyphicon-home"></span> Homepage</a></li>
								<li><a href="UserProfileServlet"><span
										class="glyphicon glyphicon-user"></span> Profile</a></li>
								<li><a href="FollowingTopicsServlet"><span
										class="glyphicon glyphicon-star-empty"></span> Following Topics</a></li>
								<li><a href="user-topics.jsp"><span
										class="glyphicon glyphicon-upload"></span> My Topics</a></li>
							</ul>
							<div class="panel panel-default"
								style="height: 200px; overflow-y: auto;">
								<div class="panel-header">Channels</div>
								<div class="panel-body">Channels and some links</div>

							</div>
							<div class="panel panel-default"
								style="height: 200px; overflow-y: auto;">
								<div class="panel-header">Recents</div>
								<div class="panel-body">Some recent topics</div>

							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="col-sm-9">

				<!-- Question Form Start -->
				<%
					if(request.getAttribute("tid")!=null){
						request.setAttribute("tid",request.getAttribute("tid"));
					}
				%>
				<div class="row">
				<div class="col-sm-6" style="border-right: 1px solid #cccccc ;">
				<form action="QuizServlet" method="POST" id="add-question">
					<div class="form-group">
						<label for="question">Question: </label>
						<textarea name="question" id="question" class="form-control" style="width:90%; margin:0 20px 20px 0"></textarea>
					</div>

					<div class="form-group">
						<label for="option1">Option1: </label>
						<textarea name="option1" id="option1" class="form-control" style="width:90%; margin:0 20px 20px 0"></textarea>
					</div>

					<div class="form-group">
						<label for="option2">Option2: </label>
						<textarea name="option2" id="option2" class="form-control" style="width:90%; margin:0 20px 20px 0"></textarea>
					</div>

					<div class="form-group">
						<label for="option3">Option3: </label>
						<textarea name="option3" id="option3" class="form-control" style="width:90%; margin:0 20px 20px 0"></textarea>
					</div>

					<div class="form-group">
						<button class="btn btn-default" name="f" value="add-question" style="margin:20px 20px 20px 0">Add
							Question</button>
					</div>
				</form>
				</div>
				

				<!-- Question Form Finish -->

				<!-- Show the Quiz Start -->
				<div class="col-sm-6">
				<form action="QuizServlet" method="POST" id="add-quiz">
					<!-- Quiz Name Start -->
					<div class="form-group">
						<label for="quiz-name" style="margin:0 0 5px 20px">Quiz Name:</label> <input
							class="form-control" name="quiz-name" id="quiz-name" type="text"
							<%if (request.getAttribute("quiz-name") != null) {%>
							value="<%=request.getAttribute("quiz-name")%>" <%}%>
							style="width:90%; margin:0 20px 20px 20px">
					</div>
					<!-- Quiz Name End -->
					<h4 style="margin:0 0 5px 20px">Please, select the correct answers for each question.</h4>
					<%
						if (request.getAttribute("questions") != null) {

								JSONArray questions = (JSONArray) request.getAttribute("questions");

								if (questions.length() > 0) {

									for (int j = 0; j < questions.length(); j++) {
										JSONObject quest = (JSONObject) questions.get(j);
										String question = quest.getString("text");
					%>
					<h4 style="color:#377bb5; margin:20px 0 20px 20px;">Question <%=j+1%>: <%=question%></h4>
					<!-- <p><%=question%></p>  -->
					<%
						JSONArray options = quest.getJSONArray("choices");

										if (options != null) {
											for (int i = 0; i < options.length(); i++) {
												String option = (String) options.get(i);
					%>
					
					<label for="q<%=j%>answer<%=i%>" style=" margin:0 0 0 20px;"><%=option%>
						<input type="checkbox" name="q<%=j%>answer<%=i%>" id="q<%=j%>answer<%=i%>" value="" class="badgebox">
						
					</label>
					<!--  <div class="checkbox">
						<label><input type="checkbox" name="q<%=j%>answer<%=i%>"
							value=""></label>
					</div>-->
					<%
						}
										}

									}

								}
							}
					%>
					<div class="form-group" style="margin:0 0 5px 20px">
						<button class="btn btn-default" name="f" value="add-quiz" style="margin:20px 20px 20px 0">Add
							Quiz</button>
					</div>
				</form>
				</div>
				
				
				</div>
				<!-- Show the Quiz Finish -->

				<!-- Quiz add result start-->
				<%
					if (request.getAttribute("success") != null) {
				%>
				<p><%=request.getAttribute("success")%></p>
				<%
					} else if (request.getAttribute("error") != null) {
				%>
				<p><%=request.getAttribute("error")%></p>
				<%
					}
				%>
				<!-- Quiz add result end -->
			</div>
		</div>
	</div>


	<%
		}
	%>


	<footer id="menu-outer">
		<div class="col-sm-offset-2 col-sm-10">
			<ul id="horizontal-list">
				<li class="col-sm-2"><a href="#">About</a></li>
				<li class="col-sm-2"><a href="#">Terms</a></li>
				<li class="col-sm-2"><a href="#">Developers</a></li>
				<li class="col-sm-2"><a href="#">Feedback</a></li>
				<li class="col-sm-2"><a href="#">Privacy</a></li>
			</ul>
		</div>
	</footer>
</body>
</html>