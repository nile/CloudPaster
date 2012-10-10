%{
    paster = _arg
}%
<table>
<tr>
	<td style="width:26px;font-size:26px;text-align:center;font-weight:bold;">${paster.answerCount} </td>
	<td>#{a @CloudPaster.view(paster.id), class:'question-list-title'}${paster?.title?.raw()}#{/a}
	<br>#{list items:paster?.tags,as:'tag'} #{a @CloudPaster.tag(tag.name)}<span class="tag">${tag.name}</span>#{/a}#{/list}
	
	<span class="info">	
	#{ifnot paster?.lastAnswerUser} 
		#{ifnot} #{a @UserCenter.index(paster?.creator.id)}${paster?.creator.name}#{/a} ${paster?.created?.freindly()} 提问 #{/ifnot}
		#{if paster?.updated} #{a @UserCenter.index(paster?.creator.id)}${paster?.creator.name}#{/a} ${paster?.updated.freindly()} 修改#{/if}
	#{/ifnot}
	#{if paster?.lastAnswerUser} #{a @UserCenter.index(paster?.lastAnswerUser.id)}${paster?.lastAnswerUser.name}#{/a} ${paster?.lastAnswered?.freindly()} 回答#{/if}
	</span>
	</td>
</tr></table>