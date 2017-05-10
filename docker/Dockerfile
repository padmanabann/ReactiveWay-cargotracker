FROM payara/server-full

RUN \
 $PAYARA_PATH/bin/asadmin start-domain && \
 $PAYARA_PATH/bin/asadmin --user $ADMIN_USER --passwordfile=/opt/pwdfile set-hazelcast-configuration --enabled true && \
 $PAYARA_PATH/bin/asadmin stop-domain
