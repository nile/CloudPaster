#{if _mode=='simple'}
	<textarea id="${_id}" name="${_name}">${_value}</textarea>
#{/if}
#{else}
	<textarea id="${_id}" name="${_name}">${_value}</textarea>
	<script>
	var myMooEditable = new MooEditable('${_id}', {
		handleSubmit: false,
        dimensions:{x:550},
		cleanup: false,
		onRender: function(){
		}
	});
	</script>
#{/else}