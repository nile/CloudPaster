<h3>我关注的分类</h3>
<div class="favorite-tags">
    <div class="tags">
    #{list items:_tags,as:'cloud'}
        <span style="white-space: nowrap" data-tag-id="${cloud.id}">
            #{a @CloudPaster.tag(cloud.name), rel:@CloudPaster.tagInfo(cloud.name)}<span class="tag">${cloud.name}</span>#{/a}
        </span>
    #{/list}
    </div>
    <div>
    #{form @CloudPaster.addFavoriteTag()}
        <input type="text" name="tagName">
        <button>关注</button>
    #{/form}
    </div>
</div>
