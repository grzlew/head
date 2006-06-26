/**

 * CategoryType.java    version: xxx



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

package org.mifos.application.fees.util.valueobjects;

import org.mifos.application.master.util.valueobjects.LookUpEntity;
import org.mifos.framework.util.valueobjects.ValueObject;
import java.util.Set;

/**
 * @author ashishsm
 *
 */

public class CategoryType extends ValueObject {

	public CategoryType() {
	}

	private Short categoryId;

	private Integer lookUpId;

	private Set lookUpValueLocale;

	public Short getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Short categoryId) {

		this.categoryId = categoryId;
	}

	public void setLookUpValueLocale(Set lookUpValueLocale) {

		this.lookUpValueLocale = lookUpValueLocale;
	}

	public Set getLookUpValueLocale() {
		return lookUpValueLocale;

	}

	public Integer getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}

}
