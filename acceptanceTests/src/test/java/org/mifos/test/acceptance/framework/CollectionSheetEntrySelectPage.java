/**
 * 
 */
package org.mifos.test.acceptance.framework;


import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

/**
 * @author keith
 *
 */
public class CollectionSheetEntrySelectPage extends AbstractPage {

	private static String RECEIPT_INPUT_ID	= "bulkentry.input.receiptId";
	private static String CONTINUE_BUTTON_ID = "bulkentry.button.continue";
	private static String CANCEL_BUTTON_ID = "bulkentry.button.cancel";

	public CollectionSheetEntrySelectPage() {
		super();
	}

	public CollectionSheetEntrySelectPage(Selenium selenium) {
		super(selenium);
	}

	public void verifyPage() {
		Assert.assertTrue(selenium.isTextPresent(" Bulk entry - Select Center"), "Didn't reach Bulk entry select page");
	}

	public HomePage cancelPage() {
		verifyPage();
		selenium.click(CANCEL_BUTTON_ID);
		waitForPageToLoad();
		return new HomePage(selenium);
	}
	
	public CollectionSheetEntryEnterDataPage submitForm(
						String branch, String loanOfficer, String center,
						String transactionDay, String transactionMonth, String transactionYear, 
						String paymentMode, String receiptId, 
						String receiptDay, String receiptMonth, String receiptYear) {
		
		selenium.select("officeId",          "label=" + branch);
		waitForPageToLoad();
		selenium.select("loanOfficerId",     "label="+ loanOfficer);
		waitForPageToLoad();
		selenium.select("customerId",        "label="+ center);
		waitForPageToLoad();
		typeTextIfNotEmpty  ("transactionDateDD", transactionDay);
		typeTextIfNotEmpty  ("transactionDateMM", transactionMonth);
		typeTextIfNotEmpty  ("transactionDateYY", transactionYear);
		selenium.select("paymentId",         "label=" + paymentMode);
		typeTextIfNotEmpty  (RECEIPT_INPUT_ID,         "123456789");
		typeTextIfNotEmpty  ("receiptDateDD",     receiptDay);
		typeTextIfNotEmpty  ("receiptDateMM",     receiptMonth);
		typeTextIfNotEmpty  ("receiptDateYY",     receiptYear);
		selenium.click (CONTINUE_BUTTON_ID);
		waitForPageToLoad();
		return new CollectionSheetEntryEnterDataPage(selenium);
	}
	
	private void typeTextIfNotEmpty(String locator, String value) {
		if (value!= null && !value.isEmpty()) {
			selenium.type(locator, value);
		}
	}
}
