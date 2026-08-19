#!/bin/sh
set -eu

: "${H2_USER:=sa}"
: "${H2_PASSWORD:=password}"
: "${H2_DATABASE:=userdb}"

java -cp /opt/h2/h2.jar org.h2.tools.Shell \
  -url "jdbc:h2:/data/${H2_DATABASE}" \
  -user "${H2_USER}" \
  -password "${H2_PASSWORD}" \
  -sql "SELECT 1"

exec java -cp /opt/h2/h2.jar org.h2.tools.Server \
  -tcp \
  -tcpAllowOthers \
  -tcpPort 9092 \
  -ifNotExists
