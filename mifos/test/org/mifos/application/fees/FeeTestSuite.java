package org.mifos.application.fees;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.mifos.application.fees.business.TestFeeFrequencyEntity;
import org.mifos.application.fees.business.TestFeesBO;
import org.mifos.application.fees.persistence.service.TestFeesPersistenceService;
import org.mifos.application.fees.struts.action.TestFeeAction;
import org.mifos.framework.MifosTestSuite;

public class FeeTestSuite extends MifosTestSuite {

	public FeeTestSuite() {
		super();
	}
	
	public static void main(String[] args){
		try{
			Test testSuite = suite();
			TestRunner.run (testSuite);	
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static Test suite()throws Exception
	{
		FeeTestSuite testSuite = new FeeTestSuite();
		testSuite.addTestSuite(TestFeesPersistenceService.class);
		testSuite.addTestSuite(TestFeeFrequencyEntity.class);
		testSuite.addTestSuite(TestFeesBO.class);
		testSuite.addTestSuite(TestFeeAction.class);
		return testSuite;
		
	}
}
