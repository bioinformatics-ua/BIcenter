PENTAHO_VERSION=8.0
PDI_BUILD=8.0.0.0-28
MYSQL_CONNECTOR_VERSION=5.1.46

# Download Pentaho if don't exist already
if [ ! -f "pdi.zip" ]; then
    wget https://netix.dl.sourceforge.net/project/pentaho/Pentaho%20${PENTAHO_VERSION}/client-tools/pdi-ce-${PDI_BUILD}.zip
    mv pdi-ce-${PDI_BUILD}.zip pdi.zip
else
    echo "Pentaho $PDI_BUILD alreay exists"
fi

# Download MySQL Drivers if don't exist already
if [ ! -d "mysql-connector" ]; then
    wget -O mysql-connector.zip https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-${MYSQL_CONNECTOR_VERSION}.zip
    unzip mysql-connector.zip
    mv mysql-connector-java-${MYSQL_CONNECTOR_VERSION}/ mysql-connector/
    mv mysql-connector/mysql-connector-java-${MYSQL_CONNECTOR_VERSION}-bin.jar mysql-connector/mysql-connector.java
else
    echo "MySQL connector $MYSQL_CONNECTOR_VERSION alreay exists"
fi
