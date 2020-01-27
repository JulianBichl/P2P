# inspired by https://developer.atlassian.com/blog/2015/08/minimal-java-docker-containers/

# FROM alpine:3.7
FROM frolvlad/alpine-glibc
MAINTAINER David Pätzel

# install necessary packages
RUN apk add --update \
  # necessary for running hadoop
  bash \
  coreutils \
  ncurses \
  # necessary for extracting java
  tar

# let's use /opt!
RUN mkdir -p /opt
WORKDIR /opt

# download and unarchive Java
RUN wget http://cdn.azul.com/zulu/bin/zulu8.25.0.1-jdk8.0.152-linux_x64.tar.gz
RUN tar xzf zulu8.25.0.1-jdk8.0.152-linux_x64.tar.gz
RUN ln -s /opt/zulu8.25.0.1-jdk8.0.152-linux_x64 /opt/jdk

# remove unneeded Java stuff
RUN rm -rf /opt/jdk/*src.zip \
           /opt/jdk/lib/missioncontrol \
           /opt/jdk/lib/visualvm \
           /opt/jdk/lib/*javafx* \
           /opt/jdk/jre/lib/plugin.jar \
           /opt/jdk/jre/lib/ext/jfxrt.jar \
           /opt/jdk/jre/bin/javaws \
           /opt/jdk/jre/lib/javaws.jar \
           /opt/jdk/jre/lib/desktop \
           /opt/jdk/jre/plugin \
           /opt/jdk/jre/lib/deploy* \
           /opt/jdk/jre/lib/*javafx* \
           /opt/jdk/jre/lib/*jfx* \
           /opt/jdk/jre/lib/amd64/libdecora_sse.so \
           /opt/jdk/jre/lib/amd64/libprism_*.so \
           /opt/jdk/jre/lib/amd64/libfxplugins.so \
           /opt/jdk/jre/lib/amd64/libglass.so \
           /opt/jdk/jre/lib/amd64/libgstreamer-lite.so \
           /opt/jdk/jre/lib/amd64/libjavafx*.so \
           /opt/jdk/jre/lib/amd64/libjfx*.so

# download and unarchive hadoop
RUN wget http://mirror.23media.de/apache/hadoop/core/hadoop-3.0.3/hadoop-3.0.3.tar.gz
RUN tar xzf hadoop-3.0.3.tar.gz
RUN ln -s /opt/hadoop-3.0.3 /opt/hadoop

# remove big unneeded zip files
RUN rm /opt/hadoop-3.0.3.tar.gz
RUN rm /opt/zulu8.25.0.1-jdk8.0.152-linux_x64.tar.gz

# set environment variables
ENV JAVA_HOME /opt/jdk
ENV HADOOP_HOME /opt/hadoop
ENV PATH ${PATH}:${JAVA_HOME}/bin

# add corpus directory and run script
COPY corpus /opt/corpus

# use bash
ENTRYPOINT ["bash"]
