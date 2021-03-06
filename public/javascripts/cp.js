$(document).ready(function() {
    var info_box;
    function show_info_box(a, info) {
        if(info_box)
            info_box.pnotify_remove();
        info_box = undefined;
        info_box = $.pnotify({
            text: info,
            icon: false,
            insert_brs:false,
            sticker:false,
            closer:false,
            delay: 1000,
            history: false,
            stack: false,
            before_open: function(pnotify) {
                pnotify.css({
                    top: a.position().top - pnotify.height() - 20,
                    left: a.position().left - pnotify.width() / 2
                });
            },
            before_close: function() {

            }
        });
    }
    var tag_delay_hover = {
            sensitivity: 1,
            interval: 300,
            over:function(a){
                $.get($(a.currentTarget).attr('rel'),function(ret){
                   show_info_box($(a.currentTarget),ret);
                });
            },
            out: function(){}
      };
    $('.favorite-tags a').hoverIntent(tag_delay_hover);
    $('body').delegate('a.bt-tag-unfocused','click',function(a){
        a.preventDefault();
        var that = $(a.currentTarget);
        $.get(that.attr('href'),function(ret){
            var tags = $('.favorite-tags').find('.tags');
            tags.find('[data-tag-id='+ret.data.id+']').detach() ;
            show_info_box(tags,ret.msg)
        });

    });
    $('.favorite-tags').delegate('button', 'click', function(b) {
        b.preventDefault();
        var that = $(b.currentTarget);
        var frm = that.parent('form');
        $(frm).ajaxSubmit(function(ret) {
            if (ret.state === 'ok') {
                var tags = that.parents('.favorite-tags').find('.tags');
                var ele = $(
                        '<span style="white-space: nowrap" data-tag-id="'+ret.data.id + '">'
                        +'<a href="/tag/' + ret.data.name +'" rel="/taginfo/'+ret.data.name+'"><span class="tag btn btn-warning">' + ret.data.name + '</span></a></span>');
                ele.appendTo(tags);
                ele.find('a').hoverIntent(tag_delay_hover);
            } else {
                show_info_box(that, ret.msg);
            }
        });
    });
    $('.post-actions').delegate('a', 'click', function(a) {
        a.preventDefault();
        var that = $(a.currentTarget);
        $.get(that.attr('href'), function(ret) {
            if (ret.state === 'failed') {
                show_info_box(that, ret.msg);
            } else {
                window.location.href = that.attr('href');
            }
        });
    });
    $('.vote-actions').delegate('a', 'click', function(a) {
        a.preventDefault();
        var that = $(a.currentTarget);
        $.get(that.attr('href'), function(ret) {
            if (ret.state === 'ok') {
                if (that.hasClass('vote-up')) {
                    that.parent().parent().find('.vote-down').removeClass('vote-down-on');
                    that.toggleClass('vote-up-on');
                } else {
                    that.parent().parent().find('.vote-up').removeClass('vote-up-on');
                    that.toggleClass('vote-down-on');
                }
                that.parent().find('.vote-score').html(ret.data);
            } else {
                show_info_box(that, ret.msg);
            }
        });
    });
    $('.share-buttons').delegate('a', 'click', function(a){
	a.preventDefault();
	var that = $(a.currentTarget);
	if(that.hasClass('weibo')){
	    share_weibo(that);
	}else if(that.hasClass('qqwb')){
	    share_qqwb(that);
	}else if(that.hasClass('renren')){
	    share_renren(that);
	}else if(that.hasClass('twitter')){
	    share_twitter(that);
	}
    });
});

function share_weibo(t) {
    window.open("http://service.weibo.com/share/share.php?url=" + t.attr('href') + "&title=" + $(document).attr('title'), "_blank", "width=615,height=505");
}
function share_qqwb(t) {
    window.open("http://share.v.t.qq.com/index.php?c=share&a=index&appkey=801290473&url=" + t.attr('href') + "&title=" + $(document).attr('title') , "_blank", "width=615,height=505");
}
function share_renren(t) {
    window.open("http://widget.renren.com/dialog/share?resourceUrl=" + t.attr('href') + "&srcUrl=" + t.attr('href') + "&title=" + $(document).attr('title') + "&pic=&description="+jQuery('meta[name=description]').attr("content"), "_blank", "width=615,height=705");
}
function share_qzone(t) {
    window.open("http://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?showcount=1&otype=share&url=" + jQuery("#"+id).data('url') + "&title=" + $(document).attr('title'), "_blank", "width=615,height=505");
}
function share_twitter(id) {
    window.open("http://twitter.com/intent/tweet?original_referer=&source=" + t.attr('href') + "tweetbutton&text=" + $(document).attr('title') + "&url=" + t.attr('href') + "&via=aqee_net", "_blank", "width=615,height=505");
}
