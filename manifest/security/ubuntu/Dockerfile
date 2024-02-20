FROM ubuntu:20.04

RUN apt-get update && \
    apt-get -y install sudo && \
    echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

RUN adduser --disabled-password --gecos '' testuser && \
    adduser testuser sudo

USER testuser
