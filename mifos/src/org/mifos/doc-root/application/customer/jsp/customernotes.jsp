<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">
<html-el:form action="CustomerNoteAction.do?method=preview"> 
  <c:choose>
  	<c:when test="${sessionScope.customerNoteActionForm.input eq 'Client'}">
	  <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead05">
	            <span class="fontnormal8pt">
            	<a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            	<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a> 
					 /
	            </span>
	            <c:if test="${!empty sessionScope.linkValues.customerCenterName}">
	               <span class="fontnormal8pt">
	               	<a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerCenterGCNum}"/>">
				       	<c:out value="${sessionScope.linkValues.customerCenterName}"/>
			       	</a>  /  </span>
		    	</c:if>
	           <c:if test="${!empty sessionScope.linkValues.customerParentName}">
	               <span class="fontnormal8pt">
	               	<a href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
				       	<c:out value="${sessionScope.linkValues.customerParentName}"/>
			       	</a>  /  </span>
		    	</c:if>
	            <!-- Name of the client -->
	            <span class="fontnormal8pt">
	            	<a href="clientCreationAction.do?method=get&customerId=<c:out value="${sessionScope.linkValues.customerId}"/>">
	            	<c:out value="${sessionScope.linkValues.customerName}"/>
	            	</a>
	            </span>
            </td>
          </tr>
        </table>
	  </c:when>
	  <c:when test="${sessionScope.customerNoteActionForm.input eq 'Center'}">
	  <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead05">
	            <span class="fontnormal8pt">
					<a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            	<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a> 
					 /
	            </span>
	            
	               <span class="fontnormal8pt">
	               	<a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
				       	<c:out value="${sessionScope.linkValues.customerName}"/>
			       	</a> 
	               </span>
            </td>
          </tr>
        </table>
	  </c:when>
	  <c:otherwise>
	  <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
          <span class="fontnormal8pt">
	          <a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            	<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a> 
					 /
           	<c:if test="${!empty sessionScope.linkValues.customerParentName}">
               	<a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
			       	<c:out value="${sessionScope.linkValues.customerParentName}"/>
		       	</a> /  
		    </c:if>
          <a href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
          	<c:out value="${sessionScope.linkValues.customerName}"/>
           </a>
           </span></td>
        </tr>
      </table>
	  </c:otherwise>
	  </c:choose>
  
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15" >
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="52%" class="headingorange"><span class="heading">          	
                <c:out value="${sessionScope.linkValues.customerName}"/> -</span>
                  <mifos:mifoslabel name="label.notes" bundle="CustomerUIResources"></mifos:mifoslabel>
                    
                    </td>
                <td width="48%" align="right" class="fontnormal">
                <a href="CustomerNoteAction.do?method=load">
                <mifos:mifoslabel name="label.addanote" bundle="CustomerUIResources"></mifos:mifoslabel>
                
                </a> </td>
              </tr>
              <tr>
	   		<td colspan="2">
   				<font class="fontnormalRedBold"><html-el:errors bundle="CustomerUIResources"/></font>
			</td>
			</tr>
            </table>
            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td>
                <mifos:mifostabletagdata name="notes" key="allnotes" type="single" 
       			className="CustomerNote" width="100%" border="0" cellspacing="0" cellpadding="0"/>
                </td>
              </tr>
            </table>
           
            <br>
          </td>
        </tr>
      </table>
<mifos:SecurityParam property="${sessionScope.customerNoteActionForm.input}"></mifos:SecurityParam>
</html-el:form>
</tiles:put>
</tiles:insert>