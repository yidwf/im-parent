/**
 * 
 * @author yesido
 * @date 2018年9月2日
 */
var defaults = {
	user_icon_big: "../../images/chat/default_user_icon.png",
	user_icon: "/images/chat/user.png",
	admin_icon: "/images/chat/1.jpg",
	manager_icon: "/images/chat/2.png"
};
/**-------config-------**/
var config = {};
// 当前用户
config.user = {
	account: "",
	password: "",
	name: "",
	icon: defaults.admin_icon
};
// 接收用户
config.to = {
	account: "",
	password: "",
	name: "",
	icon: defaults.manager_icon
};
// 保存服务器地址
config.server = {
	host:"",
	port:"",
	websocket_port:""
}
// 初始化当前用户
config.initUser = function() {
	var url = "/current/user";
	ajaxConfig.syncPost(url, {}, function(data) {
		config.user.account = data.data.account;
		config.user.password = data.data.password;
		config.user.name = data.data.account;
		if(config.user.account == "admin") {
			config.user.icon = defaults.admin_icon;
			
			config.to.account = "manager";
			config.to.password = "123456";
			config.to.name = config.to.account;
			config.to.icon = defaults.manager_icon;
		} else {
			config.user.icon = defaults.manager_icon;
			
			config.to.account = "admin";
			config.to.password = "123456";
			config.to.name = config.to.account;
			config.to.icon = defaults.admin_icon;
		}
		$("#avatar").attr("src", config.user.icon);
		$("#name").html(config.user.name);
	});
}
config.nextServer = function() {
	ajaxConfig.syncPost("/im_restful/host", {}, function(data) {
		var host = data.data.im_restful_host;
		var url = "http://"+host+":8081/im_restful/im_server/conn_host/get";
		ajaxConfig.syncPost(url, {}, function(data) {
			config.server.host = data.data.host;
			config.server.port = data.data.port;
			config.server.websocket_port = data.data.websocket_port;
		});
	});
	
}
config.message_bak = {
	header: {},
	body:{},
	put: function(key, value) {
		this[key] = value;
	},
	putHeader: function(key, value) {
		this.header[key] = value;
		return this;
	},
	putBody: function(key, value) {
		this.body[key] = value;
		return this;
	},
	val: function() {
		return JSON.stringify(this);
	}
};
config.message = {
	put: function(key, value) {
		this[key] = value;
	},
	val: function() {
		return JSON.stringify(this);
	}
};
config.initMessge = function() {
	var newObject = $.extend(true, {}, config.message);
	return newObject;
};
config.code = {
	un_know: -1,
	login: 101,
	logout: 102,
	login_repeat: 103,
	heartbeat: 104,
	push_msg_answer: 105,
	push_msg: 106,
	chat_simple: 201
};
/**-------config-------**/

/**-------im-------**/
var im = {};
im.connect = function() {
	if (!window.WebSocket) {
        window.WebSocket = window.MozWebSocket;
    }
	var account = config.user.account;
	var password = config.user.password;
	var url = "ws://" + config.server.host + ":" + config.server.websocket_port + 
		"/websocket?account=" + account + "&password=" + password;
	if (window.WebSocket) {
		// isReconnect断线后是否重连，false=不重连
		im.socket = new ReconnectingWebSocket(url, null, {isReconnect: false});
	}
	im.socket.onopen = function(event) {
    	console.log("websocket连接成功！");
    	im.heartbeat();
    	im.start_heartbeat();
    };
    im.socket.onmessage = function(event) {
    	// 接收消息回调
    	var message = JSON.parse(event.data);
    	im.handlerMessage(message);
    }
    
    im.socket.onclose = function (event) {
    	console.log("websocket连接关闭！");
    };

    im.socket.onerror = function (event) {
        console.log("websocket连接异常！");
    };
};
im.sendMsg = function(message) {
	if (!window.WebSocket) {
		console.log("浏览器不支持WebSocket");
        return;
    }
	if (im.socket.readyState == WebSocket.OPEN) {
		im.socket.send(message);
	} else {
		console.log("WebSocket连接没有建立成功!");
	}
};

// 发送心跳
im.heartbeat = function() {
	var msg = netty.build.heartbeat();
	im.sendMsg(msg.val());
}
// 定时心跳
im.start_heartbeat = function() {
	setTimeout(function(){
		im.heartbeat();
		im.start_heartbeat();
	}, 30000);
}

// 发送文本消息
im.sendTextMsg = function(to, text) {
	var msg = netty.build.text(to, text);
	im.sendMsg(msg.val());
	msg_text.append_my_msg(msg);
}
// 发送消息响应
im.sendAnswerMsg = function(answerIds) {
	var msg = netty.build.answer(answerIds);
	im.sendMsg(msg.val());
}
// 处理回调消息
im.handlerMessage = function(message) {
	console.log(JSON.stringify(message));
	switch (message.code) {
	case config.code.login:
	case config.code.logout:
	case config.code.login_repeat:
		console.log(JSON.stringify(message));
		break;
	case config.code.heartbeat:
		break;
	case config.code.chat_simple:
		msg_text.append_friend_msg(message);
		break;
	case config.code.push_msg:
		var answerIds = [];
		for(var msg_id in message.attachment){
			msg_text.append_friend_msg(JSON.parse(message.attachment[msg_id]));
			answerIds.push(msg_id);
		}
		im.sendAnswerMsg(answerIds);
		break;
	default:
		console.log("无法解析消息" + JSON.stringify(message));
		break;
	}
}
/**-------im-------**/

/**-------netty message-------**/
var netty = {
	contentType: {
		text: 1,
		img: 2,
		video: 3,
		file: 4,
		link: 5
	},
	build: {}
};
// 构建心跳数据
netty.build.heartbeat = function() {
	var msg = config.initMessge();
	msg.put("from", config.user.account);
	msg.put("contentType", netty.contentType.text);
	msg.put("content", "心跳请求");
	msg.put("createtime", date_config.getDateLong());
	msg.put("code", config.code.heartbeat);
	return msg;
};
// 构建单聊数据
netty.build.text = function(to, text) {
	var msg = config.initMessge();
	msg.put("from", config.user.account);
	msg.put("to", to);
	msg.put("contentType", netty.contentType.text);
	msg.put("content", text);
	msg.put("msgType", 1); // 单聊
	msg.put("createtime", date_config.getDateLong());
	msg.put("code", config.code.chat_simple);
	return msg;
};
// 构建消息响应数据
netty.build.answer = function(ids) {
	var msg = config.initMessge();
	msg.put("from", config.user.account);
	msg.put("contentType", netty.contentType.text);
	msg.put("content", ids);
	msg.put("code", config.code.push_msg_answer);
	return msg;
};
/**-------netty message-------**/

$(function() {
	$(".nicescroll").niceScroll({
		autohidemode: "leave",
		cursorcolor: "#cccccc",
		cursorwidth: "8px"
	});
	config.initUser();
	config.nextServer();
	im.connect();
	$("#send_btn").click(function() {
		var text = $("#msg_text").val();
		if($.trim(text) == "") {
			$("#msg_text").val("");
			$.prompt.message("发送内容不能为空！", $.prompt.warn);
			return;
		}
		im.sendTextMsg(config.to.account, text);
		$("#msg_text").val("");
	});
});

/**-------msg_text-------**/
var msg_text = {
	append_my_msg: function(message) {
		msg_text.append_msg(message, true);
	},
	append_friend_msg: function(message) {
		msg_text.append_msg(message, false);
	},
	append_msg: function(message, self) {
		var self_class = "";
		var icon = config.to.icon;
		if(self) {
			self_class = " self";
			icon = config.user.icon;
		}
		time = date_config.toDate(message.createtime);
		time = date_config.format(time);
		var html = "";
		html += "<li>";
		html += "<p class='time'><span>"+time+"</span></p>";
		html += "<div class='main" + self_class + "'>";
		html += "<img class='avatar' width='30' height='30' src='" + icon + "'/>";
		html += "<div class='text'>";
		html += message.content;
		html += "</div>";
		html += "</div>";
		html += "</li>";
		$("#chat_content").append(html);
		setTimeout(function() {
			var div = document.getElementById("chat_content");
			div.scrollTop = div.scrollHeight;
		}, 10);
	}
	
};
/**-------msg_text-------**/

/**-------date_config-------**/
var date_config = {};
date_config.detaultDate = function() {
	return date_config.getDate();
}
date_config.getDate = function() {
	var date = new Date();
    return date_config.format_yyyy_MM_dd_HH_mm_ss(date);
};
date_config.format = function(date) {
    return date_config.format_yyyy_MM_dd_HH_mm_ss(date);
};
date_config.format_yyyy_MM_dd_HH_mm_ss = function(date) {
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
            + " " + date.getHours() + seperator2 + date.getMinutes()
            + seperator2 + date.getSeconds();
    return currentdate;
};
date_config.getDateLong = function() {
	var date = new Date();
    return date.getTime();
};
date_config.toDate = function(longtime) {
	var date = new Date();  
    date.setTime(longtime);
    return date;
};
/**-------date_config-------**/