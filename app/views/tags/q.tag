%{
    paster = _arg
}%
<div class="grid9 first question">
    <div>
		
        <div class="question-title">
        ${paster.answerCount} #{a @CloudPaster.view(paster.id)}${paster?.title?.raw()}#{/a}
        </div>
        <div class="tags">
        #{list items:paster?.tags,as:'tag'} #{a @CloudPaster.tag(tag.name)}${tag.name}#{/a}&nbsp;&nbsp;#{/list}
        </div>
        <div class="question-signature">
        #{a @UserCenter.index()}${paster?.creator.name}#{/a} 发表于 ${paster?.created?.format('yyyy-MM-dd hh:ss')}
        #{if paster?.updated} &nbsp; #{a @UserCenter.index()}${paster?.creator.name}#{/a} 修改于 ${paster?.updated.format('yyyy-MM-dd hh:ss')}#{/if}
        </div>
    </div>
</div>
