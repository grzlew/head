/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.collectionsheet;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.collectionsheet.business.CollSheetBOIntegrationTest;
import org.mifos.application.collectionsheet.business.CollSheetCustBOIntegrationTest;
import org.mifos.application.collectionsheet.business.CollSheetSavingsDetailsEntityIntegrationTest;
import org.mifos.application.collectionsheet.business.service.CollectionSheetEntryBusinessServiceIntegrationTest;
import org.mifos.application.collectionsheet.persistence.BulkEntryPersistenceIntegrationTest;
import org.mifos.application.collectionsheet.persistence.service.BulkEntryPersistenceServiceIntegrationTest;
import org.mifos.application.collectionsheet.struts.action.BulkEntryActionIntegrationTest;
import org.mifos.application.collectionsheet.struts.uihelpers.BulkEntryDisplayHelperIntegrationTest;

public class CollectionSheetTestSuite extends TestSuite {

    public static void main(String[] args) {
        Test testSuite = suite();

        TestRunner.run(testSuite);
    }

    public static Test suite() {
        TestSuite testSuite = new CollectionSheetTestSuite();
        testSuite.addTestSuite(CollSheetCustBOIntegrationTest.class);
        testSuite.addTestSuite(CollSheetBOIntegrationTest.class);
        testSuite.addTestSuite(CollSheetSavingsDetailsEntityIntegrationTest.class);

        testSuite.addTestSuite(BulkEntryPersistenceIntegrationTest.class);
        testSuite.addTestSuite(BulkEntryPersistenceServiceIntegrationTest.class);
        testSuite.addTestSuite(CollectionSheetEntryBusinessServiceIntegrationTest.class);
        testSuite.addTestSuite(BulkEntryActionIntegrationTest.class);
        testSuite.addTestSuite(BulkEntryDisplayHelperIntegrationTest.class);

        return testSuite;

    }
}
