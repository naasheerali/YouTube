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

public class ChannelsIntegrationTest extends ESBIntegrationTest {

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



          /*Test ChannelsList With Mandatory Parameters snippet*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListPartSnippetAndMine} integration test.")
    public void testChannelsListWithMandatoryParametersSnippetMine() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetMine.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsListpart"),
                YouTubeConnectorProperties.getProperty("ChannelsListmine"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ChannelsList With invalid Mandatory Parameters Invalid snippet*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListInvalidSnippeet} integration test.")
    public void testActivitiesListWithInvalidIdMandatoryParametersSnippeteAndMine() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetMine.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
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




          /*Test ChannelsList With Mandatory Parameters categoryId*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListSnippetAndCategoryId} integration test.")
    public void testChannelsListWithMandatoryParametersSnippetCategoryId() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetCategoryId.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsListpart"),
                YouTubeConnectorProperties.getProperty("ChannelsListCategoryId"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ChannelsList With invalid Mandatory Parameters Invalild CategoryId*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListInvalidSnippeet} integration test.")
    public void testActivitiesListWithInvalidIdMandatoryParametersSnippetCategoryId() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetCategoryId.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsListpart"),
                YouTubeConnectorProperties.getProperty("ChannelsListInvalidCategoryId"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }




          /*Test ChannelsList With Mandatory Parameters forUsername*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListSnippetAndForUsername} integration test.")
    public void testChannelsListWithMandatoryParametersSnippetForUsername() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetForUsername.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsListpart"),
                YouTubeConnectorProperties.getProperty("ChannelsListforUsername"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ChannelsList With invalid Mandatory Parameters Invalild forUsername*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListSnippetAndForUsername} integration test.")
    public void testActivitiesListWithInvalidIdMandatoryParametersSnippetForUsername() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetForUsername.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsListpart"),
                YouTubeConnectorProperties.getProperty("ChannelsListInvalidforUsername"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


          /*Test ChannelsList With Mandatory Parameters id*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListSnippetAndid} integration test.")
    public void testChannelsListWithMandatoryParametersSnippetId() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetid.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsListpart"),
                YouTubeConnectorProperties.getProperty("ChannelsListid"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ChannelsList With invalid Mandatory Parameters Invalild id*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListSnippetAndid} integration test.")
    public void testActivitiesListWithInvalidIdMandatoryParametersSnippetId() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetid.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsListpart"),
                YouTubeConnectorProperties.getProperty("ChannelsListInvalidforUsername"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


              /*Test ChannelsList With Mandatory Parameters mySubscribers*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListSnippetAndmySubscribers} integration test.")
    public void testChannelsListWithMandatoryParametersSnippetmySubscribers() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetmySubscribers.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsListpart"),
                YouTubeConnectorProperties.getProperty("ChannelsListmySubscribers"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ChannelsList With invalid Mandatory Parameters Invalild mySubscribers*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListSnippetAndmySubscribers} integration test.")
    public void testActivitiesListWithInvalidIdMandatoryParametersSnippetmySubscribers() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetmySubscribers.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsListpart"),
                YouTubeConnectorProperties.getProperty("ChannelsListInvalidmySubscribers"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


             /*Test ChannelsList With Mandatory Parameters maxResults*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListSnippetAndmaxResults} integration test.")
    public void testChannelsListWithMandatoryParametersSnippetmaxResults() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetmaxResults.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsListpart"),
                YouTubeConnectorProperties.getProperty("ChannelsListCategoryId"),
                YouTubeConnectorProperties.getProperty("ChannelsListmaxResults"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ChannelsList With invalid Mandatory Parameters Invalild maxResults*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListSnippetAndmaxResults} integration test.")
    public void testActivitiesListWithInvalidIdMandatoryParametersSnippetmaxResults() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetmaxResults.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsListpart"),
                YouTubeConnectorProperties.getProperty("ChannelsListCategoryId"),
                YouTubeConnectorProperties.getProperty("ChannelsListInvalidmaxResults"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


             /*Test ChannelsList With Mandatory Parameters pageToken*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListSnippetAndpageToken} integration test.")
    public void testChannelsListWithMandatoryParametersSnippetpageToken() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetpageToken.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsListpart"),
                YouTubeConnectorProperties.getProperty("ChannelsListCategoryId"),
                YouTubeConnectorProperties.getProperty("ChannelsListpageToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ChannelsList With invalid Mandatory Parameters Invalild pageToken*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsListSnippetAndpageToken} integration test.")
    public void testActivitiesListWithInvalidIdMandatoryParametersSnippetpageToken() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsListSnippetpageToken.txt";
        String methodName = "ChannelsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsListpart"),
                YouTubeConnectorProperties.getProperty("ChannelsListCategoryId"),
                YouTubeConnectorProperties.getProperty("ChannelsListInvalidpageToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


//////////////////////////////////////////////////////////ChannelsListFnished/////////////////////////////////////////////////////////////////////////






             /*Test ChannelsUpdate With Mandatory Parameters id*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateid} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersId() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateId.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ChannelsUpdate With invalid Mandatory Parameters Invalild id*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateId} integration test.")
    public void testChannelsUpdatetWithInvalidIdMandatoryParametersId() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateId.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateInvalidId"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }



             /*Test ChannelsUpdate With Mandatory Parameters Description*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateDescription} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersDescription() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateDescription.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateDescription"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

              /*Test ChannelsUpdate With Mandatory Parameters Keywords*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateKeywords} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersKeywords() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateKeywords.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatekeywords"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }




              /*Test ChannelsUpdate With Mandatory Parameters DefaultTab*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateDefaultTab} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersDefaultTab() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateDefaultTab.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatedefaultTab"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }



              /*Test ChannelsUpdate With Mandatory Parameters ModerateComments*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateModerateComments} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersModerateComments() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateModerateComments.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateModerateComments"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }



              /*Test ChannelsUpdate With Mandatory Parameters ProfileColor*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateProfileColor} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersProfileColor() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateProfileColor.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateProfileColor"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }




              /*Test ChannelsUpdate With Mandatory Parameters ShowRelatedChannels*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateShowRelatedChannels} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersShowRelatedChannels() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateShowRelatedChannels.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateShowRelatedChannels"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }




              /*Test ChannelsUpdate With Mandatory Parameters FeaturedChannelsTitle*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateFeaturedChannelsTitle} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersFeaturedChannelsTitle() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateFeaturedChannelsTitle.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateFeaturedChannelsTitle"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }



              /*Test ChannelsUpdate With Mandatory Parameters FeaturedChannelsUrls*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateFeaturedChannelsUrls} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersFeaturedChannelsUrls() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateFeaturedChannelsUrls.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateFeaturedChannelsUrls"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


              /*Test ChannelsUpdate With Mandatory Parameters UnsubscribedTrailer*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateUnsubscribedTrailer} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersUnsubscribedTrailer() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateUnsubscribedTrailer.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateUnsubscribedTrailer"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


                 /*Test ChannelsUpdate With Mandatory Parameters Default*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateDefault} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersDefault() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateDefault.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepartbrand"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateDefault"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }



                 /*Test ChannelsUpdate With Mandatory Parameters LanguageAndValue*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateLanguageAndValue} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersLanguageAndValue() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateLanguageAndValue.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateLanguage"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateValue"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


                 /*Test ChannelsUpdate With Mandatory Parameters BannerExternalUrl*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateBannerExternalUrl} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersBannerExternalUrl() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateBannerExternalUrl.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatebannerImageUrl"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


                     /*Test ChannelsUpdate With Mandatory Parameters Type*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateType} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersType() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateType.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatetype"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


                     /*Test ChannelsUpdate With Mandatory Parameters CornerPosition*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateCornerPosition} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersCornerPosition() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateCornerPosition.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateCornerPosition"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

                     /*Test ChannelsUpdate With Mandatory Parameters DefaultTimingTypeOffsetMs*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateDefaultTimingTypeOffsetMs} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersDefaultTimingTypeOffsetMs() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateDefaultTimingTypeOffsetMs.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatedefaultTimingtype"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatedefaultTimingoffsetMs"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

                     /*Test ChannelsUpdate With Mandatory Parameters ItemsId*/
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateItemsId} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersItemsId() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateItemsId.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateidtype"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatevideoId"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatewebsiteUrl"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


                     /*Test ChannelsUpdate With Mandatory Parameters IdTiming*/

    @Test(groups = {"wso2.esb"}, description = "YouTube{ChannelsUpdateIdTiming} integration test.")
    public void testChannelsUpdatetWithMandatoryParametersIdTiming() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/ChannelsUpdateIdTiming.txt";
        String methodName = "ChannelsUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepart"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateid"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdateidtype"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatevideoId"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatetimingtype"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatetimingoffsetMs"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepositiontype"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatecornerPosition"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatedefaultTimingtype"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatedefaultTimingoffsetMs"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatecustomMessage"),
                YouTubeConnectorProperties.getProperty("ChannelsUpdatepromotedByContentOwner"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }



}
