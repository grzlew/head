/**
 * 
 */
package org.mifos.application.accounts.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.loan.business.LoanActivityEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanSummaryEntity;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * @author krishankg
 * 
 */
public class TestAccountBO extends TestAccount {
	
	public TestAccountBO() {
	}

	public void testSuccessRemoveFees() {
		try {
			HibernateUtil.getSessionTL();
			HibernateUtil.startTransaction();
			UserContext uc = TestObjectFactory.getUserContext();
			Set<AccountFeesEntity> accountFeesEntitySet = accountBO
					.getAccountFees();
			Iterator itr = accountFeesEntitySet.iterator();
			while (itr.hasNext())
				accountBO.removeFees(((AccountFeesEntity) itr.next()).getFees()
						.getFeeId(), uc.getId());
			HibernateUtil.getTransaction().commit();
			for (AccountFeesEntity accountFeesEntity : accountFeesEntitySet) {
				assertEquals(accountFeesEntity.getFeeStatus(),
						AccountConstants.INACTIVE_FEES);
			}
			LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
					.getLoanSummary();
			for (LoanActivityEntity accountNonTrxnEntity : ((LoanBO) accountBO)
					.getLoanActivityDetails()) {
				assertEquals(loanSummaryEntity.getOriginalFees().subtract(
						loanSummaryEntity.getFeesPaid()), accountNonTrxnEntity
						.getFeeOutstanding());
				assertEquals(loanSummaryEntity.getOriginalPrincipal().subtract(
						loanSummaryEntity.getPrincipalPaid()),
						accountNonTrxnEntity.getPrincipalOutstanding());
				assertEquals(loanSummaryEntity.getOriginalInterest().subtract(
						loanSummaryEntity.getInterestPaid()),
						accountNonTrxnEntity.getInterestOutstanding());
				assertEquals(loanSummaryEntity.getOriginalPenalty().subtract(
						loanSummaryEntity.getPenaltyPaid()),
						accountNonTrxnEntity.getPenaltyOutstanding());
				break;
			}
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	public void testFailureRemoveFees() {
		try {
			HibernateUtil.getSessionTL();
			HibernateUtil.startTransaction();
			UserContext uc = TestObjectFactory.getUserContext();
			Set<AccountFeesEntity> accountFeesEntitySet = accountBO
					.getAccountFees();
			Iterator itr = accountFeesEntitySet.iterator();
			while (itr.hasNext())
				accountBO.removeFees(((AccountFeesEntity) itr.next()).getFees()
						.getFeeId(), uc.getId());
			HibernateUtil.getTransaction().commit();
			assert (false);
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	public void testSuccessUpdateTotalFeeAmount() {
		LoanBO loanBO = (LoanBO) accountBO;
		LoanSummaryEntity loanSummaryEntity = loanBO.getLoanSummary();
		Money orignalFeesAmount = loanSummaryEntity.getOriginalFees();
		loanBO.updateTotalFeeAmount(new Money(TestObjectFactory
				.getMFICurrency(), "20"));
		assertEquals(loanSummaryEntity.getOriginalFees(), (orignalFeesAmount
				.subtract(new Money(TestObjectFactory.getMFICurrency(), "20"))));
	}

	public void testSuccessUpdateAccountActionDateEntity() {
		AccountPersistanceService accountPersistanceService = new AccountPersistanceService();
		List<Short> installmentIdList;
		try {
			installmentIdList = accountPersistanceService
					.getNextInstallmentList(accountBO.getAccountId());
			Set<AccountFeesEntity> accountFeesEntitySet = accountBO
					.getAccountFees();
			Iterator itr = accountFeesEntitySet.iterator();
			while (itr.hasNext()) {
				accountBO.updateAccountActionDateEntity(installmentIdList,
						((AccountFeesEntity) itr.next()).getFees().getFeeId());
				assertTrue(true);
			}
		} catch (Exception e) {
			assertFalse(false);
		} finally {
			accountPersistanceService = null;
		}
	}

	public void testSuccessUpdateAccountFeesEntity() {
		Set<AccountFeesEntity> accountFeesEntitySet = accountBO
				.getAccountFees();
		Iterator itr = accountFeesEntitySet.iterator();
		while (itr.hasNext()) {
			AccountFeesEntity accountFeesEntity = (AccountFeesEntity) itr
					.next();
			accountBO.updateAccountFeesEntity(accountFeesEntity.getFees()
					.getFeeId());
			assertEquals(accountFeesEntity.getFeeStatus(),
					AccountConstants.INACTIVE_FEES);
		}
	}

	public void testGetLastLoanPmntAmnt() throws Exception {
		Date currentDate = new Date(System.currentTimeMillis());
		LoanBO loan = (LoanBO) accountBO;
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData paymentData = TestObjectFactory
				.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(212 * 6), null, Short
						.valueOf("1"), "receiptNum", Short.valueOf("1"),
						currentDate,currentDate);
		loan.applyPayment(paymentData);

		TestObjectFactory.updateObject(loan);
		TestObjectFactory.flushandCloseSession();
		assertEquals(
				"The amount returned for the payment should have been 1272",
				1272.0, loan.getLastPmntAmnt());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				loan.getAccountId());
	}

	public void testLoanAdjustment() throws Exception {
		Date currentDate = new Date(System.currentTimeMillis());
		LoanBO loan = (LoanBO) accountBO;
		loan.setUserContext(TestObjectFactory.getUserContext());
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.add(loan.getAccountActionDate(Short.valueOf("1")));
		PaymentData accountPaymentDataView = TestObjectFactory
				.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(212), null, Short.valueOf("1"),
						"receiptNum", Short.valueOf("1"), currentDate,currentDate);
		loan.applyPayment(accountPaymentDataView);

		TestObjectFactory.updateObject(loan);
		TestObjectFactory.flushandCloseSession();
		loan.adjustPmnt("loan account has been adjusted by test code");

		TestObjectFactory.updateObject(loan);
		assertEquals("The amount returned for the payment should have been 0",
				0.0, loan.getLastPmntAmnt());
		LoanTrxnDetailEntity lastLoanTrxn = null;
		for (AccountTrxnEntity accntTrxn : loan.getLastPmnt().getAccountTrxns()) {
			lastLoanTrxn = (LoanTrxnDetailEntity) accntTrxn;
			break;
		}
		AccountActionDateEntity installment = loan
				.getAccountActionDate(lastLoanTrxn.getInstallmentId());
		assertEquals(
				"The installment adjusted should now be marked unpaid(due).",
				installment.getPaymentStatus(), AccountConstants.PAYMENT_UNPAID);

	}

	public void testAdjustmentForClosedAccnt() throws Exception {
		Date currentDate = new Date(System.currentTimeMillis());
		LoanBO loan = (LoanBO) accountBO;

		loan.setAccountState(new AccountStateEntity(
				AccountStates.LOANACC_OBLIGATIONSMET));
		loan.setUserContext(TestObjectFactory.getUserContext());
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData accountPaymentDataView = TestObjectFactory
				.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(212 * 6), null, Short
						.valueOf("1"), "receiptNum", Short.valueOf("1"),
						currentDate,currentDate);
		loan.applyPayment(accountPaymentDataView);

		TestObjectFactory.updateObject(loan);
		try {
			loan.adjustPmnt("loan account has been adjusted by test code");
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	public void testRetrievalOfNullMonetaryValue() throws Exception {
		Date currentDate = new Date(System.currentTimeMillis());
		LoanBO loan = (LoanBO) accountBO;
		loan.setUserContext(TestObjectFactory.getUserContext());
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData accountPaymentDataView = TestObjectFactory
				.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(0), null, Short.valueOf("1"),
						"receiptNum", Short.valueOf("1"), currentDate,currentDate);

		loan.applyPayment(accountPaymentDataView);
		TestObjectFactory.updateObject(loan);
		TestObjectFactory.flushandCloseSession();
		loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());

		Money pmntAmnt = null;
		for (AccountPaymentEntity accntPmnt : loan.getAccountPayments()) {
			pmntAmnt = accntPmnt.getAmount();
		}
		TestObjectFactory.flushandCloseSession();
		assertEquals(
				"Account payment retrieved should be zero with currency MFI currency",
				TestObjectFactory.getMoneyForMFICurrency(0), pmntAmnt);
	}

	public void testGetTransactionHistoryView() throws Exception {
		Date currentDate = new Date(System.currentTimeMillis());
		LoanBO loan = (LoanBO) accountBO;
		loan.setUserContext(TestObjectFactory.getUserContext());
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData accountPaymentDataView = TestObjectFactory
				.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(0), null, Short.valueOf("1"),
						"receiptNum", Short.valueOf("1"), currentDate,
						currentDate);
		loan.applyPayment(accountPaymentDataView);
		TestObjectFactory.flushandCloseSession();
		loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());
		loan.setUserContext(TestObjectFactory.getUserContext());
		List<TransactionHistoryView> trxnHistlist =loan.getTransactionHistoryView();
		assertNotNull("Account TrxnHistoryView list object should not be null",trxnHistlist);
		assertTrue("Account TrxnHistoryView list object Size should be greater than zero",trxnHistlist.size()>0);
		TestObjectFactory.flushandCloseSession();
		accountBO = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());
	}
	
	public void testGetPeriodicFeeList(){		
		AccountFeesEntity accountOneTimeFee = new AccountFeesEntity();
		accountOneTimeFee.setAccount(accountBO);		
		accountOneTimeFee.setAccountFeeAmount(new Money("1.0"));
		accountOneTimeFee.setFeeAmount(new Money("1.0"));
		FeesBO oneTimeFee = TestObjectFactory.createOneTimeFees("One Time Fee ", 20.0,(short) 2, 5);		
		accountOneTimeFee.setFees(oneTimeFee);
		accountBO.addAccountFees(accountOneTimeFee);
		accountPersistence.createOrUpdate(accountBO);
		TestObjectFactory.flushandCloseSession();
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		assertEquals(1,accountBO.getPeriodicFeeList().size());		
		
	}
}
