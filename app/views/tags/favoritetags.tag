#{list items:_tags,as:'cloud'}
	<span style="white-space: nowrap">#{a @CloudPaster.tag(cloud.name)}<span class="tag">${cloud.name}</span>#{/a}<span style="line-height:22px;">${cloud.count}</span>&nbsp;
	</span>
#{/list}
