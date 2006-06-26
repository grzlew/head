package org.mifos.application.accounts.business;

import java.util.Date;

import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanSummaryEntity;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

import junit.framework.TestCase;

public class TestAccountFeesActionDetailEntity extends TestCase {

	protected AccountBO accountBO=null;
	protected CustomerBO center=null;
	protected CustomerBO group=null;
	
	public void testMakeEarlyRepaymentEnteriesForFeePayment(){
		for(AccountActionDateEntity accountActionDateEntity:accountBO.getAccountActionDates()){
			for(AccountFeesActionDetailEntity accountFeesActionDetailEntity:accountActionDateEntity.getAccountFeesActionDetails()){
				accountFeesActionDetailEntity.makeRepaymentEnteries(LoanConstants.PAY_FEES_PENALTY_INTEREST);
				assertEquals(accountFeesActionDetailEntity.getFeeAmount(),accountFeesActionDetailEntity.getFeeAmountPaid());
			}
		}
	}
	
	public void testMakeEarlyRepaymentEnteriesForNotPayingFee(){
		for(AccountActionDateEntity accountActionDateEntity:accountBO.getAccountActionDates()){
			for(AccountFeesActionDetailEntity accountFeesActionDetailEntity:accountActionDateEntity.getAccountFeesActionDetails()){
				accountFeesActionDetailEntity.makeRepaymentEnteries(LoanConstants.DONOT_PAY_FEES_PENALTY_INTEREST);
				assertEquals(accountFeesActionDetailEntity.getFeeAmount(),accountFeesActionDetailEntity.getFeeAmountPaid());
			}
		}
	}
	
	public void testWaiveCharges(){
		AccountActionDateEntity accountActionDate = (AccountActionDateEntity)group.getCustomerAccount().getAccountActionDates().toArray()[0];
		Money chargeWaived=new Money();
		for(AccountFeesActionDetailEntity accountFeesActionDetailEntity :  accountActionDate.getAccountFeesActionDetails()){
			chargeWaived=accountFeesActionDetailEntity.waiveCharges();
			assertEquals(new Money(),accountFeesActionDetailEntity.getFeeAmount());
		}
		assertEquals(new Money("100"),chargeWaived);
	}
	
	
	private AccountBO getLoanAccount() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}
	
	
	@Override
	protected void tearDown() throws Exception {
		accountBO=(AccountBO)HibernateUtil.getSessionTL().get(AccountBO.class,accountBO.getAccountId());
		group=(CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,group.getCustomerId());
		center=(CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,center.getCustomerId());
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);

		HibernateUtil.closeSession();
		super.tearDown();
	}

	@Override
	protected void setUp() throws Exception {
		accountBO=getLoanAccount();
		super.setUp();
	}	


}
