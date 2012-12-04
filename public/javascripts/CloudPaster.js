function showmsg(res){
	ret = $.parseJSON(res.responseText)
	if(ret.stat=='ok'){
		var dialog = new Boxy("<div><p>"+"投票成功"+"</p></div>");
        dialog.hidedelay(2000,function(){});
	}else{
		//Boxy.alert(ret.err.msg, null, {title: 'Message'})
        var dialog = new Boxy("<div><p>"+ret.err.msg+"</p></div>");
        dialog.hidedelay(2000,function(){});
	}
}
function vote(url){
	$.ajax({
		type:'POST',
		url:url,
		dataType:'json'
	}).complete(showmsg);
}