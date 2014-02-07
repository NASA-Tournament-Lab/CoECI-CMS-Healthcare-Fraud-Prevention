<%-- 
 Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 
 @author TCSASSEMBLER
 @version 1.0 
 
 Renders the index test page. 
--%>
<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>Healthcare Fraud Prevention - Data Exchange Network Node Module Assembly Demo</title>
  </head>
  
  <body>
    <b>Healthcare Fraud Prevention - Data Exchange Network Node Module Assembly Demo</b>
  </body>
</html>
