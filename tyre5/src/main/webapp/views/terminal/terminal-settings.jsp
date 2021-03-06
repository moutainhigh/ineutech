<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<base href="<%=basePath%>">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>终端参数设置</title>

<link href="css/bootstrap.min.css" rel="stylesheet">

<!--[if lt IE 9]>
      <script src="js/html5shiv.min.js"></script>
      <script src="js/respond.min.js"></script>
    <![endif]-->
</head>
<body class="container">
	<div style="height: 50px;"></div>
	<div class="left col-sm-4">
		<div class="panel panel-default">
			<div class="panel-heading">
				终端列表 <a id="refreshTerminalsBtn" style="cursor: pointer;" class="pull-right"> <i class="glyphicon glyphicon-refresh"></i>刷新
				</a>
			</div>
			<div class="panel-body" style="padding-left: 0; padding-right: 0;">
				<div class="list-group" id="sessionItems">
					<c:forEach items="${sessions}" var="s">
						<c:if test="${s.authenticated}">
							<a href="#" data-phone="${s.terminalPhone}" class="list-group-item sessionItem">${s.terminalPhone}</a>
						</c:if>
						<c:if test="${not s.authenticated}">
							<a class="list-group-item sessionItem disabled">${s.remoteAddr}&nbsp;<span class="label label-danger">未鉴权</span></a>
						</c:if>
					</c:forEach>
					<c:if test="${empty sessions}">
						<p>&nbsp;&nbsp;没有终端连接</p>
					</c:if>
				</div>
			</div>
		</div>
	</div>
	<div class="right col-sm-8">
		<div class="panel panel-default">
			<div class="panel-heading">设置面板</div>
			<div class="panel-body">
				<div class="radio col-md-3 param-item" style="margin-top: -5px;">
					<label> <input checked data-id="0x0013" data-name="服务器IP" data-demo="120.132.13.158" data-type="String" type="radio" name="paramType">服务器IP
					</label>
				</div>
				<div class="radio col-md-3">
					<label> <input data-id="0x0018" data-name="服务器TCP端口" data-demo="20048" data-type="DWord" type="radio" name="paramType">服务器TCP端口(10)
					</label>
				</div>
				<div class="radio col-md-3">
					<label> <input data-id="0x0601" data-name="高压域" data-demo="50" data-type="DWord" type="radio" name="paramType">高压域(16)
					</label>
				</div>
				<div class="radio col-md-3">
					<label> <input data-id="0x0602" data-name="低压域" data-demo="50" data-type="DWord" type="radio" name="paramType">低压域(16)
					</label>
				</div>
				<div class="radio col-md-3">
					<label> <input data-id="0x0603" data-name="高温域 " data-demo="50" data-type="DWord" type="radio" name="paramType">高温域(16)
					</label>
				</div>
				<div class="radio col-md-3">
					<label> <input data-id="0x0610" data-name="传感器个数 " data-demo="8" data-type="DWord" type="radio" name="paramType">传感器个数(16)
					</label>
				</div>
				<div class="radio col-md-3">
					<label> <input data-id="0x0611" data-name="胎位与胎ID " data-demo="8" data-type="Byte[8]" type="radio" name="paramType">胎位与胎ID(16)
					</label>
				</div>
				<div class="radio col-md-3">
					<label> <input data-id="0x0612" data-name="胎号 " data-demo="abc123456" data-type="String" type="radio" name="paramType">胎号
					</label>
				</div>
				<div class="radio col-md-3">
					<label> <input data-id="0x0701" data-name="车牌号 " data-demo="沪A12345" data-type="String" type="radio" name="paramType">车牌号
					</label>
				</div>

				<div class="clearfix"></div>
				<hr>
				<div role="form" class="well">
					<div class="form-group">
						<div style="margin: 10px 0;" id="settings-body">
							<span class="label label-primary tips-name">服务器IP</span>&nbsp; <span class="label label-success tips-id">0x0013</span>&nbsp; <span class="label label-info tips-type">String</span>
						</div>
						<label for="paramValueInput">参数值(如果有多个值,请用英文逗号分隔)</label> <input type="text" class="form-control" id="paramValueInput" placeholder="输入参数值" value="120.132.13.158">
					</div>

					<div class="form-group">
						<label for="paramValueInput">终端手机号(点击左侧终端列表以快速填充终端手机号)</label> <input type="text" class="form-control" id="terminalPhone" placeholder="终端手机号">
					</div>
					<button class="btn btn-default" id="submitBtn">设置终端参数</button>
					<button id="queryTerminalParamsBtn" class="btn btn-success">查询终端参数</button>
				</div>
				<hr>
				<div id="tipsDiv" class="sr-only">
					<p id="reslutText" class="text text-danger">xx</p>
					<table id="resultTable" class="table table-bordered table-hover table-striped">
						<thead>
							<tr>
								<th>参数ID</th>
								<th>参数值</th>
								<th>十六进制值</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>参数ID</td>
								<td>参数值</td>
								<td>十六进制值</td>
							</tr>
							<tr>
								<td>参数ID</td>
								<td>参数值</td>
								<td>十六进制值</td>
							</tr>
							<tr>
								<td>参数ID</td>
								<td>参数值</td>
								<td>十六进制值</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<script src="js/jquery.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/kkbc/terminal/param-settings.js"></script>
</body>
</html>
