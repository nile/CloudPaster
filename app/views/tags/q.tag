%{
    paster = _arg
}%
<table>
<tr>
<td style="width:16px;font-size:16px;font-weight:bold;">${paster.answerCount} </td>
<td>
	<table style="width:100%">
	<tr>
	<td>#{a @CloudPaster.view(paster.id)}${paster?.title?.raw()}#{/a}
	#{list items:paster?.tags,as:'tag'} #{a @CloudPaster.tag(tag.name)}<span style="color:blue">${tag.name}</span>#{/a}&nbsp;#{/list}
	</td>  
	</tr>
	<tr>
	<td style="color:#828282;font-size:75%;padding-left:20px;">
	<b>${paster?.creator.name}</b> ${paster?.created?.freindly()} 提问
	#{if paster?.updated} &nbsp; <b>${paster?.creator.name}</b> ${paster?.updated.freindly()} 修改#{/if}
	#{if paster?.lastAnswerUser} &nbsp; <b>${paster?.lastAnswerUser.name}</b> ${paster?.lastAnswered?.freindly()} 回答#{/if}
	</td>
	</tr>
	</table>
</td></tr></table>