/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Days;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.GeneralConfig;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.internal.HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration;

public class UpdateCustomerFeesHelper extends TaskHelper {

    private final HolidayDao holidayDao = DependencyInjectedServiceLocator.locateHolidayDao();
    private final AccountPersistence accountPersistence = new AccountPersistence();

    private List<Days> workingDays;
    private Map<Short, List<Holiday>> officeCurrentAndFutureHolidays;

    public UpdateCustomerFeesHelper(final MifosTask mifosTask) {
        super(mifosTask);
    }

    @Override
    public void execute(@SuppressWarnings("unused") final long timeInMillis) throws BatchJobException {

        workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        officeCurrentAndFutureHolidays = new HashMap<Short, List<Holiday>>();

        long taskStartTime = new DateTimeService().getCurrentDateTime().getMillis();

        List<Integer> customerAccountIds;

        customerAccountIds = findAccountIdsToFix();


        int accountCount = customerAccountIds.size();
        if (accountCount == 0) {
            return;
        }

        List<String> errorList = new ArrayList<String>();
        int currentRecordNumber = 0;
        int outputIntervalForBatchJobs = GeneralConfig.getOutputIntervalForBatchJobs();
        int batchSize = GeneralConfig.getBatchSizeForBatchJobs();
        // jpw - hardcoded recordCommittingSize to 500 because now only accounts that need more schedules are returned
        int recordCommittingSize = 500;

        infoLogBatchParameters(accountCount, outputIntervalForBatchJobs, batchSize, recordCommittingSize);

        long startTime = new DateTimeService().getCurrentDateTime().getMillis();
        Integer currentAccountId = null;
        int updatedRecordCount = 0;

        try {
            StaticHibernateUtil.getSessionTL();
            StaticHibernateUtil.startTransaction();

            for (Integer accountId : customerAccountIds) {
                currentRecordNumber++;
                currentAccountId = accountId;
                AccountBO accountBO = accountPersistence.getAccount(accountId);

                if (accountBO instanceof CustomerAccountBO) {
//                    ((CustomerAccountBO) accountBO).generateNextSetOfMeetingDates(scheduleGenerationStrategy);
                    ((CustomerAccountBO) accountBO).applyPeriodicFeesToNextSetOfMeetingDates();
                    updatedRecordCount++;
                }

                if (currentRecordNumber % batchSize == 0) {
                    StaticHibernateUtil.flushAndClearSession();
                    getLogger().debug("completed HibernateUtil.flushAndClearSession()");
                }
                if (updatedRecordCount > 0) {
                    if (updatedRecordCount % recordCommittingSize == 0) {
                        StaticHibernateUtil.commitTransaction();
                        StaticHibernateUtil.getSessionTL();
                        StaticHibernateUtil.startTransaction();
                    }
                }

                if (currentRecordNumber % outputIntervalForBatchJobs == 0) {
                    long time = System.currentTimeMillis();
                    String message = "" + currentRecordNumber + " processed, " + (accountCount - currentRecordNumber)
                            + " remaining, " + updatedRecordCount + " updated, batch time: " + (time - startTime)
                            + " ms";
                    logMessage(message);
                    startTime = time;
                }
            }
            StaticHibernateUtil.commitTransaction();
            long time = System.currentTimeMillis();
            String message = "" + currentRecordNumber + " processed, " + (accountCount - currentRecordNumber)
                    + " remaining, " + updatedRecordCount + " updated, batch time: " + (time - startTime) + " ms";
            logMessage(message);

        } catch (Exception e) {
            logMessage("account " + currentAccountId.intValue() + " exception " + e.getMessage());
            StaticHibernateUtil.rollbackTransaction();
            errorList.add(currentAccountId.toString());
            getLogger().error("Unable to generate schedules for account with ID " + currentAccountId, e);
        } finally {
            StaticHibernateUtil.closeSession();
        }

        if (errorList.size() > 0) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }

        logMessage("UpdateCustomerFeesHelper ran in "
                + (new DateTimeService().getCurrentDateTime().getMillis() - taskStartTime));

    }

    private void infoLogBatchParameters(int accountCount, int outputIntervalForBatchJobs, int batchSize,
            int recordCommittingSize) {
        logMessage("Using parameters:" + "\n  OutputIntervalForBatchJobs: " + outputIntervalForBatchJobs
                + "\n  BatchSizeForBatchJobs: " + batchSize + "\n  RecordCommittingSizeForBatchJobs: "
                + recordCommittingSize);
        String initial_message = "" + accountCount + " accounts to process, results output every "
                + outputIntervalForBatchJobs + " accounts";
        logMessage(initial_message);
    }

    @SuppressWarnings("unchecked")
    private List<Integer> findAccountIdsToFix() throws BatchJobException {
        List<Integer> AccountIds = new ArrayList<Integer>();
        long time1 = new DateTimeService().getCurrentDateTime().getMillis();
        //AccountIds.add(68256);

        Map<String, Object> parameters = new HashMap<String, Object>();
        try {
            AccountIds =  new AccountPersistence().executeNamedQuery("getCustomerAccountsWithSchedulesMissingPeriodicFees", parameters);
        } catch (PersistenceException e) {
            throw new BatchJobException(e);
        }

        long duration = new DateTimeService().getCurrentDateTime().getMillis() - time1;
        logMessage("Time to execute the query " + duration + " . Got " + AccountIds.size()
                + " accounts.");

        return AccountIds;
    }


    private void logMessage(String finalMessage) {
        System.out.println(finalMessage);
        getLogger().info(finalMessage);
    }

    @Override
    public boolean isTaskAllowedToRun() {
        return true;
    }

    @Override
    public void requiresExclusiveAccess() {
        MifosTask.batchJobRequiresExclusiveAccess(false);
    }
}
