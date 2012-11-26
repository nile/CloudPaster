<h3>查找问题</h3>
#{form @CloudPaster.search(),method:'GET',class:'searchform'}
<table>
  <tr><td>
      <input name="keywords" value="${keywords}" type="text" style="width:120px;margin-bottom:auto;">
    </td><td>
      <button type="submit" class="btn">
	搜问题
      </button>
  </td></tr>
</table>
#{/form}	
