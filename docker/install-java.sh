#!/bin/bash
mkdir -p /usr/lib/jvm
cd /usr/lib/jvm || exit
wget https://cdn.azul.com/zulu-embedded/bin/zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf.tar.gz
tar -xzvf zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf.tar.gz
rm zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf.tar.gz
update-alternatives --install /usr/bin/java java /usr/lib/jvm/zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf/bin/java 1
update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf/bin/javac 1