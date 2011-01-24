package org.mifos.platform.accounting.tally;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.service.AccountingDataCacheManager;
import org.springframework.core.io.ClassPathResource;

public class TallyXMLOutputTest {
    AccountingDataCacheManager cacheManager;

    @Before
    public void setUp() throws Exception {
        cacheManager = new AccountingDataCacheManager();
    }

    @Test
    public void testAccoutingDataOutPut() throws Exception {
        String fileName = "Mifos Accounting Export 2010-08-10 to 2010-08-10.xml";
        File file = new ClassPathResource("org/mifos/platform/accounting/tally/2010-08-10 to 2010-08-10").getFile();
        String expected = FileUtils.readFileToString(new ClassPathResource("org/mifos/platform/accounting/tally/"+ fileName).getFile());
        List<AccountingDto> accountingData = cacheManager.accountingDataFromCache(file);
        String tallyOutput = TallyXMLGenerator.getTallyXML(accountingData, fileName);
        Assert.assertEquals(expected, tallyOutput);
    }

}