<!--
 
 * createsavingsaccount.jsp  version: 1.0
 
 
 
 * Copyright � 2005-2006 Grameen Foundation USA
 
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 
 * All rights reserved.
 
 
 
 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 
 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 
 
 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 
 
 * and how it is applied. 
 
 *
 
 -->

<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
	<SCRIPT SRC="pages/application/savings/js/CreateSavingsAccount.js"></SCRIPT>
<html-el:form  action="/savingsAction.do?method=load">

    <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" class="heading">&nbsp;</td>
      </tr>
    </table>      
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td class="bluetablehead">  
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="33%">
              <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
                  <td class="timelineboldgray"><mifos:mifoslabel name="Savings.Select"/><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/></td>
                </tr>
              </table></td>
              <td width="34%" align="center">
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                    <td class="timelineboldorange">
                    <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                    <mifos:mifoslabel name="Savings.accountInformation"/>
                    </td>
                  </tr>
                </table>
              </td>
              <td width="33%" align="right">
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                    <td class="timelineboldorangelight"><mifos:mifoslabel name="Savings.review&Submit"/></td>
                  </tr>
                </table>
              </td>
            </tr>
          </table></td>
      </tr>
    </table>
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
        <tr>
          <td width="70%" height="24" align="left" valign="top" class="paddingleftCreates"><table width="98%" border="0" cellspacing="0" cellpadding="3">
              <tr>
                <td class="headingorange">
	                <span class="heading">
	                <mifos:mifoslabel name="Savings.Create"/>
	                <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
	                <mifos:mifoslabel name="Savings.account"/> - 
	                </span>
	                <mifos:mifoslabel name="Savings.Enter"/>
	                <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
	                <mifos:mifoslabel name="Savings.accountInformation"/>
                </td>
              </tr>
              <tr>
                <td class="fontnormal">
                <mifos:mifoslabel name="Savings.selectA"/>
                <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                <mifos:mifoslabel name="Savings.instance"/>
				<mifos:mifoslabel name="Savings.clickContinue"/>
				<mifos:mifoslabel name="Savings.clickCancel"/>
<br>            <font color="#FF0000">*</font><mifos:mifoslabel name="Savings.fieldsRequired"/> </td>
              </tr>
              
              
              <tr>
                <td class="fontnormal">
	                <font class="fontnormalRedBold"><html-el:errors	bundle="SavingsUIResources" /></font>
                <br>
                  <span class="fontnormalbold">
                  <mifos:mifoslabel name="Savings.accountOwner"/>: 
                  </span><c:out value="${sessionScope.BusinessKey.customer.displayName}" />
                </td>
              </tr>
            </table>
              <br>
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr class="fontnormal">
                  <td width="30%" align="right" class="fontnormal">
                  <span class="mandatorytext"><font color="#FF0000">*</font></span>
                  <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                  <mifos:mifoslabel name="Savings.instanceName"/>:</td>
                  <td width="70%">
             		<mifos:select name="savingsActionForm" property="selectedPrdOfferingId" style="width:136px;">
						<html-el:options collection="savingsPrdOfferings" property="prdOfferingId" labelProperty="prdOfferingName" />
					</mifos:select>
                  </td>
                </tr>
              </table>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>
              <br>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
                  <html-el:submit styleClass="buttn" style="width:70px;" >
						<mifos:mifoslabel name="loan.continue" />
				  </html-el:submit>
&nbsp;
    			  <html-el:button property="cancelButton" onclick="javascript:fun_createCancel(this.form)" styleClass="cancelbuttn" style="width:70px;">
						<mifos:mifoslabel name="loan.cancel" />
				  </html-el:button>
                  </td>
                </tr>
              </table>
              <br>
          </td>
        </tr>
      </table>
     <html-el:hidden property="input" value="getPrdOfferings"/>
</html-el:form>
</tiles:put>
</tiles:insert>
