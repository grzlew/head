/**

 * OfficeSearch.java    version: 1.0

 

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

package org.mifos.framework.security.util;

import org.mifos.framework.util.valueobjects.ValueObject;

public class OfficeSearch extends ValueObject{
	/***************************** Fields **********************/
	/**
	 * This would hold the officeid
	 */
    private	Short officeId;
	/**
	 * This would hold the searchid
	 */
    private	String searchId;
	
    private Short parentOfficeId;
	
	/**
	 *  Default constructor
	 */
	public OfficeSearch() {
				
	}


	/**
	 * This Function returns the officeId
	 * @return Returns the officeId.
	 */
	public Short getOfficeId() {
		return officeId;
	}


	/**
	 * @param officeId
	 * @param searchId
	 */
	public OfficeSearch(Short officeId, String searchId) {
		this.officeId = officeId;
		this.searchId = searchId;
	}

	public OfficeSearch(Short officeId, String searchId, Short parentOfficeId) {
		this.officeId = officeId;
		this.searchId = searchId;
		this.parentOfficeId = parentOfficeId;
	}

	/**
	 * This function set the officeId
	 * @param officeId The officeId to set.
	 */
	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}


	/**
	 * This Function returns the searchId
	 * @return Returns the searchId.
	 */
	public String getSearchId() {
		return searchId;
	}


	/**
	 * This function set the searchId
	 * @param searchId The searchId to set.
	 */
	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public Short getParentOfficeId() {
		return parentOfficeId;
	}

	public void setParentOfficeId(Short parentOfficeId) {
		this.parentOfficeId = parentOfficeId;
	}
}
