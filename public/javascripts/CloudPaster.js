function showmsg(data){
	//alert(data)
	if(data.stat=='ok'){
		Boxy.alert("ͶƱ�ɹ�", null, {title: 'ͶƱ�ɹ�'})
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