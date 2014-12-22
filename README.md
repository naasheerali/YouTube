YouTube
=======
Product: Integration tests for WSO2 ESB YouTube connector Prerequisites:

    Maven 3.x
    Java 1.6 or above

Tested Platform:

    ubuntu 14.o4
    WSO2 ESB 4.8.1
    
STEPS:

1.Add the following Message Formatters  and Message Builders  in ESB_HOME/repository/conf/axis2.xml file.

Message Formatters:

\<messageFormatter contentType="application/octet-stream"
    class="org.wso2.carbon.relay.ExpandingMessageFormatter"/\>
\<messageFormatter contentType="video/*"
    class="org.wso2.carbon.relay.ExpandingMessageFormatter"/\>

Message Builders:<messageBuilder contentType="video/*"
    class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
<messageBuilder contentType="application/octet-stream"
    class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

2.Make sure the EsB 4.8.1 zip file with latest patches at: "repository"

3.run the following command. $ mvn clean install

Account Details: username:
    wso2connector.youtub@gmail.com
    password: esbconnector


