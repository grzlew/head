/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.customers.client.business;

import java.sql.Blob;

import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.business.AbstractEntity;


/**
 * replaced with {@link org.mifos.framework.image.service.ClientPhotoService}
 *<br><br>
 * This entity is not used and should be deleted as migration of Mifos 2.3
 * @deprecated
 */
@Deprecated
public class CustomerPictureEntity extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    private Integer pictureId;

    private final CustomerBO customer;

    private Blob picture;

    public CustomerPictureEntity(CustomerBO customer) {
        super();
        this.customer = customer;
    }

    public CustomerBO getCustomer() {
        return customer;
    }

    public Blob getPicture() {
        return picture;
    }

    public void setPicture(Blob picture) {
        this.picture = picture;
    }

    public Integer getPictureId() {
        return pictureId;
    }
}
