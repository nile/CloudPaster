#{if _mode=='simple'}
	<textarea id="${_id}" name="${_name}">${_value}</textarea>
#{/if}
#{else}
#{set "addtional"}
	#{get "addtional"/}
	<link rel="stylesheet" type="text/css" href="@{'public/javascripts/markitup/style.css'}">
	<link rel="stylesheet" type="text/css" href="@{'public/javascripts/markitup/markitup/skins/simple/style.css'}">
	<script type="text/javascript" src="@{'public/javascripts/markitup/markitup/jquery.markitup.js'}"></script>
	<script type="text/javascript" src="@{'public/javascripts/markitup/markitup/sets/default/set.js'}"></script>

#{/set}
	<textarea id="${_id}" name="${_name}">${_value}</textarea>
	<script>
	$(document).ready(function()	{
$('#${_id}').markItUp(mySettings, 
								{ 	root:'markupsets/html/', 
									previewTemplatePath:'/downloads/preview.php',
									previewAutoRefresh:false 
								}
							);
});							
	
	</script>
#{/else}