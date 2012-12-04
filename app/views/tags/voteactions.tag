<div class="vote-actions">
  <a class="vote-up ${_paster.hadVoteup(_user)?'vote-up-on':''}" href="@{CloudPaster.voteup(_paster.id)}">&nbsp;</a>
  <span class="vote-score ">${_paster.voteup-_paster.votedown}</span>
  <a class="vote-down ${_paster.hadVotedown(_user)?'vote-down-on':''}" href="@{CloudPaster.votedown(_paster.id)}">&nbsp;</a>
</div>
