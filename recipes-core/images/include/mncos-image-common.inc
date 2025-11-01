inherit core-image

COMMON_INSTALL = " \
    u-boot-tools \
    u-boot-fw-utils \
    libubootenv \
    openssl \
    jq \
    util-linux \
    "
LIB_COMMON_INSTALL = " \
    net-snmp \
    curl \
    sqlite3 \
    libsodium \
    paho-mqtt-cpp \
    boost \
    "

APP_COMMON_INSTALL = " \
    wireguard-tools \
    lighttpd \
    openssh \
    htop \
    tar \
    xz \
    "

IMAGE_INSTALL:append = " \
    ${COMMON_INSTALL} \
    ${LIB_COMMON_INSTALL} \
    ${APP_COMMON_INSTALL} \
    "