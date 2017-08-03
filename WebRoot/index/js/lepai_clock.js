
function lepaiclockdone(){
	setTimeout("lepaiclockdone()", 1000);
	$(".leftTime").each(function(){
		var obj = $(this);
		var tms = obj.attr("count_down");
		var t = obj.attr("timestatus");
		if(t == 2){
			var html = '距 结 束 ';
		}else if(t == 1){
			var html = '距 开 始 ';
		}else{
			var html = '';
		}
		if (tms>0) {
			tms = parseInt(tms)-1;
			var days = Math.floor(tms / (1 * 60 * 60 * 24));
			var hours = Math.floor(tms / (1 * 60 * 60)) % 24;
			var minutes = Math.floor(tms / (1 * 60)) % 60;
			var seconds = Math.floor(tms / 1) % 60;

			if(days > 0){
				html += "<span>"+days+"</span>天";
			}
			// if(hours > 0){
				html += "<span>"+hours+"</span>时";
			// }
			// if(minutes > 0){
				html += "<span>"+minutes+"</span>分";
			// }
			html += "<span>"+parseInt(seconds)+"</span>秒";
			obj.html(html);
			obj.attr("count_down",tms);
		}else{
			location.href = location.href;
		}
	});
}
lepaiclockdone();//启动倒计时
