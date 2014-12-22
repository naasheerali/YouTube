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

public class ChannelBannersIntegrationTest extends ESBIntegrationTest {

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



          /*Test ActivitiesList With Mandatory Parameters id*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListid} integration test.")
    public void testActivitiesListWithMandatoryParametersPartId() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmine.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#activityListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ActivitiesList With invalid Mandatory Parameters id*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidid} integration test.")
    public void testActivitiesListWithInvalidIdMandatoryParametersPartId() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmine.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invalidpartid"),
                YouTubeConnectorProperties.getProperty("mine"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


              /*Test ActivitiesList With Mandatory Parameters snippet*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListSnippet} integration test.")
    public void testActivitiesListWithMandatoryParametersPartSnippet() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmine.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partsnippet"),
                YouTubeConnectorProperties.getProperty("mine"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#activityListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

                  /*Test ActivitiesList With Invalid Mandatory Parameters snippet*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidSnippet} integration test.")
    public void testActivitiesListWithInvalidMandatoryParametersPartSnippet() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmine.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invalidpartsnippet"),
                YouTubeConnectorProperties.getProperty("mine"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


                 /*Test ActivitiesList With Mandatory Parameters contentDetails*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListContentDetails} integration test.")
    public void testActivitiesListWithMandatoryParametersPartContentDetails() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmine.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partcontentDetails"),
                YouTubeConnectorProperties.getProperty("mine"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#activityListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

                     /*Test ActivitiesList With Invalid Mandatory Parameters contentDetails*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidContentDetails} integration test.")
    public void testActivitiesListWithInvalidMandatoryParametersPartContentDetails() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmine.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invalidpartcontentDetails"),
                YouTubeConnectorProperties.getProperty("mine"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

        /*Test ActivitiesList With Mandatory Parameters channelId*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListChannelId} integration test.")
    public void testActivitiesListWithMandatoryParametersChannelId() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListchannelId.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
								YouTubeConnectorProperties.getProperty("client_id"),
								YouTubeConnectorProperties.getProperty("client_secret"),
								YouTubeConnectorProperties.getProperty("grant_type"),
								YouTubeConnectorProperties.getProperty("refresh_token"),
								YouTubeConnectorProperties.getProperty("partid"),
								YouTubeConnectorProperties.getProperty("channelId"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#activityListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

            /*Test ActivitiesList With Invalid Mandatory Parameters channelId*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidChannelId} integration test.")
    public void testActivitiesListInvalidWithMandatoryParametersChannelId() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListchannelId.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("invalidchannelId"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

           /*Test ActivitiesList With Mandatory Parameters mine*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListMine} integration test.")
    public void testActivitiesListWithMandatoryParametersMine() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmine.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#activityListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

               /*Test ActivitiesList With Invalid Mandatory Parameters mine*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidMine} integration test.")
    public void testActivitiesListWithInvalidMandatoryParametersMine() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmine.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("invalidmine"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

               /*Test ActivitiesList With Mandatory Parameters home*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListHome} integration test.")
    public void testActivitiesListWithMandatoryParametersHome() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListhome.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("home"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#activityListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

                   /*Test ActivitiesList With Invalid Mandatory Parameters home*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidHome} integration test.")
    public void testActivitiesListWithInvalidMandatoryParametersHome() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListhome.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("invalidhome"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


           /*Test ActivitiesList With Optional Parameters maxResults*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListMaxResults} integration test.")
    public void testActivitiesListWithOptionalParametersMaxResults() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmineMaxResults.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("maxResults"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ActivitiesList With Optional Invalid Parameters maxResults*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidMaxResults} integration test.")
    public void testActivitiesListWithOptionalInvalidParametersMaxResults() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmineMaxResults.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("invalidmaxResults"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


              /*Test ActivitiesList With Optional Parameters pageToken*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListPageToken} integration test.")
    public void testActivitiesListWithOptionalParametersPageToken() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListminePageToken.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("pageToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ActivitiesList With Optional Invalid Parameters pageToken*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidpageToken} integration test.")
    public void testActivitiesListWithOptionalInvalidParametersPageToken() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListminePageToken.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("invalidpageToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


              /*Test ActivitiesList With Optional Parameters PublishedAfter*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListPublishedAfter} integration test.")
    public void testActivitiesListWithOptionalParametersPublishedAfter() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListminePublishedAfter.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("publishedAfter"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ActivitiesList With Optional Invalid Parameters PublishedAfter*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidPublishedAfter} integration test.")
    public void testActivitiesListWithOptionalInvalidParametersPublishedAfter() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListminePublishedAfter.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("invalidpublishedAfter"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


                  /*Test ActivitiesList With Optional Parameters PublishedBefore*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListPublishedBefore} integration test.")
    public void testActivitiesListWithOptionalParametersPublishedBefore() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListminePublishedBefore.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("publishedBefore"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ActivitiesList With Optional Invalid Parameters PublishedBefore*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidPublishedBefore} integration test.")
    public void testActivitiesListWithOptionalInvalidParametersPublishedBefore() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListminePublishedBefore.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("invalidpublishedBefore"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


                  /*Test ActivitiesList With Optional Parameters regionCode*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListregionCode} integration test.")
    public void testActivitiesListWithOptionalParametersPublishedBefore() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmineRegionCode.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("regionCode"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ActivitiesList With Optional Invalid Parameters regionCode*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidRegionCode} integration test.")
    public void testActivitiesListWithOptionalInvalidParametersPublishedBefore() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmineRegionCode.txt";
        String methodName = "ActivitiesList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("invalidregionCode"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

*/
/******************************************************ActvitiesListfinishes***********************************************************************************************/


              /*Test ActivitiesInsert With Mandatory Parameters snippet*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesInsertSnippet} integration test.")
    public void testActivitiesInserttWithMandatoryParametersPartSnippet() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesInsertmineSnippet.txt";
        String methodName = "ActivitiesInsertSnippet";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partsnippet"),
                YouTubeConnectorProperties.getProperty("description"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

                  /*Test ActivitiesInsert With Invalid Mandatory Parameters snippet*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesInsertInvalidSnippet} integration test.")
    public void testActivitiesInserttWithInvalidParametersPartSnippet() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesInsertmineSnippet.txt";
        String methodName = "ActivitiesInsertSnippet";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invalidpartsnippet"),
                YouTubeConnectorProperties.getProperty("description"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


              /*Test ActivitiesInsert With Mandatory Parameters snippetAndContentDetails*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesInsertSnippetAndContentDetails} integration test.")
    public void testActivitiesInserttWithMandatoryParametersPartSnippetAndContentDetails() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesInsertmineSnippetAndContentDetails.txt";
        String methodName = "ActivitiesInsertSnippetAndContentDetails";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("snippetandcontentdetails"),
                YouTubeConnectorProperties.getProperty("kind"),
                YouTubeConnectorProperties.getProperty("videoId"),
                YouTubeConnectorProperties.getProperty("description"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

                  /*Test ActivitiesInsert With Invalid Mandatory Parameters snippetandcontendetails*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesInsertInvalidSnippetAndContentDetails} integration test.")
    public void testActivitiesInserttWithInvalidParametersPartSnippetAndContentDetails() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesInsertmineSnippetAndContentDetails.txt";
        String methodName = "ActivitiesInsertSnippetAndContentDetails";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invalidpartsnippet"),
                YouTubeConnectorProperties.getProperty("kind"),
                YouTubeConnectorProperties.getProperty("videoId"),
                YouTubeConnectorProperties.getProperty("description"));

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
