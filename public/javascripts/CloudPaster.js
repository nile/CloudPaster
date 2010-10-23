CloudPaster = new Class({	
	initialize: function(params){  
          this.loadurl = params.loadurl;
		  this.pasteurl = params.pasteurl;
		  this.UsefulUrl=params.UsefulUrl;
    },
	paste:function(content){
		var handler = this;
		var myRequest = new Request({method: 'post', 
			url: this.pasteurl,
			onComplete: function(ret){handler.pastSuccess(ret)}
		});
		myRequest.send("content="+content);
	},
	pastSuccess : function(text) {
		var json = JSON.decode(text);
		var stat = json.stat;
		if(stat=='fail' && 'ERR-001'==json.err.code){
			$('paster-content').addClass("error");
		}else if(stat == 'ok') {			
			var obj = json.paster;	
			this.injectFirst(obj);
			SqueezeBox.close();
		}
	},
	appendLast : function(obj){
		var pasters_list = $('pasters-list');		
		var first_paster = pasters_list.getLast('div');
		var clz = 'even';//(first_paster && first_paster.hasClass('odd-border')) ? 'even':'odd';
		var paster = this.createPaster(obj,clz);	
		paster.inject(pasters_list,'bottom');	
	},
	injectFirst : function(obj){
		var pasters_list = $('pasters-list');		
		var first_paster = pasters_list.getFirst('div');
		var clz = 'even';//(first_paster && first_paster.hasClass('odd-border')) ? 'even':'odd';
		var paster = this.createPaster(obj,clz);
		paster.inject(pasters_list,'top');
	},

	createPaster : function(obj,clz){
		var paster = $('paster-template').clone();
		paster.set('id','');
		paster.addClass(clz+'-border');
		var header = paster.getFirst('.paster-header');
		header.addClass(clz);
		var link = header.getElement('a');

		link.set('href',obj.viewurl)
		link.set('html',obj.key);
		var createDate = header.getElement('span');		
		createDate.set('html',obj.createDate);
		//header.getElement('.vote-up').set('onclick','javascript:ratingup("'+obj.ratingupurl+'")');
		//header.getElement('.rating-value').set('id',obj.key+'-rating');
		//header.getElement('.vote-down').set('onclick','javascript:ratingdown("'+obj.ratingdownurl+'")');
		var handler = this;
		header.getElement('.useful').addEvent('click',function(){handler.markuse(obj.usefulurl);});
		header.getElement('.useless').addEvent('click',function(){handler.markuse(obj.uselessurl);});
		paster.getElement('.paster-content').set('html',obj.contentAsHtml);
		return paster;
	},
	
	loadmore : function(){	
		//alert(this.loadurl);
		var handler = this;
		var rating = new Request({method: 'get',
			url: handler.loadurl,
			onSuccess: function(ret){
				json=JSON.decode(ret);
				if(json.stat=='ok'){					
					for (i=0; i<json.pasters.length; i++){					
						handler.appendLast(json.pasters[i]);
					}
					handler.loadurl = json.nextloadurl;
				};
			}
			});
		rating.send("");
	},
	markuse: function(url){		
		var markuse = new Request({method: 'post',
			url: url
		});
		markuse.send();
		this.loadUseful();
	},
	rating : function (url){
		var rating = new Request({method: 'post',
			url: url,
			onSuccess: function(ret){
				json=JSON.decode(ret);
				if(json.stat=='ok'){
					$(key+'-rating').set('html',json.paster.rating);
				};
			}
			});
		rating.send("");
	},
	
	loadUseful : function(){
		//alert(this.UsefulUrl);		
		var rating = new Request({method: 'get',
				url: this.UsefulUrl,
				onSuccess: function(ret){
					$('useful-list').empty();
					json=JSON.decode(ret);
					if(json.stat=='ok'){
						//alert(json);					
						for (i=0; i<json.pasters.length; i++){		
							var obj = json.pasters[i];			
							var p = $('useful-template').clone();
							var link = p.getElement('a');
							link.set('href',obj.viewurl)
							link.set('html',obj.key);
							p.getElement('.useful-count').set('html',obj.useful);
							p.inject($('useful-list'),'bottom');
							
						}
						//handler.loadurl = json.nextloadurl;
					};
				}
				});
		rating.send();
	}
});