#{list items:_objects,as:'o'}
	#{if o}

<div class="grid8 first list-paster ${o_index%2==0?'odd-border':'even-border'}">
	<div class="paster-header ${o_index%2==0?'odd':'even'}">
		<div class="grid6 first">
			<div class="paster-icon"></div>
			<a href="@{CloudPaster.view(o.key)}">${o.key}</a>
			<span class="small">${o.createDate.format('yyyy-MM-dd HH:ss')}</span>
		</div>
		<div class="grid2 right">
		#{ifnot _private}
		#{/ifnot}
			<a href="javascript:void(0);" onclick="javascript:ratingup('${o.key}')">UP</a>
			<span id="${o.key}-rating">${o.rating}</span>
			<a href="javascript:void(0);" onclick="javascript:ratingdown('${o.key}')">DOWN</a>
		</div>

	</div>
	<div class="paster-content">
	#{if o.contentAsHtml}${o.contentAsHtml.raw()}#{/if}#{else}${o.content}#{/else}
	</div>
</div>

	#{/if}
#{/list}

<script type="text/javascript">
	function ratingup(key){
		rating('@{CloudPaster.ratingup()}',key);
	}
	function ratingdown(key){
		rating('@{CloudPaster.ratingdown()}',key);
	}
	function rating(url,key){
		var rating = new Request({method: 'post',
			url: url,
			onSuccess: function(ret){
				json=JSON.decode(ret);
				if(json.stat=='ok'){
					$(key+'-rating').set('html',json.paster.rating);
				};
			}
			});
		rating.send("key="+key);
	}
</script>