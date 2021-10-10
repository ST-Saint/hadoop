FROM ubuntu:bionic

LABEL maintainer="st.saint.wyy@gmail.com"

ENV http_proxy="http://172.17.0.1:2334"
ENV https_proxy="http://172.17.0.1:2334"
ENV HOME="/home/yayu"

ENV TZ=Asia/Shanghai

RUN apt update \
    && apt install -y sudo ca-certificates apt-transport-https  \
    && echo "deb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic main restricted universe multiverse\ndeb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic-updates main restricted universe multiverse\ndeb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic-backports main restricted universe multiverse\ndeb https://mirrors.tuna.tsinghua.edu.cn/ubuntu/ bionic-security main restricted universe multiverse" > /etc/apt/sources.list \
    && apt update

RUN rm -f /var/log/faillog /var/log/lastlog && groupadd --non-unique -g 1000 yayu && useradd -rm -g 1000 -u 1000 -k /root -m yayu -d "/home/yayu" -p wyyy && echo "root ALL=(ALL)  ALL\nyayu ALL=NOPASSWD: ALL" > "/etc/sudoers"

RUN DEBIAN_FRONTEND="noninteractive" \
    apt install -y apt-utils software-properties-common \
    flex bison clang gawk build-essential autoconf automake libtool cmake zlib1g-dev pkg-config libssl-dev libsasl2-dev \
    curl wget iputils-ping iproute2 net-tools\
    openjdk-8-jdk maven \
    git zsh vim

RUN curl -fsSL https://raw.github.com/ohmyzsh/ohmyzsh/master/tools/install.sh > /home/yayu/ohmyz_install.sh \
    && chmod +x /home/yayu/ohmyz_install.sh \
    && echo "n" | /home/yayu/ohmyz_install.sh \
    && chsh -s $(which zsh)

RUN sudo apt-get -y purge "^openjdk-*" \
    && sudo apt-get -y install openjdk-8-jdk openjdk-8-jre openjdk-11-jdk openjdk-11-jre maven

RUN curl -L -s -S https://github.com/protocolbuffers/protobuf/releases/download/v2.5.0/protobuf-2.5.0.tar.gz -o protobuf-2.5.0.tar.gz \
    && mkdir protobuf \
    && tar xzf protobuf-2.5.0.tar.gz --strip-components 1 -C protobuf && cd protobuf \
    && ./configure \
    && make -j$(nproc) \
    && sudo make install
