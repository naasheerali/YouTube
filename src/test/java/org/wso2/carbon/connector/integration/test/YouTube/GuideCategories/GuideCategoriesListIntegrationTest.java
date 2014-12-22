/**
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.integration.test.YouTube;

import org.apache.axis2.context.ConfigurationContext;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;

import javax.activation.DataHandler;
import java.lang.System;
import java.net.URL;
import java.util.Properties;

public class GuideCategoriesListIntegrationTest extends ESBIntegrationTest {

    private static final String CONNECTOR_NAME = "YouTube";

    private MediationLibraryUploaderStub mediationLibUploadStub = null;

    private MediationLibraryAdminServiceStub adminServiceStub = null;

    private ProxyServiceAdminClient proxyAdmin;

    private String repoLocation = null;

    private String YouTubeConnectorFileName = CONNECTOR_NAME + ".zip";

    private Properties YouTubeConnectorProperties = null;

    private String pathToProxiesDirectory = null;

    private String pathToRequestsDirectory = null;

    private String myGroupId = null;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();

        ConfigurationContextProvider configurationContextProvider = ConfigurationContextProvider.getInstance();
        ConfigurationContext cc = configurationContextProvider.getConfigurationContext();
        mediationLibUploadStub = new MediationLibraryUploaderStub(cc, esbServer.getBackEndUrl() + "MediationLibraryUploader");
        AuthenticateStub.authenticateStub("admin", "admin", mediationLibUploadStub);
        adminServiceStub = new MediationLibraryAdminServiceStub(cc, esbServer.getBackEndUrl() + "MediationLibraryAdminService");

        AuthenticateStub.authenticateStub("admin", "admin", adminServiceStub);

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            repoLocation = System.getProperty("connector_repo").replace("/", "\\");
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
        }
        proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(), esbServer.getSessionCookie());

        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, YouTubeConnectorFileName);
        log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(30000);

        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
                "org.wso2.carbon.connector", "enabled");

        YouTubeConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);

        pathToProxiesDirectory = repoLocation + YouTubeConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + YouTubeConnectorProperties.getProperty("requestDirectoryRelativePath");

    }

    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }



          /*Test GuideCategoriesList With Mandatory Parameters snippet*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{GuideCategoriesListPartSnippetAndregionCode} integration test.")
    public void testGuideCategoriesListWithMandatoryParametersSnippetregionCode() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "GuideCategories/GuideCategoriesListSnippetMine.txt";
        String methodName = "GuideCategoriesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "GuideCategories/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListPart"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListregionCode"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#guideCategoryListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test GuideCategoriesList With invalid Mandatory Parameters Invalid regionCode*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{GuideCategoriesListPartInvalidSnippetAndregionCode} integration test.")
    public void testGuideCategoriesListWithInvalidIdMandatoryParametersSnippeteAndregionCode() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "GuideCategories/GuideCategoriesListSnippetMine.txt";
        String methodName = "GuideCategoriesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "GuideCategories/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListInvalidPart"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListregionCode"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


            /*Test GuideCategoriesList With Mandatory Parameters regionCode*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{GuideCategoriesListPartSnippetAndregionCode} integration test.")
    public void testGuideCategoriesListWithMandatoryParametersSnippetregionCode() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "GuideCategories/GuideCategoriesListSnippetMine.txt";
        String methodName = "GuideCategoriesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "GuideCategories/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListPart"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListregionCode"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#guideCategoryListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test GuideCategoriesList With invalid Mandatory Parameters Invalid regionCode*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{GuideCategoriesListPartInvalidSnippetAndregionCode} integration test.")
    public void testGuideCategoriesListWithInvalidIdMandatoryParametersSnippeteAndregionCode() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "GuideCategories/GuideCategoriesListSnippetMine.txt";
        String methodName = "GuideCategoriesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "GuideCategories/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListPart"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListInvalidregionCode"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }



         /*Test GuideCategoriesList With Mandatory Parameters id*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{GuideCategoriesListPartSnippetAndid} integration test.")
    public void testGuideCategoriesListWithMandatoryParametersSnippetid() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "GuideCategories/GuideCategoriesListSnippetid.txt";
        String methodName = "GuideCategoriesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "GuideCategories/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListPart"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListid"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#guideCategoryListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test GuideCategoriesList With invalid Mandatory Parameters Invalid id*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{GuideCategoriesListPartInvalidSnippetAndid} integration test.")
    public void testGuideCategoriesListWithInvalidIdMandatoryParametersSnippeteAndid() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "GuideCategories/GuideCategoriesListSnippetid.txt";
        String methodName = "GuideCategoriesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "GuideCategories/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListInvalidPart"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListInvalidid"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }




         /*Test GuideCategoriesList With Optional Parameters hl*/

    @Test(groups = {"wso2.esb"}, description = "YouTube{GuideCategoriesListPartSnippetAndhl} integration test.")
    public void testGuideCategoriesListWithOptionalParametersSnippethl() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "GuideCategories/GuideCategoriesListSnippethl.txt";
        String methodName = "GuideCategoriesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "GuideCategories/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListPart"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListhl"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListregionCode"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#guideCategoryListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test GuideCategoriesList With invalid Optional Parameters Invalid hl*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{GuideCategoriesListPartInvalidSnippetAndhl} integration test.")
    public void testGuideCategoriesListWithInvalidIdOptionalParametersSnippeteAndhl() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "GuideCategories/GuideCategoriesListSnippethl.txt";
        String methodName = "GuideCategoriesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "GuideCategories/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListInvalidPart"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListInvalidhl"),
                YouTubeConnectorProperties.getProperty("GuideCategoriesListInvalidid"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }









*/






































}
