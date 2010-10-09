	#{list items:_objects,as:'o'}
		#{if o}
	<div class="grid8 first list-paster ${o_index%2==0?'odd-border':'even-border'}">
		<div class="paster-header ${o_index%2==0?'odd':'even'}"><div class="paster-icon"></div><a href="@{CloudPaster.view(o.key)}">${o.key}</a>
			<span class="small">${o.createDate.format('yyyy-MM-dd HH:ss')}</span>		
#{if _private}

#{/if}</div>
	  <div class="paster-content">
		#{if o.contentAsHtml}${o.contentAsHtml.raw()}#{/if}#{else}${o.content}#{/else}
	  </div>
	</div>
	
		#{/if}
	#{/list}
