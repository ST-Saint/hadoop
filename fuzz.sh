#!/bin/bash
set -e pipefail

javac -cp /home/yayu/.m2/repository/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar:/home/yayu/.m2/repository/edu/berkeley/cs/jqf/jqf-fuzz/1.7/jqf-fuzz-1.7.jar:/home/yayu/.m2/repository/com/pholser/junit-quickcheck-core/1.0/junit-quickcheck-core-1.0.jar:/home/yayu/.m2/repository/org/javaruntype/javaruntype/1.3/javaruntype-1.3.jar:/home/yayu/.m2/repository/org/antlr/antlr-runtime/3.1.2/antlr-runtime-3.1.2.jar:/home/yayu/.m2/repository/ognl/ognl/3.1.12/ognl-3.1.12.jar:/home/yayu/.m2/repository/org/javassist/javassist/3.20.0-GA/javassist-3.20.0-GA.jar:/home/yayu/.m2/repository/ru/vyarus/generics-resolver/3.0.1/generics-resolver-3.0.1.jar:/home/yayu/.m2/repository/edu/berkeley/cs/jqf/jqf-instrument/1.7/jqf-instrument-1.7.jar:/home/yayu/.m2/repository/org/ow2/asm/asm/5.0.4/asm-5.0.4.jar:/home/yayu/.m2/repository/org/jacoco/org.jacoco.report/0.8.2/org.jacoco.report-0.8.2.jar:/home/yayu/.m2/repository/org/jacoco/org.jacoco.core/0.8.2/org.jacoco.core-0.8.2.jar:/home/yayu/.m2/repository/info/picocli/picocli/4.0.4/picocli-4.0.4.jar:/home/yayu/.m2/repository/com/pholser/junit-quickcheck-generators/0.8/junit-quickcheck-generators-0.8.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-auth/3.3.1/hadoop-auth-3.3.1.jar:/home/yayu/.m2/repository/org/slf4j/slf4j-api/1.7.30/slf4j-api-1.7.30.jar:/home/yayu/.m2/repository/org/apache/httpcomponents/httpclient/4.5.13/httpclient-4.5.13.jar:/home/yayu/.m2/repository/org/apache/httpcomponents/httpcore/4.4.13/httpcore-4.4.13.jar:/home/yayu/.m2/repository/com/nimbusds/nimbus-jose-jwt/9.8.1/nimbus-jose-jwt-9.8.1.jar:/home/yayu/.m2/repository/com/github/stephenc/jcip/jcip-annotations/1.0-1/jcip-annotations-1.0-1.jar:/home/yayu/.m2/repository/net/minidev/json-smart/2.4.2/json-smart-2.4.2.jar:/home/yayu/.m2/repository/net/minidev/accessors-smart/2.4.2/accessors-smart-2.4.2.jar:/home/yayu/.m2/repository/org/apache/zookeeper/zookeeper/3.5.6/zookeeper-3.5.6.jar:/home/yayu/.m2/repository/org/apache/curator/curator-framework/4.2.0/curator-framework-4.2.0.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-simplekdc/1.0.1/kerb-simplekdc-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-client/1.0.1/kerb-client-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerby-config/1.0.1/kerby-config-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-common/1.0.1/kerb-common-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-crypto/1.0.1/kerb-crypto-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-util/1.0.1/kerb-util-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/token-provider/1.0.1/token-provider-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-admin/1.0.1/kerb-admin-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-server/1.0.1/kerb-server-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-identity/1.0.1/kerb-identity-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerby-xdr/1.0.1/kerby-xdr-1.0.1.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-common/3.3.1/hadoop-common-3.3.1.jar:/home/yayu/.m2/repository/org/apache/hadoop/thirdparty/hadoop-shaded-protobuf_3_7/1.1.1/hadoop-shaded-protobuf_3_7-1.1.1.jar:/home/yayu/.m2/repository/com/google/guava/guava/27.0-jre/guava-27.0-jre.jar:/home/yayu/.m2/repository/com/google/guava/failureaccess/1.0/failureaccess-1.0.jar:/home/yayu/.m2/repository/com/google/guava/listenablefuture/9999.0-empty-to-avoid-conflict-with-guava/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar:/home/yayu/.m2/repository/org/checkerframework/checker-qual/2.5.2/checker-qual-2.5.2.jar:/home/yayu/.m2/repository/com/google/j2objc/j2objc-annotations/1.1/j2objc-annotations-1.1.jar:/home/yayu/.m2/repository/org/codehaus/mojo/animal-sniffer-annotations/1.17/animal-sniffer-annotations-1.17.jar:/home/yayu/.m2/repository/org/apache/commons/commons-math3/3.1.1/commons-math3-3.1.1.jar:/home/yayu/.m2/repository/commons-net/commons-net/3.6/commons-net-3.6.jar:/home/yayu/.m2/repository/commons-collections/commons-collections/3.2.2/commons-collections-3.2.2.jar:/home/yayu/.m2/repository/jakarta/activation/jakarta.activation-api/1.2.1/jakarta.activation-api-1.2.1.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-servlet/9.4.40.v20210413/jetty-servlet-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-security/9.4.40.v20210413/jetty-security-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-webapp/9.4.40.v20210413/jetty-webapp-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-xml/9.4.40.v20210413/jetty-xml-9.4.40.v20210413.jar:/home/yayu/.m2/repository/javax/servlet/jsp/jsp-api/2.1/jsp-api-2.1.jar:/home/yayu/.m2/repository/com/sun/jersey/jersey-servlet/1.19/jersey-servlet-1.19.jar:/home/yayu/.m2/repository/com/sun/jersey/jersey-json/1.19/jersey-json-1.19.jar:/home/yayu/.m2/repository/org/codehaus/jettison/jettison/1.1/jettison-1.1.jar:/home/yayu/.m2/repository/com/sun/xml/bind/jaxb-impl/2.2.3-1/jaxb-impl-2.2.3-1.jar:/home/yayu/.m2/repository/javax/xml/bind/jaxb-api/2.2.11/jaxb-api-2.2.11.jar:/home/yayu/.m2/repository/org/codehaus/jackson/jackson-core-asl/1.9.13/jackson-core-asl-1.9.13.jar:/home/yayu/.m2/repository/org/codehaus/jackson/jackson-mapper-asl/1.9.13/jackson-mapper-asl-1.9.13.jar:/home/yayu/.m2/repository/org/codehaus/jackson/jackson-jaxrs/1.9.13/jackson-jaxrs-1.9.13.jar:/home/yayu/.m2/repository/org/codehaus/jackson/jackson-xc/1.9.13/jackson-xc-1.9.13.jar:/home/yayu/.m2/repository/commons-beanutils/commons-beanutils/1.9.4/commons-beanutils-1.9.4.jar:/home/yayu/.m2/repository/org/apache/commons/commons-configuration2/2.1.1/commons-configuration2-2.1.1.jar:/home/yayu/.m2/repository/org/apache/commons/commons-lang3/3.7/commons-lang3-3.7.jar:/home/yayu/.m2/repository/org/apache/commons/commons-text/1.4/commons-text-1.4.jar:/home/yayu/.m2/repository/org/apache/avro/avro/1.7.7/avro-1.7.7.jar:/home/yayu/.m2/repository/com/thoughtworks/paranamer/paranamer/2.3/paranamer-2.3.jar:/home/yayu/.m2/repository/com/google/re2j/re2j/1.1/re2j-1.1.jar:/home/yayu/.m2/repository/com/google/code/gson/gson/2.2.4/gson-2.2.4.jar:/home/yayu/.m2/repository/com/jcraft/jsch/0.1.55/jsch-0.1.55.jar:/home/yayu/.m2/repository/org/apache/curator/curator-client/4.2.0/curator-client-4.2.0.jar:/home/yayu/.m2/repository/org/apache/curator/curator-recipes/4.2.0/curator-recipes-4.2.0.jar:/home/yayu/.m2/repository/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar:/home/yayu/.m2/repository/org/apache/commons/commons-compress/1.19/commons-compress-1.19.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-core/1.0.1/kerb-core-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerby-pkix/1.0.1/kerby-pkix-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerby-asn1/1.0.1/kerby-asn1-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerby-util/1.0.1/kerby-util-1.0.1.jar:/home/yayu/.m2/repository/org/codehaus/woodstox/stax2-api/4.2.1/stax2-api-4.2.1.jar:/home/yayu/.m2/repository/com/fasterxml/woodstox/woodstox-core/5.3.0/woodstox-core-5.3.0.jar:/home/yayu/.m2/repository/dnsjava/dnsjava/2.1.7/dnsjava-2.1.7.jar:/home/yayu/.m2/repository/org/xerial/snappy/snappy-java/1.1.8.2/snappy-java-1.1.8.2.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-common/3.3.1/hadoop-common-3.3.1-tests.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-hdfs-client/3.3.1/hadoop-hdfs-client-3.3.1.jar:/home/yayu/.m2/repository/com/squareup/okhttp/okhttp/2.7.5/okhttp-2.7.5.jar:/home/yayu/.m2/repository/com/squareup/okio/okio/1.6.0/okio-1.6.0.jar:/home/yayu/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.10.5/jackson-annotations-2.10.5.jar:/home/yayu/.m2/repository/org/apache/zookeeper/zookeeper/3.5.6/zookeeper-3.5.6-tests.jar:/home/yayu/.m2/repository/org/apache/zookeeper/zookeeper-jute/3.5.6/zookeeper-jute-3.5.6.jar:/home/yayu/.m2/repository/org/apache/yetus/audience-annotations/0.5.0/audience-annotations-0.5.0.jar:/home/yayu/.m2/repository/org/apache/hadoop/thirdparty/hadoop-shaded-guava/1.1.1/hadoop-shaded-guava-1.1.1.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-server/9.4.40.v20210413/jetty-server-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-http/9.4.40.v20210413/jetty-http-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-io/9.4.40.v20210413/jetty-io-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-util/9.4.40.v20210413/jetty-util-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-util-ajax/9.4.40.v20210413/jetty-util-ajax-9.4.40.v20210413.jar:/home/yayu/.m2/repository/com/sun/jersey/jersey-core/1.19/jersey-core-1.19.jar:/home/yayu/.m2/repository/javax/ws/rs/jsr311-api/1.1.1/jsr311-api-1.1.1.jar:/home/yayu/.m2/repository/com/sun/jersey/jersey-server/1.19/jersey-server-1.19.jar:/home/yayu/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar:/home/yayu/.m2/repository/commons-codec/commons-codec/1.11/commons-codec-1.11.jar:/home/yayu/.m2/repository/commons-io/commons-io/2.8.0/commons-io-2.8.0.jar:/home/yayu/.m2/repository/commons-logging/commons-logging/1.1.3/commons-logging-1.1.3.jar:/home/yayu/.m2/repository/commons-daemon/commons-daemon/1.0.13/commons-daemon-1.0.13.jar:/home/yayu/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar:/home/yayu/.m2/repository/com/google/protobuf/protobuf-java/2.5.0/protobuf-java-2.5.0.jar:/home/yayu/.m2/repository/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar:/home/yayu/.m2/repository/com/google/code/findbugs/findbugs/3.0.1/findbugs-3.0.1.jar:/home/yayu/.m2/repository/net/jcip/jcip-annotations/1.0/jcip-annotations-1.0.jar:/home/yayu/.m2/repository/com/google/code/findbugs/bcel-findbugs/6.0/bcel-findbugs-6.0.jar:/home/yayu/.m2/repository/com/google/code/findbugs/jFormatString/2.0.1/jFormatString-2.0.1.jar:/home/yayu/.m2/repository/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:/home/yayu/.m2/repository/org/ow2/asm/asm-debug-all/5.0.2/asm-debug-all-5.0.2.jar:/home/yayu/.m2/repository/org/ow2/asm/asm-commons/5.0.2/asm-commons-5.0.2.jar:/home/yayu/.m2/repository/org/ow2/asm/asm-tree/5.0.2/asm-tree-5.0.2.jar:/home/yayu/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar:/home/yayu/.m2/repository/com/apple/AppleJavaExtensions/1.4/AppleJavaExtensions-1.4.jar:/home/yayu/.m2/repository/jaxen/jaxen/1.1.6/jaxen-1.1.6.jar:/home/yayu/.m2/repository/junit/junit/4.13.1/junit-4.13.1.jar:/home/yayu/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-minikdc/3.3.1/hadoop-minikdc-3.3.1.jar:/home/yayu/.m2/repository/org/mockito/mockito-core/2.28.2/mockito-core-2.28.2.jar:/home/yayu/.m2/repository/net/bytebuddy/byte-buddy/1.9.10/byte-buddy-1.9.10.jar:/home/yayu/.m2/repository/net/bytebuddy/byte-buddy-agent/1.9.10/byte-buddy-agent-1.9.10.jar:/home/yayu/.m2/repository/org/objenesis/objenesis/2.6/objenesis-2.6.jar:/home/yayu/.m2/repository/org/slf4j/slf4j-log4j12/1.7.30/slf4j-log4j12-1.7.30.jar:/home/yayu/.m2/repository/io/netty/netty/3.10.6.Final/netty-3.10.6.Final.jar:/home/yayu/.m2/repository/io/netty/netty-all/4.1.61.Final/netty-all-4.1.61.Final.jar:/home/yayu/.m2/repository/org/apache/htrace/htrace-core4/4.1.0-incubating/htrace-core4-4.1.0-incubating.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-kms/3.3.1/hadoop-kms-3.3.1.jar:/home/yayu/.m2/repository/org/slf4j/jul-to-slf4j/1.7.30/jul-to-slf4j-1.7.30.jar:/home/yayu/.m2/repository/io/dropwizard/metrics/metrics-core/3.2.4/metrics-core-3.2.4.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-kms/3.3.1/hadoop-kms-3.3.1-tests.jar:/home/yayu/.m2/repository/org/fusesource/leveldbjni/leveldbjni-all/1.8/leveldbjni-all-1.8.jar:/home/yayu/.m2/repository/org/bouncycastle/bcprov-jdk15on/1.60/bcprov-jdk15on-1.60.jar:/home/yayu/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.10.5.1/jackson-databind-2.10.5.1.jar:/home/yayu/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.10.5/jackson-core-2.10.5.jar:/home/yayu/.m2/repository/org/apache/curator/curator-test/4.2.0/curator-test-4.2.0.jar:/home/yayu/.m2/repository/org/assertj/assertj-core/3.12.2/assertj-core-3.12.2.jar:/home/yayu/.m2/repository/org/lz4/lz4-java/1.7.1/lz4-java-1.7.1.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-annotations/3.3.1/hadoop-annotations-3.3.1.jar:hadoop-hdfs-project/hadoop-hdfs/target/classes:hadoop-hdfs-project/hadoop-hdfs/target/test-classes:hadoop-hdfs-project/hadoop-hdfs/target/classes hadoop-hdfs-project/hadoop-hdfs/src/test/java/org/apache/hadoop/hdfs/server/namenode/upgradefuzzing/UpgradeFuzzingTest.java hadoop-hdfs-project/hadoop-hdfs/src/test/java/org/apache/hadoop/hdfs/server/namenode/upgradefuzzing/FsShellGenerator.java

if [[ -z "$AFL_DIR" ]]; then
    export AFL_DIR=$HOME/Project/Upgrade-Fuzzing/afl
    if [ ! -d $AFL_DIR ]; then
        export AFL_DIR=$HOME/afl
    fi
fi
if [[ -z "$JQF_DIR" ]]; then
    JQF_DIR=$HOME/Project/Upgrade-Fuzzing/jqf
    if [ ! -d $JQF_DIR ]; then
        JQF_DIR=$HOME/jqf
    fi
fi
JQF_SID=$(( ( RANDOM % 1000 )  + 1 ))
echo $JQF_SID
$JQF_DIR/bin/jqf-afl-fuzz -c /home/yayu/.m2/repository/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar:/home/yayu/.m2/repository/edu/berkeley/cs/jqf/jqf-fuzz/1.7/jqf-fuzz-1.7.jar:/home/yayu/.m2/repository/com/pholser/junit-quickcheck-core/1.0/junit-quickcheck-core-1.0.jar:/home/yayu/.m2/repository/org/javaruntype/javaruntype/1.3/javaruntype-1.3.jar:/home/yayu/.m2/repository/org/antlr/antlr-runtime/3.1.2/antlr-runtime-3.1.2.jar:/home/yayu/.m2/repository/ognl/ognl/3.1.12/ognl-3.1.12.jar:/home/yayu/.m2/repository/org/javassist/javassist/3.20.0-GA/javassist-3.20.0-GA.jar:/home/yayu/.m2/repository/ru/vyarus/generics-resolver/3.0.1/generics-resolver-3.0.1.jar:/home/yayu/.m2/repository/edu/berkeley/cs/jqf/jqf-instrument/1.7/jqf-instrument-1.7.jar:/home/yayu/.m2/repository/org/ow2/asm/asm/5.0.4/asm-5.0.4.jar:/home/yayu/.m2/repository/org/jacoco/org.jacoco.report/0.8.2/org.jacoco.report-0.8.2.jar:/home/yayu/.m2/repository/org/jacoco/org.jacoco.core/0.8.2/org.jacoco.core-0.8.2.jar:/home/yayu/.m2/repository/info/picocli/picocli/4.0.4/picocli-4.0.4.jar:/home/yayu/.m2/repository/com/pholser/junit-quickcheck-generators/0.8/junit-quickcheck-generators-0.8.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-auth/3.3.1/hadoop-auth-3.3.1.jar:/home/yayu/.m2/repository/org/slf4j/slf4j-api/1.7.30/slf4j-api-1.7.30.jar:/home/yayu/.m2/repository/org/apache/httpcomponents/httpclient/4.5.13/httpclient-4.5.13.jar:/home/yayu/.m2/repository/org/apache/httpcomponents/httpcore/4.4.13/httpcore-4.4.13.jar:/home/yayu/.m2/repository/com/nimbusds/nimbus-jose-jwt/9.8.1/nimbus-jose-jwt-9.8.1.jar:/home/yayu/.m2/repository/com/github/stephenc/jcip/jcip-annotations/1.0-1/jcip-annotations-1.0-1.jar:/home/yayu/.m2/repository/net/minidev/json-smart/2.4.2/json-smart-2.4.2.jar:/home/yayu/.m2/repository/net/minidev/accessors-smart/2.4.2/accessors-smart-2.4.2.jar:/home/yayu/.m2/repository/org/apache/zookeeper/zookeeper/3.5.6/zookeeper-3.5.6.jar:/home/yayu/.m2/repository/org/apache/curator/curator-framework/4.2.0/curator-framework-4.2.0.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-simplekdc/1.0.1/kerb-simplekdc-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-client/1.0.1/kerb-client-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerby-config/1.0.1/kerby-config-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-common/1.0.1/kerb-common-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-crypto/1.0.1/kerb-crypto-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-util/1.0.1/kerb-util-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/token-provider/1.0.1/token-provider-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-admin/1.0.1/kerb-admin-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-server/1.0.1/kerb-server-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-identity/1.0.1/kerb-identity-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerby-xdr/1.0.1/kerby-xdr-1.0.1.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-common/3.3.1/hadoop-common-3.3.1.jar:/home/yayu/.m2/repository/org/apache/hadoop/thirdparty/hadoop-shaded-protobuf_3_7/1.1.1/hadoop-shaded-protobuf_3_7-1.1.1.jar:/home/yayu/.m2/repository/com/google/guava/guava/27.0-jre/guava-27.0-jre.jar:/home/yayu/.m2/repository/com/google/guava/failureaccess/1.0/failureaccess-1.0.jar:/home/yayu/.m2/repository/com/google/guava/listenablefuture/9999.0-empty-to-avoid-conflict-with-guava/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar:/home/yayu/.m2/repository/org/checkerframework/checker-qual/2.5.2/checker-qual-2.5.2.jar:/home/yayu/.m2/repository/com/google/j2objc/j2objc-annotations/1.1/j2objc-annotations-1.1.jar:/home/yayu/.m2/repository/org/codehaus/mojo/animal-sniffer-annotations/1.17/animal-sniffer-annotations-1.17.jar:/home/yayu/.m2/repository/org/apache/commons/commons-math3/3.1.1/commons-math3-3.1.1.jar:/home/yayu/.m2/repository/commons-net/commons-net/3.6/commons-net-3.6.jar:/home/yayu/.m2/repository/commons-collections/commons-collections/3.2.2/commons-collections-3.2.2.jar:/home/yayu/.m2/repository/jakarta/activation/jakarta.activation-api/1.2.1/jakarta.activation-api-1.2.1.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-servlet/9.4.40.v20210413/jetty-servlet-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-security/9.4.40.v20210413/jetty-security-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-webapp/9.4.40.v20210413/jetty-webapp-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-xml/9.4.40.v20210413/jetty-xml-9.4.40.v20210413.jar:/home/yayu/.m2/repository/javax/servlet/jsp/jsp-api/2.1/jsp-api-2.1.jar:/home/yayu/.m2/repository/com/sun/jersey/jersey-servlet/1.19/jersey-servlet-1.19.jar:/home/yayu/.m2/repository/com/sun/jersey/jersey-json/1.19/jersey-json-1.19.jar:/home/yayu/.m2/repository/org/codehaus/jettison/jettison/1.1/jettison-1.1.jar:/home/yayu/.m2/repository/com/sun/xml/bind/jaxb-impl/2.2.3-1/jaxb-impl-2.2.3-1.jar:/home/yayu/.m2/repository/javax/xml/bind/jaxb-api/2.2.11/jaxb-api-2.2.11.jar:/home/yayu/.m2/repository/org/codehaus/jackson/jackson-core-asl/1.9.13/jackson-core-asl-1.9.13.jar:/home/yayu/.m2/repository/org/codehaus/jackson/jackson-mapper-asl/1.9.13/jackson-mapper-asl-1.9.13.jar:/home/yayu/.m2/repository/org/codehaus/jackson/jackson-jaxrs/1.9.13/jackson-jaxrs-1.9.13.jar:/home/yayu/.m2/repository/org/codehaus/jackson/jackson-xc/1.9.13/jackson-xc-1.9.13.jar:/home/yayu/.m2/repository/commons-beanutils/commons-beanutils/1.9.4/commons-beanutils-1.9.4.jar:/home/yayu/.m2/repository/org/apache/commons/commons-configuration2/2.1.1/commons-configuration2-2.1.1.jar:/home/yayu/.m2/repository/org/apache/commons/commons-lang3/3.7/commons-lang3-3.7.jar:/home/yayu/.m2/repository/org/apache/commons/commons-text/1.4/commons-text-1.4.jar:/home/yayu/.m2/repository/org/apache/avro/avro/1.7.7/avro-1.7.7.jar:/home/yayu/.m2/repository/com/thoughtworks/paranamer/paranamer/2.3/paranamer-2.3.jar:/home/yayu/.m2/repository/com/google/re2j/re2j/1.1/re2j-1.1.jar:/home/yayu/.m2/repository/com/google/code/gson/gson/2.2.4/gson-2.2.4.jar:/home/yayu/.m2/repository/com/jcraft/jsch/0.1.55/jsch-0.1.55.jar:/home/yayu/.m2/repository/org/apache/curator/curator-client/4.2.0/curator-client-4.2.0.jar:/home/yayu/.m2/repository/org/apache/curator/curator-recipes/4.2.0/curator-recipes-4.2.0.jar:/home/yayu/.m2/repository/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar:/home/yayu/.m2/repository/org/apache/commons/commons-compress/1.19/commons-compress-1.19.jar:/home/yayu/.m2/repository/org/apache/kerby/kerb-core/1.0.1/kerb-core-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerby-pkix/1.0.1/kerby-pkix-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerby-asn1/1.0.1/kerby-asn1-1.0.1.jar:/home/yayu/.m2/repository/org/apache/kerby/kerby-util/1.0.1/kerby-util-1.0.1.jar:/home/yayu/.m2/repository/org/codehaus/woodstox/stax2-api/4.2.1/stax2-api-4.2.1.jar:/home/yayu/.m2/repository/com/fasterxml/woodstox/woodstox-core/5.3.0/woodstox-core-5.3.0.jar:/home/yayu/.m2/repository/dnsjava/dnsjava/2.1.7/dnsjava-2.1.7.jar:/home/yayu/.m2/repository/org/xerial/snappy/snappy-java/1.1.8.2/snappy-java-1.1.8.2.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-common/3.3.1/hadoop-common-3.3.1-tests.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-hdfs-client/3.3.1/hadoop-hdfs-client-3.3.1.jar:/home/yayu/.m2/repository/com/squareup/okhttp/okhttp/2.7.5/okhttp-2.7.5.jar:/home/yayu/.m2/repository/com/squareup/okio/okio/1.6.0/okio-1.6.0.jar:/home/yayu/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.10.5/jackson-annotations-2.10.5.jar:/home/yayu/.m2/repository/org/apache/zookeeper/zookeeper/3.5.6/zookeeper-3.5.6-tests.jar:/home/yayu/.m2/repository/org/apache/zookeeper/zookeeper-jute/3.5.6/zookeeper-jute-3.5.6.jar:/home/yayu/.m2/repository/org/apache/yetus/audience-annotations/0.5.0/audience-annotations-0.5.0.jar:/home/yayu/.m2/repository/org/apache/hadoop/thirdparty/hadoop-shaded-guava/1.1.1/hadoop-shaded-guava-1.1.1.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-server/9.4.40.v20210413/jetty-server-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-http/9.4.40.v20210413/jetty-http-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-io/9.4.40.v20210413/jetty-io-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-util/9.4.40.v20210413/jetty-util-9.4.40.v20210413.jar:/home/yayu/.m2/repository/org/eclipse/jetty/jetty-util-ajax/9.4.40.v20210413/jetty-util-ajax-9.4.40.v20210413.jar:/home/yayu/.m2/repository/com/sun/jersey/jersey-core/1.19/jersey-core-1.19.jar:/home/yayu/.m2/repository/javax/ws/rs/jsr311-api/1.1.1/jsr311-api-1.1.1.jar:/home/yayu/.m2/repository/com/sun/jersey/jersey-server/1.19/jersey-server-1.19.jar:/home/yayu/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar:/home/yayu/.m2/repository/commons-codec/commons-codec/1.11/commons-codec-1.11.jar:/home/yayu/.m2/repository/commons-io/commons-io/2.8.0/commons-io-2.8.0.jar:/home/yayu/.m2/repository/commons-logging/commons-logging/1.1.3/commons-logging-1.1.3.jar:/home/yayu/.m2/repository/commons-daemon/commons-daemon/1.0.13/commons-daemon-1.0.13.jar:/home/yayu/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar:/home/yayu/.m2/repository/com/google/protobuf/protobuf-java/2.5.0/protobuf-java-2.5.0.jar:/home/yayu/.m2/repository/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar:/home/yayu/.m2/repository/com/google/code/findbugs/findbugs/3.0.1/findbugs-3.0.1.jar:/home/yayu/.m2/repository/net/jcip/jcip-annotations/1.0/jcip-annotations-1.0.jar:/home/yayu/.m2/repository/com/google/code/findbugs/bcel-findbugs/6.0/bcel-findbugs-6.0.jar:/home/yayu/.m2/repository/com/google/code/findbugs/jFormatString/2.0.1/jFormatString-2.0.1.jar:/home/yayu/.m2/repository/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:/home/yayu/.m2/repository/org/ow2/asm/asm-debug-all/5.0.2/asm-debug-all-5.0.2.jar:/home/yayu/.m2/repository/org/ow2/asm/asm-commons/5.0.2/asm-commons-5.0.2.jar:/home/yayu/.m2/repository/org/ow2/asm/asm-tree/5.0.2/asm-tree-5.0.2.jar:/home/yayu/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar:/home/yayu/.m2/repository/com/apple/AppleJavaExtensions/1.4/AppleJavaExtensions-1.4.jar:/home/yayu/.m2/repository/jaxen/jaxen/1.1.6/jaxen-1.1.6.jar:/home/yayu/.m2/repository/junit/junit/4.13.1/junit-4.13.1.jar:/home/yayu/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-minikdc/3.3.1/hadoop-minikdc-3.3.1.jar:/home/yayu/.m2/repository/org/mockito/mockito-core/2.28.2/mockito-core-2.28.2.jar:/home/yayu/.m2/repository/net/bytebuddy/byte-buddy/1.9.10/byte-buddy-1.9.10.jar:/home/yayu/.m2/repository/net/bytebuddy/byte-buddy-agent/1.9.10/byte-buddy-agent-1.9.10.jar:/home/yayu/.m2/repository/org/objenesis/objenesis/2.6/objenesis-2.6.jar:/home/yayu/.m2/repository/org/slf4j/slf4j-log4j12/1.7.30/slf4j-log4j12-1.7.30.jar:/home/yayu/.m2/repository/io/netty/netty/3.10.6.Final/netty-3.10.6.Final.jar:/home/yayu/.m2/repository/io/netty/netty-all/4.1.61.Final/netty-all-4.1.61.Final.jar:/home/yayu/.m2/repository/org/apache/htrace/htrace-core4/4.1.0-incubating/htrace-core4-4.1.0-incubating.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-kms/3.3.1/hadoop-kms-3.3.1.jar:/home/yayu/.m2/repository/org/slf4j/jul-to-slf4j/1.7.30/jul-to-slf4j-1.7.30.jar:/home/yayu/.m2/repository/io/dropwizard/metrics/metrics-core/3.2.4/metrics-core-3.2.4.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-kms/3.3.1/hadoop-kms-3.3.1-tests.jar:/home/yayu/.m2/repository/org/fusesource/leveldbjni/leveldbjni-all/1.8/leveldbjni-all-1.8.jar:/home/yayu/.m2/repository/org/bouncycastle/bcprov-jdk15on/1.60/bcprov-jdk15on-1.60.jar:/home/yayu/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.10.5.1/jackson-databind-2.10.5.1.jar:/home/yayu/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.10.5/jackson-core-2.10.5.jar:/home/yayu/.m2/repository/org/apache/curator/curator-test/4.2.0/curator-test-4.2.0.jar:/home/yayu/.m2/repository/org/assertj/assertj-core/3.12.2/assertj-core-3.12.2.jar:/home/yayu/.m2/repository/org/lz4/lz4-java/1.7.1/lz4-java-1.7.1.jar:/home/yayu/.m2/repository/org/apache/hadoop/hadoop-annotations/3.3.1/hadoop-annotations-3.3.1.jar:hadoop-hdfs-project/hadoop-hdfs/src/test/java:hadoop-hdfs-project/hadoop-hdfs/target/classes:hadoop-common-project/hadoop-common/target/classes:hadoop-hdfs-project/hadoop-hdfs-client/target/classes:hadoop-hdfs-project/hadoop-hdfs/target/test-classes:hadoop-hdfs-project/hadoop-hdfs/target/classes -i fuzz-seeds -m 32768 -v -t 6000 -S $JQF_SID org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.UpgradeFuzzingTest fuzzingCommand
