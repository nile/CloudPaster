%{
    paster = _arg
}%
<table style="width:100%">
<tr>
<td>${paster.answerCount} #{a @CloudPaster.view(paster.id)}${paster?.title?.raw()}#{/a}
#{list items:paster?.tags,as:'tag'} #{a @CloudPaster.tag(tag.name)}<span style="color:blue">${tag.name}</span>#{/a}&nbsp;#{/list}
</td>  
</tr>
<tr>
<td style="color:#828282;font-size:75%;padding-left:20px;">
#{a @UserCenter.index()}${paster?.creator.name}#{/a} 发表于 ${paster?.created?.format('yyyy-MM-dd hh:ss')}
#{if paster?.updated} &nbsp; #{a @UserCenter.index()}${paster?.creator.name}#{/a} 修改于 ${paster?.updated.format('yyyy-MM-dd hh:ss')}#{/if}
</td>
</tr>
</table>
        

        
