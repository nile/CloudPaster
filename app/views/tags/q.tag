%{
    paster = _arg
}%
<table>
<tr>
	<td style="width:16px;font-size:14px;font-weight:bold;">${paster.answerCount} </td>
	<td>#{a @CloudPaster.view(paster.id)}${paster?.title?.raw()}#{/a}
	#{list items:paster?.tags,as:'tag'} #{a @CloudPaster.tag(tag.name)}<span style="color:blue">${tag.name}</span>#{/a}&nbsp;#{/list}

	<span style="color:#828282;font-size:75%;padding-left:20px;">	
	#{ifnot paster?.lastAnswerUser} 
		#{ifnot} ${paster?.creator.name} ${paster?.created?.freindly()} 提问 #{/ifnot}
		#{if paster?.updated} ${paster?.creator.name} ${paster?.updated.freindly()} 修改#{/if}
	#{/ifnot}
	#{if paster?.lastAnswerUser} ${paster?.lastAnswerUser.name} ${paster?.lastAnswered?.freindly()} 回答#{/if}
	</span>
	</td>
</tr></table>