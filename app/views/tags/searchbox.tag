<h3>查找问题</h3>
#{form @CloudPaster.search(),method:'GET',class:'searchform'}
<table>
  <tr><td>
      <input name="keywords" value="${keywords}" type="text">
    </td><td>
      <button type="submit" class="searchbutton"><img alt="搜索" src="@{'public/images/search.png'}"></button>
  </td></tr>
</table>
#{/form}	
