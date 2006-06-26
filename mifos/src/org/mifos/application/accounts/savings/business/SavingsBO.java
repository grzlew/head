/**

 * SavingsBO.java    version: 1.0

 

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

 */
package org.mifos.application.accounts.savings.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFlagMapping;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateFlagEntity;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.exceptions.AccountExceptionConstants;
import org.mifos.application.accounts.exceptions.IDGenerationException;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.savings.persistence.service.SavingsPersistenceService;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountPaymentData;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.exception.StateChangeException;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.master.util.valueobjects.AccountType;
import org.mifos.application.master.util.valueobjects.InterestCalcType;
import org.mifos.application.master.util.valueobjects.RecommendedAmntUnit;
import org.mifos.application.master.util.valueobjects.SavingsType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.business.MeetingRecurrenceEntity;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.interestcalculator.InterestCalculationException;
import org.mifos.framework.components.interestcalculator.InterestCalculatorConstansts;
import org.mifos.framework.components.interestcalculator.InterestCalculatorFactory;
import org.mifos.framework.components.interestcalculator.InterestCalculatorIfc;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class SavingsBO extends AccountBO {

	private Money recommendedAmount;

	private Money savingsBalance;

	private SavingsOfferingBO savingsOffering;

	private SavingsPerformanceEntity savingsPerformance;

	private Date activationDate;

	private RecommendedAmntUnit recommendedAmntUnit;

	private SavingsType savingsType;

	private Money interestToBePosted;

	private Date lastIntCalcDate;

	private Date lastIntPostDate;

	private Date nextIntCalcDate;

	private Date nextIntPostDate;

	private Date interIntCalcDate;

	private Money minAmntForInt;

	private Double interestRate;

	private InterestCalcType interestCalcType;

	private MeetingBO timePerForInstcalc;

	private MeetingBO freqOfPostIntcalc;

	private Set<SavingsActivityEntity> savingsActivityDetails;

	private SavingsPersistenceService dbService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	private SavingsHelper helper = new SavingsHelper();

	protected SavingsBO() {
		savingsActivityDetails = new HashSet<SavingsActivityEntity>();
	}

	public SavingsBO(UserContext userContext) {
		super(userContext);
		savingsActivityDetails = new HashSet<SavingsActivityEntity>();
	}

	public Money getRecommendedAmount() {
		return recommendedAmount;
	}

	public void setRecommendedAmount(Money recommendedAmount) {
		this.recommendedAmount = recommendedAmount;
	}

	public Money getSavingsBalance() {
		return savingsBalance;
	}

	public void setSavingsBalance(Money savingsBalance) {
		this.savingsBalance = savingsBalance;
	}

	public SavingsOfferingBO getSavingsOffering() {
		return savingsOffering;
	}

	public void setSavingsOffering(SavingsOfferingBO savingsOffering) {
		this.savingsOffering = savingsOffering;
	}

	public SavingsPerformanceEntity getSavingsPerformance() {
		return savingsPerformance;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public RecommendedAmntUnit getRecommendedAmntUnit() {
		return recommendedAmntUnit;
	}

	public void setRecommendedAmntUnit(RecommendedAmntUnit recommendedAmntUnit) {
		this.recommendedAmntUnit = recommendedAmntUnit;
	}

	public SavingsType getSavingsType() {
		return savingsType;
	}

	public void setSavingsType(SavingsType savingsType) {
		this.savingsType = savingsType;
	}

	public Money getInterestToBePosted() {
		return interestToBePosted;
	}

	public void setInterestToBePosted(Money interestToBePosted) {
		this.interestToBePosted = interestToBePosted;
	}

	public Date getInterIntCalcDate() {
		return interIntCalcDate;
	}

	public void setInterIntCalcDate(Date interIntCalcDate) {
		this.interIntCalcDate = interIntCalcDate;
	}

	public Date getLastIntCalcDate() {
		return lastIntCalcDate;
	}

	public void setLastIntCalcDate(Date lastIntCalcDate) {
		this.lastIntCalcDate = lastIntCalcDate;
	}

	public Date getLastIntPostDate() {
		return lastIntPostDate;
	}

	public void setLastIntPostDate(Date lastIntPostDate) {
		this.lastIntPostDate = lastIntPostDate;
	}

	public Date getNextIntCalcDate() {
		return nextIntCalcDate;
	}

	public void setNextIntCalcDate(Date nextIntCalcDate) {
		this.nextIntCalcDate = nextIntCalcDate;
	}

	public Date getNextIntPostDate() {
		return nextIntPostDate;
	}

	public void setNextIntPostDate(Date nextIntPostDate) {
		this.nextIntPostDate = nextIntPostDate;
	}

	public MeetingBO getFreqOfPostIntcalc() {
		return freqOfPostIntcalc;
	}

	public InterestCalcType getInterestCalcType() {
		return interestCalcType;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public Money getMinAmntForInt() {
		return minAmntForInt;
	}

	public MeetingBO getTimePerForInstcalc() {
		return timePerForInstcalc;
	}

	private void setFreqOfPostIntcalc(MeetingBO freqOfPostIntcalc) {
		this.freqOfPostIntcalc = freqOfPostIntcalc;
	}

	private void setInterestCalcType(InterestCalcType interestCalcType) {
		this.interestCalcType = interestCalcType;
	}

	private void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	private void setMinAmntForInt(Money minAmntForInt) {
		this.minAmntForInt = minAmntForInt;
	}

	private void setTimePerForInstcalc(MeetingBO timePerForInstcalc) {
		this.timePerForInstcalc = timePerForInstcalc;
	}

	public Set<SavingsActivityEntity> getSavingsActivityDetails() {
		return savingsActivityDetails;
	}

	private void setSavingsActivityDetails(
			Set<SavingsActivityEntity> savingsActivityDetails) {
		this.savingsActivityDetails = savingsActivityDetails;
	}

	public void addSavingsActivityDetails(SavingsActivityEntity savingsActivity) {
		savingsActivity.setAccount(this);
		savingsActivityDetails.add(savingsActivity);
	}

	private void setSavingsOfferingDetails() {
		setMinAmntForInt(getSavingsOffering().getMinAmntForInt());
		setInterestRate(getSavingsOffering().getInterestRate());
		setInterestCalcType(getSavingsOffering().getInterestCalcType());
		setTimePerForInstcalc(getMeeting(getSavingsOffering()
				.getTimePerForInstcalc().getMeeting()));
		setFreqOfPostIntcalc(getMeeting(getSavingsOffering()
				.getFreqOfPostIntcalc().getMeeting()));
	}

	private MeetingBO getMeeting(MeetingBO offeringMeeting) {
		MeetingBO meeting = new MeetingBO();
		meeting.setMeetingStartDate(offeringMeeting.getMeetingStartDate());
		meeting.setMeetingStartTime(offeringMeeting.getMeetingStartTime());
		meeting.setMeetingEndDate(offeringMeeting.getMeetingEndDate());
		meeting.setMeetingEndTime(offeringMeeting.getMeetingEndTime());
		meeting.setMeetingPlace(offeringMeeting.getMeetingPlace());
		meeting.setMeetingType(offeringMeeting.getMeetingType());

		MeetingDetailsEntity meetingDetails = new MeetingDetailsEntity();
		meetingDetails.setRecurAfter(offeringMeeting.getMeetingDetails()
				.getRecurAfter());
		meetingDetails.setRecurrenceType(offeringMeeting.getMeetingDetails()
				.getRecurrenceType());

		MeetingRecurrenceEntity meetingRecurrence = new MeetingRecurrenceEntity();
		meetingRecurrence.setWeekDay(offeringMeeting.getMeetingDetails()
				.getMeetingRecurrence().getWeekDay());
		meetingRecurrence.setRankOfDays(offeringMeeting.getMeetingDetails()
				.getMeetingRecurrence().getRankOfDays());
		meetingRecurrence.setDayNumber(offeringMeeting.getMeetingDetails()
				.getMeetingRecurrence().getDayNumber());

		meetingDetails.setMeetingRecurrence(meetingRecurrence);
		meeting.setMeetingDetails(meetingDetails);

		return meeting;
	}

	private void setSavingsPerformance(
			SavingsPerformanceEntity savingsPerformance) {
		if (savingsPerformance != null)
			savingsPerformance.setSavings(this);
		this.savingsPerformance = savingsPerformance;
	}

	private SavingsPersistenceService getDBService() throws ServiceException {
		if (dbService == null) {
			dbService = (SavingsPersistenceService) ServiceFactory
					.getInstance().getPersistenceService(
							PersistenceServiceName.Savings);
		}
		return dbService;
	}

	private PersonnelPersistenceService getPersonnelDBService()
			throws ServiceException {
		return (PersonnelPersistenceService) ServiceFactory.getInstance()
				.getPersistenceService(PersistenceServiceName.Personnel);
	}

	private SavingsPerformanceEntity createSavingsPerformance() {
		SavingsPerformanceEntity savingsPerformance = new SavingsPerformanceEntity();
		logger
				.info("In SavingsBO::createSavingsPerformance(), SavingsPerformanceEntity created successfully ");
		return savingsPerformance;
	}

	public void save() throws IDGenerationException, SchedulerException,
			SystemException, ApplicationException {
		logger.info("In SavingsBO::save(), Before Saving , accountId: "
				+ getAccountId());
		this.setGlobalAccountNum(generateId(userContext.getBranchGlobalNum()));
		logger.info("In SavingsBO::save(), Generated globalAccountNum: "
				+ getGlobalAccountNum());
		setOffice(getCustomer().getOffice());
		setAccountType(new AccountType(new Short(AccountTypes.SAVINGSACCOUNT)));
		setSavingsPerformance(createSavingsPerformance());
		setSavingsBalance(new Money());
		this.setCreatedBy(userContext.getId());
		this.setCreatedDate(new Date());
		this.setSavingsType(getSavingsOffering().getSavingsType());
		this.setRecommendedAmntUnit(getSavingsOffering()
				.getRecommendedAmntUnit());
		this.setSavingsOfferingDetails();

		// generated the deposit action dates only if savings account is being
		// saved in approved state
		if (isActive())
			setValuesForActiveState();

		// security check befor saving
		checkPermissionForSave(this.getAccountState(), userContext, null);

		getDBService().save(this);
		logger.info("In SavingsBO::save(), Successfully saved , accountId: "
				+ getAccountId());
	}

	public void update() throws SystemException {
		logger.debug("In SavingsBO::update(), accountId: " + getAccountId());
		this.setUpdatedBy(userContext.getId());
		this.setUpdatedDate(new Date());
		getDBService().update(this);
		logger
				.info("In SavingsBO::update(), successfully updated , accountId: "
						+ getAccountId());
	}

	public void updateAndGenerateSchedule() throws SystemException {
		logger.debug("In SavingsBO::updateSchedule(), accountId: "
				+ getAccountId());
		for (AccountActionDateEntity accountDate : this.getAccountActionDates()) {
			if (accountDate.getActionDate().compareTo(helper.getCurrentDate()) >= 0)
				accountDate.setDeposit(getRecommendedAmount());
		}
		update();
		logger
				.info("In SavingsBO::updateSchedule(), successfully updated Deposit Schedule, accountId: "
						+ getAccountId());
	}

	public boolean isMandatory() {
		logger.debug("In SavingsBO::isMandatory(), savingTypeId: "
				+ getSavingsType().getSavingsTypeId());
		return getSavingsType().getSavingsTypeId().shortValue() == ProductDefinitionConstants.MANDATORY
				.shortValue();
	}

	public boolean isDepositScheduleBeRegenerated() {
		logger
				.debug("In SavingsBO::isDepositScheduleBeRegenerated(), accountStateId: "
						+ getAccountState().getId());
		return (getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_APPROVED || getAccountState()
				.getId().shortValue() == AccountStates.SAVINGS_ACC_INACTIVE);
	}

	public boolean isActive() {
		return getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_APPROVED;
	}

	public Money calculateInterestForClosure(Date closureDate)
			throws InterestCalculationException, SystemException {
		return calculateInterest(getInterestRate(), closureDate);
	}

	public void postInterest() throws SchedulerException, SystemException,
			FinancialException {
		if (getInterestToBePosted() != null
				&& getInterestToBePosted().getAmountDoubleValue() > 0) {
			Money interestPosted = getInterestToBePosted();
			setSavingsBalance(getSavingsBalance().add(interestPosted));
			setInterestToBePosted(new Money());
			setLastIntPostDate(getNextIntPostDate());
			setNextIntPostDate(helper.getNextScheduleDate(getActivationDate(),
					getLastIntPostDate(), getFreqOfPostIntcalc()));
			PaymentTypeEntity paymentType = (PaymentTypeEntity) HibernateUtil
					.getSession().get(PaymentTypeEntity.class,
							SavingsConstants.DEFAULT_PAYMENT_TYPE);
			makeEntriesForInterestPosting(interestPosted, paymentType,
					getCustomer(), null);
			getDBService().update(this);
		}
	}

	public void updateInterestAccrued() throws InterestCalculationException,
			SchedulerException, SystemException {
		if (getInterestToBePosted() == null)
			setInterestToBePosted(new Money());
		setInterestToBePosted(getInterestToBePosted().add(
				calculateInterest(getInterestRate(), getNextIntCalcDate())));
		setLastIntCalcDate(getNextIntCalcDate());
		setNextIntCalcDate(helper.getNextScheduleDate(getActivationDate(),
				getLastIntCalcDate(), getTimePerForInstcalc()));
		getDBService().update(this);
	}

	private Money calculateInterest(double interestRate, Date toDate)
			throws InterestCalculationException, SystemException {
		logger.debug("In SavingsBO::calculateInterest(), accountId: "
				+ getAccountId());
		Date fromDate = getFromDate();
		Money principal = null;
		Money interestAmount = new Money();
		SavingsTrxnDetailEntity trxn = null;

		if (getActivationDate().equals(fromDate)) {
			trxn = getDBService().retrieveFirstTransaction(getAccountId());
			if (trxn != null)
				fromDate = trxn.getActionDate();
		} else {
			trxn = getDBService().retrieveLastTransaction(getAccountId(),
					fromDate);
			if (trxn == null) {
				trxn = getDBService().retrieveFirstTransaction(getAccountId());
				if (trxn != null)
					fromDate = trxn.getActionDate();
			}
		}

		if (trxn != null
				&& getInterestCalcType().getInterestCalculationTypeID().equals(
						ProductDefinitionConstants.MINIMUM_BALANCE))
			principal = getMinimumBalance(fromDate, toDate, trxn);
		else if (trxn != null
				&& getInterestCalcType().getInterestCalculationTypeID().equals(
						ProductDefinitionConstants.AVERAGE_BALANCE))
			principal = getAverageBalance(fromDate, toDate, trxn);

		// Do not Calculate interest if principal amount is less than the
		// minimum amount needed for interest calculation
		if (getMinAmntForInt() != null
				&& principal != null
				&& (getMinAmntForInt().getAmountDoubleValue() == 0 || principal
						.getAmountDoubleValue() >= getMinAmntForInt()
						.getAmountDoubleValue()))
			interestAmount = calculateInterestForDays(principal, interestRate,
					fromDate, toDate);
		return interestAmount;
	}

	private Date getFromDate() {
		return (getInterIntCalcDate() != null) ? getInterIntCalcDate()
				: (getLastIntCalcDate() != null ? getLastIntCalcDate()
						: getActivationDate());
	}

	public void closeAccount(AccountPaymentEntity payment,
			AccountNotesEntity notes, CustomerBO customer)
			throws SystemException, FinancialException {
		logger.debug("In SavingsBO::closeAccount(), accountId: "
				+ getAccountId());
		PersonnelBO loggedInUser = getPersonnelDBService().getPersonnel(
				userContext.getId());
		AccountStateEntity accountState = this.getAccountState();
		this.setAccountState(getDBService().getAccountStatusObject(
				AccountStates.SAVINGS_ACC_CLOSED));
		if (getInterestToBePosted() != null
				&& getInterestToBePosted().getAmountDoubleValue() > 0) {
			AccountPaymentEntity interestPayment = helper.createAccountPayment(
					getInterestToBePosted(), payment.getPaymentType(),
					loggedInUser);
			interestPayment.addAcountTrxn(helper.createAccountPaymentTrxn(
					interestPayment, payment.getAmount(),
					AccountConstants.ACTION_SAVINGS_INTEREST_POSTING, customer,
					loggedInUser));
			this.addAccountPayment(interestPayment);
			buildFinancialEntries(interestPayment.getAccountTrxns());
		}
		if (payment.getAmount().getAmountDoubleValue() > 0) {
			payment.addAcountTrxn(helper.createAccountPaymentTrxn(payment,
					new Money(), AccountConstants.ACTION_SAVINGS_WITHDRAWAL,
					customer, loggedInUser));
			payment.setCreatedDate(helper.getCurrentDate());
			payment.setPaymentDate(helper.getCurrentDate());
			this.addAccountPayment(payment);
			buildFinancialEntries(payment.getAccountTrxns());
		}
		notes.setCommentDate(new java.sql.Date(helper.getCurrentDate()
				.getTime()));
		notes.setPersonnel(loggedInUser);
		this.addAccountNotes(notes);
		this.setLastIntCalcDate(helper.getCurrentDate());
		this.setLastIntPostDate(helper.getCurrentDate());
		this.setInterIntCalcDate(null);
		this.setSavingsBalance(new Money());
		this.setInterestToBePosted(new Money());

		this.setClosedDate(new Date(System.currentTimeMillis()));
		this
				.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
						accountState, this.getAccountState(), loggedInUser
								.getPersonnelId()));
		this.update();
		logger
				.debug("In SavingsBO::close(), account closed successfully ; accountId: "
						+ getAccountId());
	}

	private void makeEntriesForInterestPosting(Money interestAmt,
			PaymentTypeEntity paymentType, CustomerBO customer,
			PersonnelBO loggedInUser) throws SystemException,
			FinancialException {
		AccountPaymentEntity interestPayment = helper.createAccountPayment(
				interestAmt, paymentType, loggedInUser);
		interestPayment.addAcountTrxn(helper.createAccountPaymentTrxn(
				interestPayment, interestAmt,
				AccountConstants.ACTION_SAVINGS_INTEREST_POSTING, customer,
				loggedInUser));
		this.addAccountPayment(interestPayment);
		buildFinancialEntries(interestPayment.getAccountTrxns());
	}

	protected Money getMinimumBalance(Date fromDate, Date toDate,
			SavingsTrxnDetailEntity initialTrxn) {
		logger.debug("In SavingsBO::getMinimumBalance(), accountId: "
				+ getAccountId());
		Money minBal = initialTrxn.getBalance();
		List<AccountTrxnEntity> accountTrxnList = getAccountTrxnsOrderByTrxnDate();
		for (int i = 0; i < accountTrxnList.size(); i++) {
			if (accountTrxnList.get(i).getActionDate().compareTo(fromDate) >= 0
					&& accountTrxnList.get(i).getActionDate().compareTo(toDate) < 0) {
				i = getLastTrxnIndexForDay(accountTrxnList, i);
				SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accountTrxnList
						.get(i);
				if ((initialTrxn.getActionDate().equals(
						savingsTrxn.getActionDate()) && initialTrxn
						.getAccountTrxnId() < savingsTrxn.getAccountTrxnId())
						|| (minBal.getAmountDoubleValue() > savingsTrxn
								.getBalance().getAmountDoubleValue()))
					minBal = savingsTrxn.getBalance();
			}
		}
		return minBal;
	}

	private int getLastTrxnIndexForDay(List<AccountTrxnEntity> accountTrxnList,
			int i) {
		AccountTrxnEntity accountTrxn;
		do {
			accountTrxn = accountTrxnList.get(i);
			i++;
		} while (i < accountTrxnList.size()
				&& accountTrxn.getActionDate().equals(
						accountTrxnList.get(i).getActionDate()));
		return i - 1;
	}

	protected Money getAverageBalance(Date fromDate, Date toDate,
			SavingsTrxnDetailEntity initialTrxn) {
		logger.debug("In SavingsBO::getAverageBalance(), accountId: "
				+ getAccountId());
		int noOfDays = 0;
		List<AccountTrxnEntity> accountTrxnList = getAccountTrxnsOrderByTrxnDate();
		Money initialBalance = initialTrxn.getAmount();
		Money totalBalance = new Money();

		for (int i = 0; i < accountTrxnList.size(); i++) {
			if (accountTrxnList.get(i).getActionDate().compareTo(fromDate) >= 0
					&& accountTrxnList.get(i).getActionDate().compareTo(toDate) < 0) {
				i = getLastTrxnIndexForDay(accountTrxnList, i);
				SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accountTrxnList
						.get(i);
				int days = helper.calculateDays(fromDate, savingsTrxn
						.getActionDate());
				fromDate = savingsTrxn.getActionDate();
				if (initialTrxn.getActionDate().equals(
						savingsTrxn.getActionDate())
						&& initialTrxn.getAccountTrxnId() < savingsTrxn
								.getAccountTrxnId())
					initialBalance = savingsTrxn.getAmount();
				totalBalance = totalBalance.add(new Money(Configuration
						.getInstance().getSystemConfig().getCurrency(),
						initialBalance.getAmountDoubleValue() * days));
				initialBalance = savingsTrxn.getBalance();
				noOfDays += days;
			}
		}
		int days = helper.calculateDays(fromDate, toDate);
		totalBalance = totalBalance.add(new Money(Configuration.getInstance()
				.getSystemConfig().getCurrency(), initialBalance
				.getAmountDoubleValue()
				* days));
		noOfDays += days;
		return (noOfDays == 0 ? initialBalance : new Money(Configuration
				.getInstance().getSystemConfig().getCurrency(), totalBalance
				.getAmountDoubleValue()
				/ noOfDays));
	}

	private Money calculateInterestForDays(Money principal,
			double interestRate, Date fromDate, Date toDate)
			throws InterestCalculationException {
		int days = helper.calculateDays(fromDate, toDate);
		InterestCalculatorIfc calculator = InterestCalculatorFactory
				.getInterestCalculator(InterestCalculatorConstansts.COMPOUND_INTEREST);
		return calculator.getInterest(helper.createInterestInputs(principal,
				interestRate, days, InterestCalculatorConstansts.DAYS));
	}

	public void generateAndUpdateDepositActionsForClient(CustomerBO client)
			throws SchedulerException, SystemException {
		if (client.getCustomerMeeting().getMeeting() != null) {
			if (!(getCustomer().getCustomerLevel().getLevelId().shortValue() == CustomerConstants.GROUP_LEVEL_ID && getRecommendedAmntUnit()
					.getRecommendedAmntUnitId().shortValue() == ProductDefinitionConstants.COMPLETEGROUP
					.shortValue())) {
				generateDepositAccountActions(client, client
						.getCustomerMeeting().getMeeting());
				this.update();
			}
		}
	}

	private void generateDepositAccountActions() throws SchedulerException,
			SystemException {
		logger.debug("In SavingsBO::generateDepositAccountActions()");
		// deposit happens on each meeting date of the customer. If for
		// center/group with individual deposits, insert row for every client
		if (getCustomer().getCustomerMeeting() != null
				&& getCustomer().getCustomerMeeting().getMeeting() != null) {
			MeetingBO depositSchedule = getCustomer().getCustomerMeeting()
					.getMeeting();

			depositSchedule.setMeetingStartDate(Calendar.getInstance());
			if (getCustomer().getCustomerLevel().getLevelId().equals(
					CustomerConstants.CLIENT_LEVEL_ID)
					|| (getCustomer().getCustomerLevel().getLevelId().equals(
							CustomerConstants.GROUP_LEVEL_ID) && getRecommendedAmntUnit()
							.getRecommendedAmntUnitId().shortValue() == ProductDefinitionConstants.COMPLETEGROUP)) {
				generateDepositAccountActions(getCustomer(), depositSchedule);
			} else {
				List<CustomerBO> children = getCustomer().getChildren(
						CustomerConstants.CLIENT_LEVEL_ID);
				for (CustomerBO customer : children) {
					generateDepositAccountActions(customer, depositSchedule);
				}
			}
		}
	}

	private void generateDepositAccountActions(CustomerBO customer,
			MeetingBO meeting) throws SchedulerException {
		SchedulerIntf scheduler = SchedulerHelper.getScheduler(meeting);
		List<Date> depositDates = scheduler.getAllDates();
		short installmentNumber = 1;
		for (Date dt : depositDates) {
			AccountActionDateEntity actionDate = helper.createActionDateObject(
					customer, dt, userContext.getId(), getRecommendedAmount());
			actionDate.setInstallmentId(installmentNumber++);
			addAccountActionDate(actionDate);
			logger
					.debug("In SavingsBO::generateDepositAccountActions(), Successfully added account action on date: "
							+ dt);
		}
	}

	protected AccountPaymentEntity makePayment(PaymentData paymentData)
			throws AccountException, SystemException {
		Money totalAmount = paymentData.getTotalAmount();
		Money enteredAmount = totalAmount;
		Date transactionDate = paymentData.getTransactionDate();
		List<AccountPaymentData> accountPayments = paymentData
				.getAccountPayments();
		CustomerBO customer = new CustomerPersistenceService()
				.getCustomer(paymentData.getCustomerId());
		AccountPaymentEntity accountPayment = new AccountPaymentEntity();
		accountPayment.setPaymentDetails(totalAmount, paymentData
				.getRecieptNum(), paymentData.getRecieptDate(), paymentData
				.getPaymentTypeId());
		if (totalAmount.getAmountDoubleValue() > 0
				&& paymentData.getAccountPayments().size() <= 0) {
			SavingsTrxnDetailEntity accountTrxn = buildUnscheduledDeposit(
					totalAmount, paymentData.getPersonnelId(), customer,
					transactionDate);
			accountPayment.addAcountTrxn(accountTrxn);
			return accountPayment;
		}
		for (AccountPaymentData accountPaymentData : accountPayments) {
			AccountActionDateEntity accountAction = getAccountActionDate(
					accountPaymentData.getInstallmentId(), customer
							.getCustomerId());
			if (accountAction != null
					&& enteredAmount.getAmountDoubleValue() > 0.0) {
				if (accountAction.getPaymentStatus().equals(
						AccountConstants.PAYMENT_PAID))
					throw new AccountException("errors.update",
							new String[] { getGlobalAccountNum() });
				Money depositAmount = new Money();
				Short paymentStatus = AccountConstants.PAYMENT_UNPAID;
				if (enteredAmount.getAmountDoubleValue() > accountAction
						.getTotalDepositDue().getAmountDoubleValue()) {
					depositAmount = accountAction.getTotalDepositDue();
					enteredAmount = enteredAmount.subtract(accountAction
							.getTotalDepositDue());
					paymentStatus = AccountConstants.PAYMENT_PAID;
				} else {
					depositAmount = enteredAmount;
					enteredAmount = new Money();
				}
				if (getSavingsType().getSavingsTypeId().equals(
						ProductDefinitionConstants.VOLUNTARY)
						&& depositAmount.getAmountDoubleValue() > 0.0)
					paymentStatus = AccountConstants.PAYMENT_PAID;
				savingsBalance = savingsBalance.add(depositAmount);
				// TODO uncomment when savingsperformance is implemented
				// savingsPerformance.setPaymentDetails(depositAmount);
				accountAction.setPaymentDetails(depositAmount, paymentStatus,
						new java.sql.Date(transactionDate.getTime()));
				SavingsTrxnDetailEntity accountTrxn = new SavingsTrxnDetailEntity();
				accountTrxn.setAccount(this);
				accountTrxn.setPaymentDetails(depositAmount, accountAction
						.getActionDate(), customer, paymentData
						.getPersonnelId(), transactionDate);
				accountTrxn.setInstallmentId(accountAction.getInstallmentId());
				accountPayment.addAcountTrxn(accountTrxn);
			}
		}
		if (enteredAmount.getAmountDoubleValue() > 0) {
			SavingsTrxnDetailEntity accountTrxn = buildUnscheduledDeposit(
					enteredAmount, paymentData.getPersonnelId(), customer,
					transactionDate);
			accountPayment.addAcountTrxn(accountTrxn);
		}
		return accountPayment;
	}

	private SavingsTrxnDetailEntity buildUnscheduledDeposit(
			Money depositAmount, Short personnelId, CustomerBO customer,
			Date transactionDate) throws ServiceException {
		SavingsTrxnDetailEntity accountTrxn = new SavingsTrxnDetailEntity();
		savingsBalance = savingsBalance.add(depositAmount);
		accountTrxn.setAccount(this);
		accountTrxn.setPaymentDetails(depositAmount, new java.sql.Date(System
				.currentTimeMillis()), customer, personnelId, transactionDate);
		// TODO uncomment when savingsperformance is implemented
		// savingsPerformance.setPaymentDetails(depositAmount);
		return accountTrxn;
	}

	public void withdraw(PaymentData accountPaymentData)
			throws AccountException, SystemException {
		Money totalAmount = accountPaymentData.getTotalAmount();
		Date transactionDate = accountPaymentData.getTransactionDate();
		if (totalAmount.getAmountDoubleValue() > savingsBalance
				.getAmountDoubleValue()) {
			throw new AccountException("errors.insufficentbalance",
					new String[] { getGlobalAccountNum() });
		}
		Double maxWithdrawAmount = getSavingsOffering().getMaxAmntWithdrawl()
				.getAmountDoubleValue();
		if (maxWithdrawAmount != null && maxWithdrawAmount != 0
				&& totalAmount.getAmountDoubleValue() > maxWithdrawAmount) {
			throw new AccountException("errors.exceedmaxwithdrawal",
					new String[] { getGlobalAccountNum() });
		}
		savingsBalance = savingsBalance.subtract(totalAmount);
		// TODO uncomment when savingsperformance is implemented
		// savingsPerformance.setWithdrawDetails(totalAmount);

		CustomerBO customer = new CustomerPersistenceService()
				.getCustomer(accountPaymentData.getCustomerId());
		AccountPaymentEntity accountPayment = new AccountPaymentEntity();
		accountPayment.setPaymentDetails(totalAmount, accountPaymentData
				.getRecieptNum(), accountPaymentData.getRecieptDate(),
				accountPaymentData.getPaymentTypeId());

		SavingsTrxnDetailEntity accountTrxnBO = new SavingsTrxnDetailEntity();
		accountTrxnBO.setAccount(this);
		accountTrxnBO.setWithdrawalDetails(totalAmount, new java.sql.Date(
				System.currentTimeMillis()), customer, accountPaymentData
				.getPersonnelId(), transactionDate);
		accountPayment.addAcountTrxn(accountTrxnBO);
		addAccountPayment(accountPayment);
		try {
			buildFinancialEntries(accountPayment.getAccountTrxns());
			getDBService().update(this);
		} catch (FinancialException fe) {
			throw new AccountException("errors.update", fe);
		}
	}

	public List<AccountStateEntity> getStatusList() {
		List<AccountStateEntity> statusList = SavingsStateMachine.getInstance()
				.getStatusList(this.getAccountState());
		if (null != statusList) {
			for (AccountStateEntity accStateObj : statusList) {
				accStateObj.setLocaleId(userContext.getLocaleId());
			}
		}
		return statusList;
	}

	public void initializeSavingsStateMachine(Short localeId)
			throws StatesInitializationException {
		SavingsStateMachine.getInstance().initialize(localeId,
				getOffice().getOfficeId());
	}

	public String getStatusName(Short localeId, Short accountStateId)
			throws ApplicationException, SystemException {
		return SavingsStateMachine.getInstance().getStatusName(localeId,
				accountStateId);
	}

	public String getFlagName(Short flagId) throws ApplicationException,
			SystemException {
		return SavingsStateMachine.getInstance().getFlagName(flagId);
	}

	private void setValuesForActiveState() throws SchedulerException,
			SystemException {
		this.setActivationDate(new Date(new java.util.Date().getTime()));
		this.generateDepositAccountActions();
		this.setNextIntCalcDate(helper.getNextScheduleDate(getActivationDate(),
				null, getTimePerForInstcalc()));
		this.setNextIntPostDate(helper.getNextScheduleDate(getActivationDate(),
				null, getFreqOfPostIntcalc()));
	}

	public AccountStateEntity retrieveAccountStateEntityMasterObject(
			AccountStateEntity accountStateEntity) {
		return SavingsStateMachine.getInstance()
				.retrieveAccountStateEntityMasterObject(accountStateEntity);
	}

	private void activationDateHelper(Short newStatusId)
			throws SchedulerException, SystemException {
		if (Configuration.getInstance().getAccountConfig(
				getOffice().getOfficeId())
				.isPendingApprovalStateDefinedForSavings()) {
			if (this.getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_PENDINGAPPROVAL
					&& newStatusId.shortValue() == AccountStates.SAVINGS_ACC_APPROVED) {
				setValuesForActiveState();
			}
		} else {
			if (this.getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_PARTIALAPPLICATION
					&& newStatusId.shortValue() == AccountStates.SAVINGS_ACC_APPROVED) {
				setValuesForActiveState();
			}
		}
	}

	public void changeStatus(AccountStateEntity newState,
			AccountNotesEntity accountNotesEntity,
			AccountStateFlagEntity flagSelected, UserContext userContext)
			throws ApplicationException, SystemException {
		checkStatusChangeAllowed(newState);
		// permission Check
		checkPermissionForStatusChange(newState, userContext, flagSelected);

		activationDateHelper(newState.getId());
		if (null != flagSelected)
			setFlag(flagSelected);
		setStatus(newState, accountNotesEntity);
		this.update();
	}

	private void setFlag(AccountStateFlagEntity accountStateFlagEntity) {
		// remove all previous flags except the blacklisted ones denoted by
		// retained flag
		Iterator iter = this.getAccountFlags().iterator();
		while (iter.hasNext()) {
			AccountFlagMapping currentFlag = (AccountFlagMapping) iter.next();
			if (!currentFlag.getFlag().isFlagRetained())
				iter.remove();
		}
		this.addAccountFlag(accountStateFlagEntity);
	}

	private void setStatus(AccountStateEntity accountStateEntity,
			AccountNotesEntity accountNotesEntity) throws ServiceException {
		AccountStateEntity accountState = this.getAccountState();
		this.setAccountState(this
				.retrieveAccountStateEntityMasterObject(accountStateEntity));
		this.addAccountNotes(accountNotesEntity);
		if (accountStateEntity.getId().equals(Short.valueOf("15"))
				|| accountStateEntity.getId().equals(Short.valueOf("17")))
			this.setClosedDate(new Date(System.currentTimeMillis()));
		this
				.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
						accountState, this.getAccountState(), userContext
								.getId()));
	}

	public void adjustLastUserAction(Money amountAdjustedTo,
			String adjustmentComment) throws ApplicationException,
			SystemException {
		logger
				.debug("In SavingsBO::generateDepositAccountActions(), accountId: "
						+ getAccountId());
		if (!isAdjustPossibleOnLastTrxn(amountAdjustedTo)) {
			throw new ApplicationException(
					AccountExceptionConstants.CANNOTADJUST);
		}
		adjustExistingPayment(amountAdjustedTo, adjustmentComment);
		makeAdjustmentPayment(amountAdjustedTo, adjustmentComment);
		this.update();
	}

	private void adjustExistingPayment(Money amountAdjustedTo,
			String adjustmentComment) throws SystemException,
			ApplicationException {
		AccountPaymentEntity lastPayment = getLastPmnt();
		lastPayment.setUserContext(getUserContext());
		for (AccountTrxnEntity accntTrxn : lastPayment.getAccountTrxns()) {
			if (lastPayment.getActionType().equals(
					AccountConstants.ACTION_SAVINGS_DEPOSIT))
				adjustForDeposit(accntTrxn);
			else if (lastPayment.getActionType().equals(
					AccountConstants.ACTION_SAVINGS_WITHDRAWAL))
				adjustForWithdrawal(accntTrxn);
		}
		logger.debug("transaction count before adding reversal transactions: "
				+ lastPayment.getAccountTrxns().size());
		List<AccountTrxnEntity> newlyAddedTrxns = lastPayment
				.reversalAdjustment(adjustmentComment);
		for (AccountTrxnEntity accountTrxn : newlyAddedTrxns) {
			accountTrxn.setPersonnel(getPersonnelDBService().getPersonnel(
					userContext.getId()));
		}
		buildFinancialEntries(new HashSet<AccountTrxnEntity>(newlyAddedTrxns));
	}

	private void makeAdjustmentPayment(Money amountAdjustedTo,
			String adjustmentComment) throws SystemException,
			FinancialException {
		AccountPaymentEntity lastPayment = getLastPmnt();
		AccountPaymentEntity newAccountPayment = null;

		lastPayment.setUserContext(getUserContext());
		if (amountAdjustedTo.getAmountDoubleValue() > 0) {
			newAccountPayment = helper.createAccountPayment(amountAdjustedTo,
					lastPayment.getPaymentType(), getPersonnelDBService()
							.getPersonnel(userContext.getId()));
			newAccountPayment.setPaymentDate(lastPayment.getPaymentDate());
		}
		if (newAccountPayment != null) {
			newAccountPayment.setAmount(amountAdjustedTo);
			Set<AccountTrxnEntity> accountTrxns = createTrxnsForAmountAdjusted(
					lastPayment, amountAdjustedTo);
			for (AccountTrxnEntity accountTrxn : accountTrxns) {
				newAccountPayment.addAcountTrxn(accountTrxn);
			}
			this.addAccountPayment(newAccountPayment);
			buildFinancialEntries(newAccountPayment.getAccountTrxns());
		}
	}

	private List<AccountActionDateEntity> getAccountActions(Date dueDate,
			Integer customerId) {
		List<AccountActionDateEntity> accountActions = new ArrayList<AccountActionDateEntity>();
		for (AccountActionDateEntity accountAction : getAccountActionDates()) {
			if (accountAction.getActionDate().compareTo(dueDate) <= 0
					&& accountAction.getPaymentStatus().equals(
							AccountConstants.PAYMENT_UNPAID)
					&& accountAction.getCustomer().getCustomerId().equals(
							customerId))
				accountActions.add(accountAction);
		}
		return accountActions;
	}

	protected Set<AccountTrxnEntity> createTrxnsForAmountAdjusted(
			AccountPaymentEntity lastAccountPayment, Money newAmount)
			throws SystemException {
		if (isMandatory()
				&& lastAccountPayment.getActionType().equals(
						AccountConstants.ACTION_SAVINGS_DEPOSIT))
			return createDepositTrxnsForMandatoryAccountsAfterAdjust(
					lastAccountPayment, newAmount);

		if (lastAccountPayment.getActionType().equals(
				AccountConstants.ACTION_SAVINGS_DEPOSIT))
			return createDepositTrxnsForVolAccountsAfterAdjust(
					lastAccountPayment, newAmount);

		Set<AccountTrxnEntity> newTrxns = new HashSet<AccountTrxnEntity>();
		SavingsTrxnDetailEntity accountTrxn = null;
		// create transaction for withdrawal
		SavingsTrxnDetailEntity oldSavingsAccntTrxn = null;
		for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment
				.getAccountTrxns()) {
			oldSavingsAccntTrxn = (SavingsTrxnDetailEntity) oldAccntTrxn;
			break;
		}
		accountTrxn = new SavingsTrxnDetailEntity();
		setSavingsBalance(getSavingsBalance().subtract(newAmount));
		accountTrxn.setTrxnDetails(AccountConstants.ACTION_SAVINGS_WITHDRAWAL,
				newAmount, getSavingsBalance(), oldSavingsAccntTrxn
						.getCustomer(), getPersonnelDBService().getPersonnel(
						userContext.getId()), oldSavingsAccntTrxn.getDueDate(),
				oldSavingsAccntTrxn.getActionDate());
		// getSavingsPerformance().setTotalWithdrawals(getSavingsPerformance().getTotalWithdrawals().add(accountTrxn.getWithdrawlAmount()));
		newTrxns.add(accountTrxn);
		return newTrxns;
	}

	private Set<AccountTrxnEntity> createDepositTrxnsForMandatoryAccountsAfterAdjust(
			AccountPaymentEntity lastAccountPayment, Money newAmount)
			throws SystemException {
		Set<AccountTrxnEntity> newTrxns = new HashSet<AccountTrxnEntity>();
		SavingsTrxnDetailEntity accountTrxn = null;
		CustomerBO customer = null;
		for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment
				.getAccountTrxns()) {
			customer = oldAccntTrxn.getCustomer();
			break;
		}
		List<AccountActionDateEntity> accountActionList = getAccountActions(
				lastAccountPayment.getPaymentDate(), customer.getCustomerId());
		for (AccountActionDateEntity accountAction : accountActionList) {
			if (newAmount.getAmountDoubleValue() == 0)
				break;
			accountTrxn = new SavingsTrxnDetailEntity();
			if (accountAction.getDeposit().getAmountDoubleValue() <= newAmount
					.getAmountDoubleValue()) {
				accountTrxn.setDepositAmount(accountAction.getDeposit());
				newAmount = newAmount.subtract(accountAction.getDeposit());
				accountAction.setDepositPaid(accountTrxn.getDepositAmount());
				accountAction.setPaymentStatus(AccountConstants.PAYMENT_PAID);
				accountAction.setPaymentDate(new java.sql.Date(
						lastAccountPayment.getPaymentDate().getTime()));
			} else {
				accountTrxn.setDepositAmount(newAmount);
				newAmount = newAmount.subtract(newAmount);
				accountAction.setDepositPaid(accountTrxn.getDepositAmount());
				accountAction.setPaymentStatus(AccountConstants.PAYMENT_UNPAID);
				accountAction.setPaymentDate(new java.sql.Date(
						lastAccountPayment.getPaymentDate().getTime()));
			}
			accountTrxn.setInstallmentId(accountAction.getInstallmentId());
			setSavingsBalance(getSavingsBalance().add(
					accountTrxn.getDepositAmount()));
			accountTrxn.setTrxnDetails(AccountConstants.ACTION_SAVINGS_DEPOSIT,
					accountTrxn.getDepositAmount(), getSavingsBalance(),
					customer, getPersonnelDBService().getPersonnel(
							userContext.getId()),
					accountAction.getActionDate(), lastAccountPayment
							.getPaymentDate());
			// getSavingsPerformance().setTotalDeposits(getSavingsPerformance().getTotalDeposits().add(accountTrxn.getDepositAmount()));
			newTrxns.add(accountTrxn);
		}
		// add trxn for excess amount
		if (newAmount.getAmountDoubleValue() > 0) {
			accountTrxn = new SavingsTrxnDetailEntity();
			accountTrxn.setDepositAmount(newAmount);
			newAmount = newAmount.subtract(newAmount);
			setSavingsBalance(getSavingsBalance().add(
					accountTrxn.getDepositAmount()));
			accountTrxn.setTrxnDetails(AccountConstants.ACTION_SAVINGS_DEPOSIT,
					accountTrxn.getDepositAmount(), getSavingsBalance(),
					customer, getPersonnelDBService().getPersonnel(
							userContext.getId()), null, lastAccountPayment
							.getPaymentDate());
			// getSavingsPerformance().setTotalDeposits(getSavingsPerformance().getTotalDeposits().add(accountTrxn.getDepositAmount()));
			newTrxns.add(accountTrxn);
		}

		return newTrxns;
	}

	private Set<AccountTrxnEntity> createDepositTrxnsForVolAccountsAfterAdjust(
			AccountPaymentEntity lastAccountPayment, Money newAmount)
			throws SystemException {
		Set<AccountTrxnEntity> newTrxns = new HashSet<AccountTrxnEntity>();
		SavingsTrxnDetailEntity accountTrxn = null;
		CustomerBO customer = null;
		for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment
				.getAccountTrxns()) {
			customer = oldAccntTrxn.getCustomer();
			break;
		}
		for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment
				.getAccountTrxns()) {
			if (oldAccntTrxn.getAccountActionEntity().getId().equals(
					AccountConstants.ACTION_SAVINGS_DEPOSIT)) {
				SavingsTrxnDetailEntity oldSavingsAccntTrxn = (SavingsTrxnDetailEntity) oldAccntTrxn;
				if (oldAccntTrxn.getInstallmentId() != null) {
					accountTrxn = new SavingsTrxnDetailEntity();
					AccountActionDateEntity accountAction = getAccountActionDate(
							oldSavingsAccntTrxn.getInstallmentId(),
							oldSavingsAccntTrxn.getCustomer().getCustomerId());
					if (accountAction.getDeposit().getAmountDoubleValue() <= newAmount
							.getAmountDoubleValue()) {
						accountTrxn
								.setDepositAmount(accountAction.getDeposit());
						newAmount = newAmount.subtract(accountAction
								.getDeposit());
					} else if (newAmount.getAmountDoubleValue() != 0) {
						accountTrxn.setDepositAmount(newAmount);
						newAmount = newAmount.subtract(newAmount);
					}
					accountTrxn.setInstallmentId(oldAccntTrxn
							.getInstallmentId());
					setSavingsBalance(getSavingsBalance().add(
							accountTrxn.getDepositAmount()));
					accountTrxn.setTrxnDetails(
							AccountConstants.ACTION_SAVINGS_DEPOSIT,
							accountTrxn.getDepositAmount(),
							getSavingsBalance(), customer,
							getPersonnelDBService().getPersonnel(
									userContext.getId()), accountAction
									.getActionDate(), lastAccountPayment
									.getPaymentDate());
					// getSavingsPerformance().setTotalDeposits(getSavingsPerformance().getTotalDeposits().add(accountTrxn.getDepositAmount()));
					break;
				}
			}
		}

		if (accountTrxn != null)
			newTrxns.add(accountTrxn);
		// Create a new transaction with remaining amount
		if (newAmount.getAmountDoubleValue() > 0) {
			accountTrxn = new SavingsTrxnDetailEntity();
			accountTrxn.setDepositAmount(newAmount);
			accountTrxn.setAmount(newAmount);
			setSavingsBalance(getSavingsBalance().add(
					accountTrxn.getDepositAmount()));
			accountTrxn.setTrxnDetails(AccountConstants.ACTION_SAVINGS_DEPOSIT,
					accountTrxn.getDepositAmount(), getSavingsBalance(),
					customer, getPersonnelDBService().getPersonnel(
							userContext.getId()), null, lastAccountPayment
							.getPaymentDate());
			// getSavingsPerformance().setTotalDeposits(getSavingsPerformance().getTotalDeposits().add(accountTrxn.getDepositAmount()));
			newTrxns.add(accountTrxn);
		}
		return newTrxns;
	}

	private void adjustForDeposit(AccountTrxnEntity accntTrxn) {
		SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accntTrxn;
		Short installmentId = savingsTrxn.getInstallmentId();
		setSavingsBalance(getSavingsBalance().subtract(
				savingsTrxn.getDepositAmount()));
		AccountActionDateEntity accntActionDate = getAccountActionDate(
				installmentId, accntTrxn.getCustomer().getCustomerId());
		if (accntActionDate != null) {
			accntActionDate.setDepositPaid(accntActionDate.getDepositPaid()
					.subtract(savingsTrxn.getDepositAmount()));
			accntActionDate.setPaymentStatus(AccountConstants.PAYMENT_UNPAID);
			accntActionDate.setPaymentDate(null);
		}
		// getSavingsPerformance().setTotalDeposits(getSavingsPerformance().getTotalDeposits().subtract(savingsTrxn.getDepositAmount()));
	}

	private void adjustForWithdrawal(AccountTrxnEntity accntTrxn) {
		SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accntTrxn;
		setSavingsBalance(getSavingsBalance().add(
				savingsTrxn.getWithdrawlAmount()));
		// getSavingsPerformance().setTotalWithdrawals(getSavingsPerformance().getTotalWithdrawals().subtract(savingsTrxn.getWithdrawlAmount()));
	}

	protected boolean isAdjustPossibleOnLastTrxn(Money amountAdjustedTo) {
		if (!(getAccountState().getId().equals(
				AccountStates.SAVINGS_ACC_APPROVED) || getAccountState()
				.getId().equals(AccountStates.SAVINGS_ACC_INACTIVE))) {
			logger
					.debug("State is not active hence adjustment is not possible");
			return false;
		}
		AccountPaymentEntity accountPayment = getLastPmnt();
		if (accountPayment != null
				&& getLastPmntAmnt() != 0
				&& (accountPayment.getActionType().equals(
						AccountConstants.ACTION_SAVINGS_WITHDRAWAL) || accountPayment
						.getActionType().equals(
								AccountConstants.ACTION_SAVINGS_DEPOSIT))) {
			if (accountPayment.getAmount().equals(amountAdjustedTo)) {
				logger
						.debug("Either Amount to be adjusted is same as last user payment amount, or last payment is not withdrawal or deposit, therefore adjustment is not possible.");
				return false;
			}
			return (adjustmentCheckForWithdrawal(accountPayment,
					amountAdjustedTo) && adjustmentCheckForBalance(
					accountPayment, amountAdjustedTo));
		}
		logger.debug("No last Payment found for adjustment");
		return false;
	}

	private boolean adjustmentCheckForWithdrawal(
			AccountPaymentEntity accountPayment, Money amountAdjustedTo) {
		Double maxWithdrawAmount = getSavingsOffering().getMaxAmntWithdrawl()
				.getAmountDoubleValue();
		if (accountPayment.getActionType().equals(
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL)
				&& maxWithdrawAmount != null
				&& maxWithdrawAmount != 0
				&& amountAdjustedTo.getAmountDoubleValue() > maxWithdrawAmount) {
			logger.debug("Amount is more than withdrawal limit");
			return false;
		}
		return true;
	}

	private boolean adjustmentCheckForBalance(
			AccountPaymentEntity accountPayment, Money amountAdjustedTo) {
		Money balanceAfterAdjust = getSavingsBalance();
		for (AccountTrxnEntity accntTrxn : accountPayment.getAccountTrxns()) {
			SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accntTrxn;
			if (accountPayment.getActionType().equals(
					AccountConstants.ACTION_SAVINGS_WITHDRAWAL)
					&& amountAdjustedTo.getAmountDoubleValue() > savingsTrxn
							.getWithdrawlAmount().getAmountDoubleValue()) {
				balanceAfterAdjust = balanceAfterAdjust
						.subtract(amountAdjustedTo.subtract(savingsTrxn
								.getWithdrawlAmount()));
				if (balanceAfterAdjust.getAmountDoubleValue() < 0) {
					logger
							.debug("After adjustment balance is becoming -ve, therefore adjustment is not possible.");
					return false;
				}
			}
		}
		return true;
	}

	public AccountNotesEntity createAccountNotes(String comment)
			throws ServiceException {
		AccountNotesEntity accountNotes = new AccountNotesEntity();
		accountNotes.setCommentDate(new java.sql.Date(System
				.currentTimeMillis()));
		accountNotes.setPersonnel(getPersonnelDBService().getPersonnel(
				userContext.getId()));
		accountNotes.setComment(comment);
		return accountNotes;
	}

	private boolean isPermissionAllowed(AccountStateEntity newSate,
			UserContext userContext, AccountStateFlagEntity flagSelected,
			boolean saveFlag) {
		short newSateId = newSate.getId().shortValue();
		short cancelFlag = flagSelected != null ? flagSelected.getId()
				.shortValue() : 0;
		if (saveFlag)
			return AuthorizationManager.getInstance().isActivityAllowed(
					userContext,
					new ActivityContext(ActivityMapper.getInstance()
							.getActivityIdForState(newSateId), this.getOffice()
							.getOfficeId(), this.getCustomer().getPersonnel()
							.getPersonnelId()));
		else
			return AuthorizationManager.getInstance().isActivityAllowed(
					userContext,
					new ActivityContext(ActivityMapper.getInstance()
							.getActivityIdForNewStateId(newSateId, cancelFlag),
							this.getOffice().getOfficeId(), this.getCustomer()
									.getPersonnel().getPersonnelId()));
	}

	private void checkStatusChangeAllowed(AccountStateEntity newState)
			throws ApplicationException {
		if (!(SavingsStateMachine.getInstance().isTransitionAllowed(this,
				newState))) {
			throw new StateChangeException(
					SavingsConstants.STATUS_CHANGE_NOT_ALLOWED);
		}

	}

	private void checkPermissionForStatusChange(AccountStateEntity newState,
			UserContext userContext, AccountStateFlagEntity flagSelected)
			throws SecurityException {
		if (!isPermissionAllowed(newState, userContext, flagSelected, false))
			throw new SecurityException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	private void checkPermissionForSave(AccountStateEntity newState,
			UserContext userContext, AccountStateFlagEntity flagSelected)
			throws SecurityException {
		if (!isPermissionAllowed(newState, userContext, flagSelected, true))
			throw new SecurityException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	public Money getOverDueDepositAmount(java.sql.Date meetingDate) {
		Money overdueAmount = new Money();
		if (isMandatory()) {
			for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {
				if (accountActionDate.getPaymentStatus().shortValue() == AccountConstants.PAYMENT_UNPAID
						&& (accountActionDate.getActionDate()
								.before(meetingDate))) {
					overdueAmount = overdueAmount.add(accountActionDate
							.getTotalDepositDue());
				}
			}
		}
		return overdueAmount;
	}

	public List<SavingsRecentActivityView> getRecentAccountActivity(
			Integer count) {
		List<SavingsRecentActivityView> accountActivityList = new ArrayList<SavingsRecentActivityView>();
		int activitiesAdded = 0;
		for (AccountPaymentEntity accountPaymentEntity : getAccountPayments()) {
			for (AccountTrxnEntity accountTrxnEntity : accountPaymentEntity
					.getAccountTrxns()) {
				if (count == null || activitiesAdded < count.intValue()) {
					accountActivityList
							.add(createSavingsRecentActivityView(accountTrxnEntity));
					activitiesAdded++;
				}
			}
		}
		return accountActivityList;
	}

	private SavingsRecentActivityView createSavingsRecentActivityView(
			AccountTrxnEntity accountTrxnEntity) {
		SavingsRecentActivityView savingsRecentActivityView = new SavingsRecentActivityView();
		savingsRecentActivityView.setAccountTrxnId(accountTrxnEntity
				.getAccountTrxnId());
		savingsRecentActivityView.setActionDate(accountTrxnEntity
				.getActionDate());
		savingsRecentActivityView.setAmount(removeSign(
				accountTrxnEntity.getAmount()).toString());
		savingsRecentActivityView.setActivity(accountTrxnEntity
				.getAccountActionEntity().getName(userContext.getLocaleId()));
		savingsRecentActivityView.setRunningBalance(String
				.valueOf(((SavingsTrxnDetailEntity) accountTrxnEntity)
						.getBalance().getAmountDoubleValue()));
		return savingsRecentActivityView;
	}

	private Money removeSign(Money amount) {
		return (amount.getAmountDoubleValue() < 0) ? amount.negate() : amount;
	}

	public Money getTotalAmountInArrears() {
		Money totalAmount = new Money();
		List<AccountActionDateEntity> previousDueInstallments = getDetailsOfInstallmentsInArrears();
		if (previousDueInstallments != null
				&& previousDueInstallments.size() > 0)
			for (AccountActionDateEntity accountAction : previousDueInstallments)
				totalAmount = totalAmount.add(accountAction
						.getTotalDepositDue());
		return totalAmount;
	}

	public Money getTotalAmountDue() {
		return getTotalAmountInArrears().add(
				getTotalAmountDueForNextInstallment());
	}

	public Money getTotalAmountDueForInstallment(Short installmentId) {
		Money totalAmount = new Money();
		if (null != getAccountActionDates()
				&& getAccountActionDates().size() > 0) {
			for (AccountActionDateEntity accntActionDate : getAccountActionDates()) {
				if (accntActionDate.getInstallmentId().equals(installmentId)
						&& accntActionDate.getPaymentStatus().equals(
								AccountConstants.PAYMENT_UNPAID)) {
					totalAmount = totalAmount.add(accntActionDate
							.getTotalDepositDue());
				}
			}
		}

		return totalAmount;
	}

	public Money getTotalAmountDueForNextInstallment() {
		AccountActionDateEntity nextAccountAction = getDetailsOfNextInstallment();
		if (nextAccountAction != null)
			return getTotalAmountDueForInstallment(nextAccountAction
					.getInstallmentId());
		return new Money();
	}

	private List<AccountActionDateEntity> getNextInstallment() {
		List<AccountActionDateEntity> nextInstallment = new ArrayList<AccountActionDateEntity>();
		AccountActionDateEntity nextAccountAction = getDetailsOfNextInstallment();
		if (nextAccountAction != null && null != getAccountActionDates()
				&& getAccountActionDates().size() > 0) {
			for (AccountActionDateEntity accntActionDate : getAccountActionDates())
				if (accntActionDate.getInstallmentId().equals(
						nextAccountAction.getInstallmentId())
						&& accntActionDate.getPaymentStatus().equals(
								AccountConstants.PAYMENT_UNPAID))
					nextInstallment.add(accntActionDate);
		}
		return nextInstallment;
	}

	public void waiveAmountDue() throws ServiceException, AccountException {
		addSavingsActivityDetails(buildSavingsActivityForWaive(
				getTotalAmountDueForNextInstallment(), getSavingsBalance(),
				AccountConstants.ACTION_WAIVEOFFDUE));
		List<AccountActionDateEntity> nextInstallments = getNextInstallment();
		for (AccountActionDateEntity accountActionDate : nextInstallments) {
			accountActionDate.waiveDepositDue();
		}

		try {
			getDBService().save(this);
		} catch (PersistenceException pe) {
			throw new AccountException("errors.update", pe);
		}
	}

	public void waiveAmountOverDue() throws ServiceException, AccountException {
		addSavingsActivityDetails(buildSavingsActivityForWaive(
				getTotalAmountInArrears(), getSavingsBalance(),
				AccountConstants.ACTION_WAIVEOFFOVERDUE));
		List<AccountActionDateEntity> installmentsInArrears = getDetailsOfInstallmentsInArrears();
		for (AccountActionDateEntity accountActionDate : installmentsInArrears) {
			accountActionDate.waiveDepositDue();
		}
		try {
			getDBService().save(this);
		} catch (PersistenceException pe) {
			throw new AccountException("errors.update", pe);
		}
	}

	private SavingsActivityEntity buildSavingsActivityForWaive(Money amount,
			Money balanceAmount, short acccountActionId)
			throws ServiceException {
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);
		AccountActionEntity accountAction = (AccountActionEntity) masterPersistenceService
				.findById(AccountActionEntity.class, acccountActionId);
		PersonnelBO personnel = new PersonnelPersistenceService()
				.getPersonnel(getUserContext().getId());
		return new SavingsActivityEntity(personnel, accountAction, amount,
				balanceAmount);
	}

}
