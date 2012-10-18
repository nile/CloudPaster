%{
    paster = _arg
}%
<table>
<tr>
	<td style="width:116px;">
	  <span class="vote-score" title="支持数量">${paster.voteup-paster.votedown}</span>
	  <span class="vote-score" title="答案数量">${paster.answerCount}</span>
	  <span class="vote-score" title="浏览数量">${paster.viewCount}</span></td>
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
