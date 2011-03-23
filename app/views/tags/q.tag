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
#{a @UserCenter.index()}${paster?.creator.name}#{/a} ${paster?.created?.freindly()} 提问
#{if paster?.updated} &nbsp; #{a @UserCenter.index()}${paster?.creator.name}#{/a} ${paster?.updated.freindly()} 修改#{/if}
</td>
</tr>
</table>
        

        
