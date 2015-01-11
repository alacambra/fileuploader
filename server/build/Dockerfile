FROM jboss/wildfly
ADD uploader.war /opt/uploader/files/
USER root
RUN chown jboss /opt/uploader/files/
ADD uploader.war /opt/jboss/wildfly/standalone/deployments/
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]