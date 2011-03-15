function showmsg(data){
	//alert(data)
	if(data.stat=='ok'){
		Boxy.alert("投票成功", null, {title: '投票成功'})
	}else{
		Boxy.alert(data.err.msg, null, {title: 'Message'})
	}
}
function vote(url){
	$.ajax({
		type:'POST',
		url:url,
		dataType:json
	}).complete(showmsg);
}