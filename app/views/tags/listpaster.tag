#{list items:_objects,as:'o'}
	#{if o}

<div class="grid8 first list-paster ${o_index%2==0?'odd-border':'even-border'}">
	<div class="paster-header ${o_index%2==0?'odd':'even'}">
		<div class="grid6 first">
			<div class="paster-icon"></div>
			<a href="@{CloudPaster.view(o.skey)}">${o.skey}</a>
			<span class="small">${o.createDate?.format('yy-MM-dd HH:ss')}</span>
		</div>
		<div class="grid2 right" style="display:none">
		#{ifnot _private}
		#{/ifnot}
			<a href="javascript:void(0);" onclick="javascript:ratingup('${o.skey}')">UP</a>
			<span id="${o.skey}-rating">${o.rating}</span>
			<a href="javascript:void(0);" onclick="javascript:ratingdown('${o.skey}')">DOWN</a>
		</div>

	</div>
	<div class="paster-content">
	#{if o.content}${o.content.raw()}#{/if}
	</div>
</div>

	#{/if}
#{/list}

<script type="text/javascript">

</script>