#!/usr/bin/env bash
set -euo pipefail

docker container run \
    -t -i \
    -v /home/yayu/.m2:/home/yayu/.m2 \
    -v /home/yayu/Project/Upgrade-Fuzzing/jqf:/home/yayu/jqf \
    -v /home/yayu/Project/Upgrade-Fuzzing/afl:/home/yayu/afl \
    -v /home/yayu/Project/Upgrade-Fuzzing/hadoop:/home/yayu/hadoop \
    -w /home/yayu/hadoop \
    -u yayu \
    --network bridge \
    --privileged \
    --name hadoop-jqf \
    hadoop:jqf \
    /bin/zsh
