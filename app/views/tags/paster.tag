%{def paster=_arg}%
{key:"${paster.key}",
	content:"${paster.content.escapeJavaScript()}",
	contentAsHtml:"${paster.contentAsHtml.escapeJavaScript()}",
	rating:${paster.rating},
	useful:${paster.useful},
	usefulurl:'@{CloudPaster.useful(paster.key)}',
	useless:${paster.useless},
	uselessurl:'@{CloudPaster.useless(paster.key)}',
	createDate:'${paster.createDate.format('yy-MM-dd HH')}',
	viewurl:'@{CloudPaster.view(paster.key)}',
	ratingupurl:'@{CloudPaster.ratingup(paster.key)}',
	ratingdownurl:'@{CloudPaster.ratingdown(paster.key)}'
}