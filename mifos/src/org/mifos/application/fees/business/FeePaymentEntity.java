/**

 * FeePayment.java    version: xxx



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

package org.mifos.application.fees.business;

import org.mifos.application.master.util.valueobjects.LookUpEntity;
import org.mifos.application.master.util.valueobjects.LookUpValueLocale;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.valueobjects.ValueObject;
import java.util.*;

/**
 * @author ashishsm
 *
 */
/**
 * A class that represents a row in the 'fee_payment' table.
 * This class may be customized as it is never re-generated
 * after being created.
 */
public class FeePaymentEntity extends PersistentObject {
	

	private Short feePaymentId;

	private Integer lookUpId;

	private Set<LookUpValueLocale> lookUpValueLocale;

	public FeePaymentEntity() {
	}

	public Short getFeePaymentId() {
		return feePaymentId;
	}

	
	public void setFeePaymentId(Short feePaymentId) {

		this.feePaymentId = feePaymentId;
	}

	public void setLookUpValueLocale(Set<LookUpValueLocale> lookUpValueLocale) {

		this.lookUpValueLocale = lookUpValueLocale;
	}

	public Set<LookUpValueLocale> getLookUpValueLocale() {
		return lookUpValueLocale;

	}

	public Integer getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}

}
