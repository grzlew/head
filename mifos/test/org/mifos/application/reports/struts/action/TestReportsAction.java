package org.mifos.application.reports.struts.action;

import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import servletunit.struts.MockStrutsTestCase;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class TestReportsAction extends MockStrutsTestCase {
	private UserContext userContext ;
	
	public TestReportsAction() {
	}

	public TestReportsAction(String name) {
		super(name);
	}
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI(
					"WEB-INF/web.xml").getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/framework/util/helpers/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en", "US"));
		userContext.setBranchId(new Short("1"));
		userContext.setBranchGlobalNum("0001");
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		
	}
	
	public void testVerifyForwardOfReport(){
		addRequestParameter("viewPath", "report_designer");
		setRequestPathInfo("/reportsAction.do");
		addRequestParameter("method","getReportPage");
        actionPerform();
		verifyForwardPath("/pages/application/reports/jsp/report_designer.jsp");
	}
}
