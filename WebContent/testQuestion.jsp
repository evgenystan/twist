<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.twist.problemPool.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<style>
body
{
	background-color:#eeeeff;
}
#content
{
	width:50em;
	min-height:500px;
	border-radius:1em;
	padding:1em;
	margin:0px auto 0px;
	background-color:white;
	box-shadow:0px 0.3em .7em #aaa;
}
.inputbox {vertical-align:top;}
.hidden {visibility:hidden}
.inputfield
{
	display: inline-block;
	padding:2px !important;
	margin-top:2px !important;
	border: 1px solid #eeeeee !important;
	min-width: 1ex !important;
	min-height: 1.5em !important;
}
.inputfield:hover
{
	box-shadow: 0px 0px 3px 0px #aaaaaa inset !important;
	border: 1px solid #aaaaaa !important;
}
.inputfield:focus
{
	box-shadow: 0px 0px 3px 0px #aaaaaa inset !important;
	border: 1px solid #aaaaaa !important;
}
.addField
{
	max-height:1.5em;
	height:1.5em;
	display:inline-block;
	font-size:1.5em;
	margin:3px;
	text-shadow: 0px 0px .3em #777777,0px 0px .2em #ffffff;
}
.removeField
{
	display:inline-block;
	font-size:1.5em;
	margin:3px;
	text-shadow: 0px 0px .3em #777777,0px 0px .2em #ffffff;
}
.addField:hover
{
	position:relative;
	margin:2px 4px 4px 2px;
	left:-1px;
	top:-1px;
	font-size:1.5em;
	text-shadow: 1px 1px .4em #000000,0px 0px .4em #ffffff;
}
.removeField:hover
{
	position:relative;
	margin:2px 4px 4px 2px;
	left:-1px;
	top:-1px;
	font-size:1.5em;
	text-shadow: 1px 1px .4em #000000,0px 0px .4em #ffffff;
}
</style>

<script type="text/x-mathjax-config">
  MathJax.Hub.Config({
    extensions: ["tex2jax.js"],
    jax: ["input/TeX","output/HTML-CSS"],
    tex2jax: {inlineMath: [["$","$"],["\\(","\\)"]]}
  });
</script>
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="./unpacked/MathJax.js"></script>
<script type="text/javascript" src="js/utils.js"></script>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>testQuestion</title>
<script></script>
</head>
<body>
<noscript>
<div style="color:#CC0000; text-align:center">
<b>Warning: <a href="http://www.mathjax.org/">MathJax</a> and many components of the testing system
require JavaScript to process the mathematics on this page.<br />
If your browser supports JavaScript, be sure it is enabled.</b>
</div>
</noscript>
<div id='content'>
	<%
		ProblemPuller pPuller = new ProblemPuller();
		String id=request.getParameter("id");
		String course=request.getParameter("course");
		
		if (id == null)
		{
		}
		else if (course == null)
		{
			out.print(pPuller.getProblemStatementAsString(Integer.parseInt(id)));
		}
		else
		{
			out.print(pPuller.getProblemStatementAsStringV2(course,id));
		}
	%>
</div>
</body>
</html>