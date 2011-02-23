%{def paster=_arg}%
{key:"${paster.skey}",
	title:"${paster.title?.escapeJavaScript()}",
	content:"${paster.content?.escapeJavaScript()}",
	rating:${paster.rating},
	useful:${paster.useful},
	usefulurl:'@{CloudPaster.useful(paster.skey)}',
	useless:${paster.useless},
	uselessurl:'@{CloudPaster.useless(paster.skey)}',
	createDate:'${paster.createDate.format('yy-MM-dd HH:mm')}',
	viewurl:'@{CloudPaster.view(paster.skey)}',
	ratingupurl:'@{CloudPaster.ratingup(paster.skey)}',
	ratingdownurl:'@{CloudPaster.ratingdown(paster.skey)}'
}