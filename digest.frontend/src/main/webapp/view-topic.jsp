<%@page import="javax.swing.text.StyledEditorKit.ForegroundAction"%>
<%@page import="java.util.Enumeration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="java.io.*"%>
<%@page import="java.net.*"%>
<%@page import="org.json.*"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>View Topic</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script
	src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.12.0/jquery.validate.min.js"
	type="text/javascript"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<style>
body {
	background-color: lightgrey;
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
	background: black;
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
	color: grey;
}

ul#horizontal-list a:hover {
	text-decoration: none;
	color: white;
}

.open-topic {
	width: 100%;
	heigth: 100%;
}

.open-topic button {
	float: right;
}

.topic-header {
	width: 100%;
}

.topic-body {
	with: 100%;
	margin: 2px 2px 2px 2px;
}
</style>
<script>
	$(document).ready(function() {

		$('#view_topic_form').validate({ 
			rules : {
				topic_id : {
					required : true
				},
			}
		});

	});
</script>
</head>
<body>
	<%
	if (session.getAttribute("session") == null) {
	%>

	<nav class="navbar navbar-inverse">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target="#myNavbar">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="index.jsp">DIGest <span><img
						alt="digest-icon" src="dig-icon.png"></span></a>
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

	<h1>Must be updated for unregistered users</h1>
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
				<a class="navbar-brand" href="index.jsp">DIGest <span><img
						alt="digest-icon" src="dig-icon.png"></span></a>
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
					<li><a href="LogoutServlet"><span class="glyphicon glyphicon-log-out"></span>
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
								<li class="active"><a href="#"><span
										class="glyphicon glyphicon-home"></span> Homepage</a></li>
								<li><a href="UserProfileServlet"><span
										class="glyphicon glyphicon-user"></span> Profile</a></li>
								<li><a href="followed-topics.jsp"><span
										class="glyphicon glyphicon-star-empty"></span> Followed Topics</a></li>
								<li><a href="user-topics.jsp"><span
										class="glyphicon glyphicon-upload"></span> My Topics</a></li>
							</ul>
							<div class="panel panel-default"
								style="height: 200px; overflow-y: auto;">
								<div class="panel-header">Your Notes</div>
								<div class="panel-body">Notes go here. adsd asda fc sd ds
									cds cds dsc sd \n sdf \n sdcds\n</div>

							</div>
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
				<h1>Topic Header</h1>
				<p>Topic Owner</p>
				<p>Topic Followers Number</p>
				<div class="view-topic col-sm-12">
					<!--<form class="form-horizontal" action="#">
						<div class="form-group">-->
					<div class="topic-header">
						<div class="row">
							<div class="container col-sm-2">
								<img id="topic-img"
									style="display: block; width: 120%;  margin: 9px 9px 9px 9px;"
									alt="topic image" src="topic.png" ></img>
							</div>
							<div class="container col-sm-10">
								<div class="panel panel-default"
									style="height: 140px; overflow-y: auto; margin: 9px 9px 9px 9px;"></div>
							</div>
						</div>
					</div>
					<div class="topic-tabs container col-sm-12" style="margin: 25px 0 0 0;">
						<ul class="nav nav-tabs">
							<li class="active"><a data-toggle="tab" href="#comments">Comments</a></li>
							<li><a data-toggle="tab" href="#materials">Materials</a></li>
							<li><a data-toggle="tab" href="#quiz">Quiz</a></li>
							<li><a data-toggle="tab" href="#related-topics">Related Topics</a></li>
						</ul>

						<div class="tab-content">
							<div id="comments" class="tab-pane fade in active">
								<div class="panel panel-default"
									style="height: 500px; overflow-y: auto;">
									<div class="row">
										<div class="col-sm-10 col-sm-offset-1" id="logout" style="margin: 25px 5px 25px 25px;">
											<ul class="media-list">
												<li class="media"><a class="pull-left" href="#"> 
												<p class="media-heading reviews">Account
																Name</p>
												<!--<img
														class="media-object img-circle" alt="profile">-->
												</a>
													<div class="media-body">
														<div class="well well-lg">
															<!--<p class="media-heading text-uppercase reviews">Account
																Name</p>-->
															<p class="media-comment">Text Example.</p>
															<a class="btn btn-info btn-circle"
																href="#" id="reply"><span
																class="glyphicon glyphicon-share-alt"></span> Reply</a> <a
																class="btn btn-warning btn-circle "
																data-toggle="collapse" href="#reply1"><span
																class="glyphicon glyphicon-comment"></span> 2 Comments</a>
														</div>
													</div>
													<div class="collapse col-sm-9 pull-right " id="reply1">
														<ul class="media-list">
															<li class="media media-replied"><a class="pull-left"
																href="#"> <img class="media-object img-circle"
																	alt="profile">
															</a>
																<div class="media-body">
																	<div class="well well-lg">
																		<h4 class="media-heading text-uppercase reviews">
																			<span class="glyphicon glyphicon-share-alt"></span>
																			Account Name
																		</h4>
																		<p class="media-comment">Text Example.</p>
																	</div>
																</div></li>
															<li class="media media-replied" id="replied"><a
																class="pull-left" href="#"> <img
																	class="media-object img-circle" alt="profile">
															</a>
																<div class="media-body">
																	<div class="well well-lg">
																		<h4 class="media-heading text-uppercase reviews">
																			<span class="glyphicon glyphicon-share-alt"></span>
																			Account Name
																		</h4>
																		</h4>
																		<p class="media-comment">Text Example</p>
																	</div>
																</div></li>
														</ul>
													</div></li>
											</ul>
										</div>
									</div>
								</div>
							</div>
							<div id="materials" class="tab-pane fade">
								<div class="panel panel-default"
									style="height: 500px; overflow-y: auto;"></div>
							</div>
							<div id="quiz" class="tab-pane fade">
								<div class="panel panel-default"
									style="height: 500px; overflow-y: auto;"></div>
							</div>
						</div>
					</div>
					<!--</div>
					</form>-->
				</div>
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