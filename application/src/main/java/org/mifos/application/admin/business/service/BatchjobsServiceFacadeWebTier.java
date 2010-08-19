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

package org.mifos.application.admin.business.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.mifos.application.admin.servicefacade.BatchjobsDto;
import org.mifos.application.admin.servicefacade.BatchjobsServiceFacade;
import org.mifos.framework.components.batchjobs.MifosScheduler;
import org.mifos.framework.components.batchjobs.MifosTask;

public class BatchjobsServiceFacadeWebTier implements BatchjobsServiceFacade{

    @Override
    public List<BatchjobsDto> getBatchjobs(ServletContext context) {
        List<BatchjobsDto> batchjobs = new ArrayList<BatchjobsDto>();
        MifosScheduler mifosScheduler = (MifosScheduler) context.getAttribute(MifosScheduler.class.getName());
        for (MifosTask mifosTask : mifosScheduler.getTasks()) {
            batchjobs.add(new BatchjobsDto(mifosTask.name));
        }
        return batchjobs;
    }

}
