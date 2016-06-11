FROM debian:jessie

RUN awk '$1 ~ "^deb" { $3 = $3 "-backports"; print; exit }' /etc/apt/sources.list > /etc/apt/sources.list.d/backports.list

# Install Oracle Java (auto accept licence)
RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" >> /etc/apt/sources.list \
    && echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" >> /etc/apt/sources.list \
    && apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886 \
    && apt-get update \
    && echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections \
    && apt-get install -y --no-install-recommends oracle-java8-installer \
    && apt-get install -y git-all

RUN apt-get update
RUN apt-get -y install golang
RUN apt-get -y install wget
RUN apt-get -y install unzip

RUN cd ~
RUN wget https://services.gradle.org/distributions/gradle-2.13-bin.zip
RUN unzip gradle-2.13-bin.zip
ENV GRADLE_HOME=$HOME/gradle-2.13
ENV PATH=$PATH:$GRADLE_HOME/bin

ENV JAVA_HOME=/usr/lib/jvm/java-8-oracle

# Clean up
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
