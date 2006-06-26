/**

 * Feelevel.java    version: xxx



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

import org.mifos.framework.business.PersistentObject;

/**
 * @author Krishankg
 * 
 */
public class FeeLevelEntity extends PersistentObject {

	private FeesBO fee;

	private Short levelId;

	private Short feeLevelId;

	public FeeLevelEntity() {
	}

	public void setFeeLevelId(Short feeLevelId) {
		this.feeLevelId = feeLevelId;
	}

	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}

	public Short getFeeLevelId() {
		return feeLevelId;
	}

	public Short getLevelId() {
		return levelId;
	}

	public FeesBO getFee() {
		return fee;
	}

	public void setFee(FeesBO fee) {
		this.fee = fee;
	}

}
