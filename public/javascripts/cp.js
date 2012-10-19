$(document).ready(function(){
    function show_info_box(a,info) {
    info_box = $.pnotify({
        text: info,
        icon: false,
        delay: 1500,
        history: false,
        stack: false,
        before_open: function(pnotify) {
            pnotify.css({
                top: a.position().top-pnotify.height()-20,
                left: a.position().left - pnotify.width() / 2
            });
        },
        before_close: function() {

        }
    });
    }
    $('.post-actions').delegate('a','click',function(a){
	a.preventDefault();
	var that = $(a.currentTarget);
	$.get(that.attr('href'),function(ret){
	    if(ret.state == 'failed'){
		show_info_box(that,ret.msg);
	    }else{
		window.location.href=that.attr('href');
	    }
	});
    });
    $('.vote-actions').delegate('a','click',function(a){
	a.preventDefault();
	var that = $(a.currentTarget);
	$.get(that.attr('href'),function(ret){
	    if(ret.state=='ok'){
		if(that.hasClass('vote-up')){
		    that.parent().parent().find('.vote-down').removeClass('vote-down-on');
		    that.toggleClass('vote-up-on');
		}else{
		    that.parent().parent().find('.vote-up').removeClass('vote-up-on');
		    that.toggleClass('vote-down-on');
		}
		that.parent().find('.vote-score').html(ret.newscore);
	    }else{
		show_info_box(that,ret.msg);
	    }
	})
    });
});