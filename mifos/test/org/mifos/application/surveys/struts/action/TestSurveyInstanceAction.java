package org.mifos.application.surveys.struts.action;

import java.util.Date;

import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.TestSurvey;
import org.mifos.application.surveys.helpers.InstanceStatus;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestDatabase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.PersistenceAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

import junit.framework.TestCase;

public class TestSurveyInstanceAction extends MifosMockStrutsTestCase {

	private TestDatabase database;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		database = TestDatabase.makeStandard();
		//PersistenceAction.setDefaultSessionOpener(database);
		
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/surveys/struts-config.xml")
				.getPath());
		UserContext userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
	}
	
	
	public void testCreate() throws Exception {
		SurveyInstance sampleInstance = TestSurvey.makeSurveyInstance("testCreate survey name");
		String clientId = Integer.toString(sampleInstance.getClient().getCustomerId());
		String officerId = Short.toString(sampleInstance.getOfficer().getPersonnelId());
		
		String dateConducted = DateUtils.makeDateAsSentFromBrowser();
		InstanceStatus status = InstanceStatus.INCOMPLETE;
		int surveyId = sampleInstance.getSurvey().getSurveyId();
		addRequestParameter("customerId", clientId);
		addRequestParameter("officerId", officerId);
		addRequestParameter("dateConducted", dateConducted);
		addRequestParameter("surveyId", Integer.toString(surveyId));
		addRequestParameter("instanceStatus", Integer.toString(status.getValue()));
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();
	}
	
}
