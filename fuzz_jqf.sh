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

javac -cp "${classpath}" ${upgradefuzz_dir}/*.java

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
$JQF_DIR/bin/jqf-afl-fuzz -c ${classpath} -i fuzz-seeds -m 32768 -v -t 60000 -S $JQF_SID org.apache.hadoop.hdfs.server.namenode.upgradefuzzing.FuzzingTest fuzzCommand
