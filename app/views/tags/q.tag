%{
    paster = _arg
}%
<div class="grid9 first question">
<!--
    <div class="grid1 first voting-box ">
        <div class="vote-buttons">
            <table style="padding:0px;">
                <tr><td  style="padding:0px;vertical-align:bottom;"><input type="submit" value="" class="vote-up" onclick="javascript:vote('@{CloudPaster.voteup(paster.id)}')"></td></tr>
                <tr><td  style="padding:0px;vertical-align:top;"><input type="submit" value="" class="vote-down" onclick="javascript:vote('@{CloudPaster.votedown(paster.id)}')"></td></tr>
            </table>
        </div>
        <div class="vote-count">
            ${paster.voteup-paster.votedown}
        </div>
    </div>	
    <div class="grid1 reply-box ">
        <div class="reply-count">
            ${paster.answerCount}
        </div>
        回复
    </div>
-->	
    <div>
        <div class="question-title">
        #{a @CloudPaster.view(paster.id)}${paster?.title?.raw()}#{/a}
        </div>
        <div class="tags">
        #{list items:paster?.tags,as:'tag'}#{a @CloudPaster.tag(tag.name)}${tag.name}#{/a}&nbsp;&nbsp;#{/list}
        </div>
        <div class="question-signature">
        #{a @UserCenter.index()}${paster?.creator.name}#{/a} 发表于 ${paster?.created?.format('yyyy-MM-dd hh:ss')}
        #{if paster?.updated} <br/> #{a @UserCenter.index()}${paster?.creator.name}#{/a} 修改于 ${paster?.updated.format('yyyy-MM-dd hh:ss')}#{/if}
        </div>
    </div>
</div>
