#!/bin/bash
set -oe pipefail

dependencies="hadoop-hdfs-project/hadoop-hdfs/target/dependency/*"

classpath=${dependencies}\
:hadoop-hdfs-project/hadoop-hdfs/src/test/java\
:hadoop-hdfs-project/hadoop-hdfs/target\
:hadoop-hdfs-project/hadoop-hdfs/target/classes\
:hadoop-hdfs-project/hadoop-hdfs/target/test-classes\
:hadoop-hdfs-project/hadoop-hdfs/target/dependency\
:hadoop-hdfs-project/hadoop-hdfs-client/target/classes\
:hadoop-common-project/target\
:hadoop-common-project/hadoop-common/target/classes\
:hadoop-common-project/hadoop-common/target/test-classes


upgradefuzz_dir="hadoop-hdfs-project/hadoop-hdfs/src/test/java/org/apache/hadoop/hdfs/server/namenode/upgradefuzzing/"

java -cp ${classpath} org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.MiniCluster "$@"
