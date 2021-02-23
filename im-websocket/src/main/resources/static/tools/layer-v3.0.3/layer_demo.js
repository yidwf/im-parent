document.write("<script type='text/javascript' src='/tools/layer-v3.0.3/layer.js'></script>");
jQuery.prompt = {
	percent_h: 0.80,
	percent_w: 0.75,
	ok: "ok",
	msg: "msg",
	warn: "warn",
	error: "error",
	/**
	 * 提示窗口：含确定关闭按钮
	 * 使用方式：$.prompt.alert("标题", $.prompt.msg);
	 * @param title
	 * @param type 不传默认$.prompt.msg
	 */
	alert: function(title, type) {
		var icon = 6;
		if(type == jQuery.prompt.ok) {
			icon = 1;
		} else if(type == jQuery.prompt.msg) {
			icon = 6;
		} else if(type == jQuery.prompt.warn) {
			icon = 0;
		} else if(type == jQuery.prompt.error) {
			icon = 2;
		}
		layer.alert(title, {
			closeBtn: 0,
			icon: icon,
			move: false,
			time: 10000
		});
		$(".layui-layer-btn a").text("确定").css("background-color", "#5cb85c");
		$(".layui-layer-btn a").text("确定").css("border-color", "#5cb85c");
	},
	/**
	 * 提示窗口
	 * 使用方式：$.prompt.message("注册成功！", $.prompt.msg);
	 * @param title
	 * @param type 不传默认$.prompt.msg
	 * @param time 多少毫秒后关闭
	 */
	message: function(title, type, options) {
		if(options == undefined) {
			options = {};
		}
		if(options.time == undefined) {
			options.time = 3000;
		}
		if(type == jQuery.prompt.ok) {
			layer.msg(title, {
				time : options.time,
				icon : 1
			});
		} else if(type == jQuery.prompt.error) {
			layer.msg(title, {
				time : options.time,
				icon : 2
			});
		} else if(type == jQuery.prompt.warn) {
			layer.msg(title, {
				time : options.time,
				icon : 0
			});
		} else if(type == jQuery.prompt.msg) {
			layer.msg(title, {time : options.time});
		} else {
			layer.msg(title, {time : options.time});
		}
	}
}