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

package org.wso2.carbon.tenantloader.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.tenantloader.TenantLoaderException;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * This class holds the methods to decrypt and encrypt data in tenants due to a key store change
 * Used in KeyStoreChangeServiceComponent.
 */
public class TenantLoader {

    /**
     * Variable used to log entries.
     */
    private static final Log log = LogFactory.getLog(TenantLoader.class);

    /**
     * Document builder used to get keyChange.xml data.
     */
    private static final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

    /**
     * This method is used by the service activator method to decrypt data using old key store and encrypt data using
     * new key store.
     *
     * @throws TenantLoaderException  Throws when validation error occurred while getting tenant or registry data.
     */
    public void loadTenants() throws TenantLoaderException {

            // Decrypt and encrypt resource properties using new key store.
            loadAllTenatsRegistry();

    }

    /**
     * This method is used to decrypt and encrypt tenants resource properties.
     *
     * @throws TenantLoaderException  Throws when:
*                                   <ul>
*                                       <li>KeyChangeExceptions thrown in statTenantFlow, decryptAndEncryptProperties
*                                       methods.</li>
*                                       <li>If an error occurs while getting registry from service holder
*                                       getRegistryService() method.</li>
*                                   </ul>
     */
    private void loadAllTenatsRegistry() throws TenantLoaderException {
        Tenant[] tenantList = getTenantList();
        // Iterate through tenants
        for (Tenant tenant : tenantList) {
            String tenantDomain = tenant.getDomain();
            log.info("LOADING TENANT REGISTRY FOR THE TENANT: " + tenantDomain );
            String tenantAdminUsername = tenant.getAdminName();
            try {
                // Start a new tenant flow for a tenant
                int tenantId = statTenantFlow(tenantDomain, tenantAdminUsername);

                // Load tenants registry
                ServiceHolder.getTenantRegLoader().loadTenantRegistry(tenantId);
                ServiceHolder.getTenantIndexLoader().loadTenantIndex(tenantId);
            } catch (RegistryException e) {
                throw new TenantLoaderException("Error while getting registry from service holder getRegistryService() "
                        + "method.", e);
            } finally {
                PrivilegedCarbonContext.endTenantFlow();
            }
        }
    }




    /**
     * This method is used to get tenant list for decrypting and encrypting data.
     *
     * @return tenantList           tenant list from xml file of separation by key store changed data.
     * @throws Tena ntLoaderException   Throws when exceptions thrown in getKeyChangeMethod, getTenantsByKeyChangeDate,
     *                              getTenantsByKeyChangeXml methods.
     */
    private Tenant[] getTenantList() throws TenantLoaderException {
        try {
            return ServiceHolder.getRealmService().getTenantManager().getAllTenants();
        } catch (UserStoreException e) {
            e.printStackTrace();
        }
        return new Tenant[0];
    }

    /**
     * This method is used to tenant flow.
     *
     * @param tenantDomain          tenant domain needed to start the tenant flow.
     * @param tenantAdminUsername   tenant admin username needed to start the tenant flow.
     * @return                      tenant flow started tenant's tenant id.
     * @throws TenantLoaderException   Throws when a user store exception occurs while getting the tenant ID for tenant.
     */
    private int statTenantFlow(String tenantDomain, String tenantAdminUsername) throws TenantLoaderException {
        // Start a new tenant flow for a tenant
        log.info("Tenant '" + tenantDomain + "' update started.");
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain);
        int tenantId;
        try {
            tenantId = ServiceHolder.getRealmService().getTenantManager().getTenantId(tenantDomain);
        } catch (UserStoreException e) {
            throw new TenantLoaderException("User store exception while getting the tenant ID for tenant " +
                    tenantDomain, e);
        }
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(tenantId);
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(tenantAdminUsername);
        return tenantId;
    }
}
