/**
 
 * SavingsHelper.java    version: xxx
 
 
 
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
package org.mifos.application.accounts.savings.util.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.interestcalculator.InterestInputs;
import org.mifos.framework.components.scheduler.MonthData;
import org.mifos.framework.components.scheduler.ScheduleDataIntf;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerFactory;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;

public class SavingsHelper {
	private Calendar cal;
	//TODO: pick from configuration
	private Date getFiscalStartDate(){
		try{
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			return df.parse("01/01/2006");
		}catch(ParseException pe){			
		}
		return null;
	}
	
	private int getFiscalStartDayNumber(){
		Calendar cal = getCalendar();
		cal.setTime(getFiscalStartDate());
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	public SavingsHelper(){
		cal=Calendar.getInstance();
		cal.setTime(getFiscalStartDate());
	}
	
	public Date getNextScheduleDate(Date accountActivationDate, Date currentScheduleDate, MeetingBO meeting)throws SchedulerException{
		SchedulerIntf scheduler = getScheduler(meeting);
		return (currentScheduleDate==null)? getFirstDate(scheduler, accountActivationDate) : scheduler.getNextScheduleDateAfterRecurrence(currentScheduleDate);
	}
	
	public Date getPrevScheduleDate(Date currentScheduleDate, MeetingBO meeting)throws SchedulerException{
		SchedulerIntf scheduler = getScheduler(meeting);
		return scheduler.getPrevScheduleDateAfterRecurrence(currentScheduleDate);
	}
	
	private Date getFirstDate(SchedulerIntf scheduler, Date accountActivationDate)throws SchedulerException{
		Date date = null;
		for(date = scheduler.getNextScheduleDateAfterRecurrence(getFiscalStartDate());
			date.compareTo(accountActivationDate)<=0;
			date = scheduler.getNextScheduleDateAfterRecurrence(date));
		return date;
	}
	
	
	private SchedulerIntf getScheduler(MeetingBO meeting)throws SchedulerException{
		Calendar cal = getCalendar();
		cal.setTime(getFiscalStartDate());
		meeting.setMeetingStartDate(cal);
		Short recurrenceId = meeting.getMeetingDetails().getRecurrenceType().getRecurrenceId();
		ScheduleDataIntf scheduleData;
		scheduleData=SchedulerFactory.getScheduleData(recurrenceId);
		if(scheduleData instanceof MonthData){
			if(meeting.getMeetingType().getMeetingTypeId().equals(MeetingConstants.INTEREST_POST_FREQ))
				scheduleData.setDayNumber(31);
			else if(meeting.getMeetingType().getMeetingTypeId().equals(MeetingConstants.INTEREST_CALC_FREQ))			
				scheduleData.setDayNumber(getFiscalStartDayNumber());
		}
		return SchedulerHelper.getScheduler(scheduleData , meeting);		
	}
	
	public int calculateDays(Date fromDate, Date toDate){
		long y = 1000 * 60 * 60 * 24;
		long x = (getMFITime(toDate)/y)-(getMFITime(fromDate)/y);
		return (int)x;
	}
	
	private long getMFITime(Date date){
		Calendar cal1 = Calendar.getInstance(Configuration.getInstance().getSystemConfig().getMifosTimeZone());
		cal1.setTime(date);
		return date.getTime()+cal1.get(Calendar.ZONE_OFFSET)+cal1.get(Calendar.DST_OFFSET);
	}
	
	private Calendar getCalendar(){
		return Calendar.getInstance(Configuration.getInstance().getSystemConfig().getMifosTimeZone());
	}
	
	public Date getCurrentDate(){
		cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
		return cal.getTime();
	}
	
	public InterestInputs createInterestInputs(Money principal,  double interestRate, int duration, String durationType){
		InterestInputs interestInputs = new InterestInputs();
		interestInputs.setPrincipal(principal);
		interestInputs.setDuration(duration);
		interestInputs.setDurationType(durationType);
		interestInputs.setInterestRate(interestRate);
		return interestInputs;
	}
	
	public AccountActionDateEntity createActionDateObject(CustomerBO customer, Date date, Short userId, Money amount){
		AccountActionDateEntity actionDate = new AccountActionDateEntity();
		actionDate.setActionDate(new java.sql.Date(date.getTime()));
		actionDate.setCreatedBy(userId);
		actionDate.setCreatedDate(new Date());
		actionDate.setCustomer(customer);
		actionDate.setDeposit(amount);
		actionDate.setPaymentStatus(new Short("0"));
		actionDate.setPrincipal(new Money());
		actionDate.setInterest(new Money());
		actionDate.setPenalty(new Money());
		return actionDate;
	}
	
	public AccountTrxnEntity createAccountPaymentTrxn(AccountPaymentEntity payment, Money balance, Short accountAction, CustomerBO customer, PersonnelBO createdBy)throws SystemException{
		SavingsTrxnDetailEntity savingsTrxn = new SavingsTrxnDetailEntity();
		savingsTrxn.setTrxnDetails(accountAction, payment.getAmount(), balance, customer, createdBy);
		return savingsTrxn;
	}
	
	public AccountPaymentEntity createAccountPayment(Money amount, PaymentTypeEntity paymentTypeEntity, PersonnelBO createdBy ){
		AccountPaymentEntity  payment = new AccountPaymentEntity();
		if(createdBy!=null)
			payment.setCreatedBy(createdBy.getPersonnelId());
		payment.setCreatedDate(getCurrentDate());
		payment.setPaymentDate(payment.getCreatedDate());
		payment.setAmount(amount);
		payment.setPaymentType(paymentTypeEntity);
		return payment;
	}
}
