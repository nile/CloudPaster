#{form @CloudPaster.search(),method:'GET',class:'searchform'}
    <table>
			<tr>
			  <td class="lightblack">查找问题</td>
			  <td>&nbsp;</td>
			  </tr>
			<tr><td>
				<input name="keywords" value="${keywords}" type="text">
			</td><td>
				<button type="submit" class="searchbutton"><img alt="搜索" src="@{'public/images/search.png'}"></button>
			</td></tr>
	</table>
#{/form}	