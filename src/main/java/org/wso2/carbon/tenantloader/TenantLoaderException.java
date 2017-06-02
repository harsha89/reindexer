/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.tenantloader;

/**
 * This class handles tenant loader related exceptions.
 */
public class TenantLoaderException extends Exception {

    /**
     * Constructor method to handle tenant loader related exceptions without message and throwable course.
     */
    public TenantLoaderException() {
        super();
    }

    /**
     * Constructor method to handle tenant loader related exceptions only with message.
     *
     * @param message   error message.
     */
    public TenantLoaderException(String message) {
        super(message);
    }

    /**
     * Constructor method to handle tenant loader related exceptions only with throwable course.
     *
     * @param cause     throwable cause.
     */
    public TenantLoaderException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor method to handle tenant loader related exceptions with message and throwable course.
     *
     * @param message   error message.
     * @param cause     throwable cause.
     */
    public TenantLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

}
