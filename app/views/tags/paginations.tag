%{  
	long cur = _caller.from/10+1;
	long pagecount = (int)(_caller.count/10) + (_caller.count%10>0?1:0) 
	boolean isfirst = cur == 1;
	boolean  islast = cur == pagecount;
	long lastindx = (pagecount-1)*10;
	
	long s = Math.max( cur-3,1);
	long e = Math.min(pagecount, cur+2);
	
}%
#{if _index};
	<div class="grid9 first  paginations">
	#{if _caller.count > 10}
		#{if isfirst}
			第一页
		#{/if}
		#{else}
			<a href="@{CloudPaster.index(0)}">第一页</a>
			<a href="@{CloudPaster.index(_caller.from-10)}">前一页</a>
		#{/else}	
		
		%{  if(s>1) out << "...";
			for( int a = s;a < e; a++ ){
		}%
			#{if a+1 == cur}
				${a+1}
			#{/if}
			#{else}
				<a href="@{CloudPaster.index(a*10)}">${a+1}</a>
			#{/else}
		%{
			}
			if(e<pagecount) out << "...";
		}%
		#{if islast }
			最后一页
		#{/if}
		#{else}
			<a href="@{CloudPaster.index(_caller.from+10)}">下一页</a>
			<a href="@{CloudPaster.index(lastindx)}">最后一页</a>
		#{/else}
	#{/if}
	</div>
#{/if}

#{if _my}
	<div class="grid8 first  paginations">
	#{if _caller.count > 10}
		#{if isfirst}
			第一页
		#{/if}
		#{else}
			<a href="@{CloudPaster.my(0)}">第一页</a>
			<a href="@{CloudPaster.my(_caller.from-10)}">前一页</a>
		#{/else}	
		
		%{  if(s>1) out << "...";
			for( int a = s;a < e; a++ ){
		}%
			#{if a+1 == cur}
				${a+1}
			#{/if}
			#{else}
				<a href="@{CloudPaster.my(a*10)}">${a+1}</a>
			#{/else}
		%{
			}
			if(e<pagecount) out << "...";
		}%
		#{if islast }
			最后一页
		#{/if}
		#{else}
			<a href="@{CloudPaster.my(_caller.from+10)}">下一页</a>
			<a href="@{CloudPaster.my(lastindx)}">最后一页</a>
		#{/else}
	#{/if}
	</div>
#{/if}

#{if _search}
	<div class="grid8 first  paginations">
	#{if _caller.count > 10}
		#{if isfirst}
			第一页
		#{/if}
		#{else}
			<a href="@{CloudPaster.search(_caller.keywords,0)}">第一页</a>
			<a href="@{CloudPaster.search(_caller.keywords,_caller.from-10)}">前一页</a>
		#{/else}	
		
		%{  if(s>1) out << "...";
			for( int a = s;a < e; a++ ){
		}%
			#{if a+1 == cur}
				${a+1}
			#{/if}
			#{else}
				<a href="@{CloudPaster.search(_caller.keywords,a*10)}">${a+1}</a>
			#{/else}
		%{
			}
			if(e<pagecount) out << "...";
		}%
		#{if islast }
			最后一页
		#{/if}
		#{else}
			<a href="@{CloudPaster.search(_caller.keywords,_caller.from+10)}">下一页</a>
			<a href="@{CloudPaster.search(_caller.keywords,lastindx)}">最后一页</a>
		#{/else}
	#{/if}
	</div>
#{/if}

#{if _unanswered}
	<div class="grid8 first  paginations">
	#{if _caller.count > 10}
		#{if isfirst}
			第一页
		#{/if}
		#{else}
			<a href="@{CloudPaster.unanswered(0)}">第一页</a>
			<a href="@{CloudPaster.unanswered(_caller.from-10)}">前一页</a>
		#{/else}	
		
		%{  if(s>1) out << "...";
			for( int a = s;a < e; a++ ){
		}%
			#{if a+1 == cur}
				${a+1}
			#{/if}
			#{else}
				<a href="@{CloudPaster.unanswered(a*10)}">${a+1}</a>
			#{/else}
		%{
			}
			if(e<pagecount) out << "...";
		}%
		#{if islast }
			最后一页
		#{/if}
		#{else}
			<a href="@{CloudPaster.unanswered(_caller.from+10)}">下一页</a>
			<a href="@{CloudPaster.unanswered(lastindx)}">最后一页</a>
		#{/else}
	#{/if}
	</div>
#{/if}

#{if _questions}
	<div class="grid8 first  paginations">
	#{if _caller.count > 10}
		#{if isfirst}
			第一页
		#{/if}
		#{else}
			<a href="@{CloudPaster.questions(0)}">第一页</a>
			<a href="@{CloudPaster.questions(_caller.from-10)}">前一页</a>
		#{/else}	
		
		%{  if(s>1) out << "...";
			for( int a = s;a < e; a++ ){
		}%
			#{if a+1 == cur}
				${a+1}
			#{/if}
			#{else}
				<a href="@{CloudPaster.questions(a*10)}">${a+1}</a>
			#{/else}
		%{
			}
			if(e<pagecount) out << "...";
		}%
		#{if islast }
			最后一页
		#{/if}
		#{else}
			<a href="@{CloudPaster.questions(_caller.from+10)}">下一页</a>
			<a href="@{CloudPaster.questions(lastindx)}">最后一页</a>
		#{/else}
	#{/if}
	</div>
#{/if}