#{if _mode=='simple'}
    <textarea style="width:60%;height:80px;" id="${_id}" name="${_name}"  rows="10">${_value}</textarea>
#{/if}
#{else}
#{set 'moreScripts'}
  #{get 'moreScripts'/}
  #{script "markitup/jquery.markitup.js"/}
  #{stylesheet "../javascripts/markitup/skins/markitup/style.css"/}
#{/set}	  

%{if ( _type=='markdown' ){ }%
	  #{get 'moreScripts'/}
	  #{set 'moreScripts'}
	  #{script "markitup/sets/markdown/set.js"/}
	  #{stylesheet "../javascripts/markitup/sets/markdown/style.css"/}
	#{/set}
%{ } else if ( _type=='wiki' ){ }%
	  #{get 'moreScripts'/}
	  #{set 'moreScripts'}
	  #{script "markitup/sets/wiki/set.js"/}
	  #{stylesheet "../javascripts/markitup/sets/wiki/style.css"/}
	#{/set}
%{ } else { }%	
	#{set 'moreScripts'}
	#{get 'moreScripts'/}
	  #{script "markitup/sets/default/set.js"/}
	  #{stylesheet "../javascripts/markitup/sets/default/style.css"/}
	#{/set}
%{ } }%

<script type="text/javascript">
<!--
$(document).ready(function()	{
	$('#${_id}').markItUp(mySettings,{previewParserPath:'@{Editor.preview()}'});
});
-->
</script>
<textarea style="height:140px" id="${_id}" name="${_name}" cols="80" rows="15">${_value}</textarea>
#{/else}