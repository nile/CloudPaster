<h3>我关注的分类</h3>
<div class="favorite-tags">
  #{list items:_tags,as:'cloud'}
  <span style="white-space: nowrap">
      #{a @CloudPaster.tag(cloud.name), rel:"ajax6.html"}<span class="tag">${cloud.name}</span>#{/a}
  </span>
  #{/list}
  #{form @CloudPaster.addFavoriteTag()}
  <input type="text" name="tagName">
  <button>关注</button>
  #{/form}
</div>
