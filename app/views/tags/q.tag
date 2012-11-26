%{
paster = _arg
}%
<div class="row-fluid question">
    <div class="span2" style="vertical-align:middle;">
        <span class="vote-score" title="支持数量">${paster.voteup-paster.votedown}</span>
        <span class="vote-score" title="答案数量">${paster.answerCount}</span>
        <span class="vote-score" title="浏览数量">${paster.viewCount}</span>
    </div>
    <div class="span10">
        <h4>#{a @CloudPaster.view(paster.id), class:'question-list-title'}${paster?.title?.raw()}#{/a}</h4>
        #{tags paster.tags/}

        <span class="info">	
            #{ifnot paster?.lastAnswerUser} 
            #{ifnot} #{a @UserCenter.index(paster?.creator.id)}${paster?.creator.name}#{/a} ${paster?.created?.freindly()} 提问 #{/ifnot}
            #{if paster?.updated} #{a @UserCenter.index(paster?.creator.id)}${paster?.creator.name}#{/a} ${paster?.updated.freindly()} 修改#{/if}
            #{/ifnot}
            #{if paster?.lastAnswerUser} #{a @UserCenter.index(paster?.lastAnswerUser.id)}${paster?.lastAnswerUser.name}#{/a} ${paster?.lastAnswered?.freindly()} 回答#{/if}
        </span>
    </div>
</div>
