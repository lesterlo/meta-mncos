SUMMARY = "nginx reverse-proxy config + filesystem layout for the msys web stack"
DESCRIPTION = "Installs the on-target nginx configuration under \
/opt/monutchee/msys/conf/webserver and prepares the worker-owned config/runtime \
directories the C++ backend (NginxController) needs. The backend owns nginx's \
lifecycle (Option A); the distro nginx.service stays disabled (see the nginx \
bbappend). A per-device self-signed TLS cert is generated on first boot."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://nginx.conf \
    file://index.html \
"

# worker = the unprivileged account nginx + backend run as; nginx = the proxy the
# backend drives; openssl-bin = the CLI used by the first-boot cert generation.
RDEPENDS:${PN} = "worker-user nginx openssl-bin"

S = "${WORKDIR}"

do_install() {
    # Config tree (read-only config + static site) and the TLS runtime dir.
    install -d ${D}/opt/monutchee/msys/conf/webserver/www
    install -d ${D}/opt/monutchee/msys/runtime/webserver/ssl
    install -m 0644 ${WORKDIR}/nginx.conf ${D}/opt/monutchee/msys/conf/webserver/nginx.conf
    install -m 0644 ${WORKDIR}/index.html ${D}/opt/monutchee/msys/conf/webserver/www/index.html
}

FILES:${PN} = " \
    /opt/monutchee/msys/conf/webserver \
    /opt/monutchee/msys/runtime/webserver \
"

# Ownership and the per-device TLS cert must happen on the device — where the
# `worker` account exists and where we want a key unique to each unit — so run on
# first boot, not at rootfs-assembly time.
pkg_postinst_ontarget:${PN}() {
    set -e
    SSLDIR=/opt/monutchee/msys/runtime/webserver/ssl
    if [ ! -f "$SSLDIR/server.crt" ]; then
        openssl req -x509 -nodes -days 3650 -newkey rsa:2048 \
            -keyout "$SSLDIR/server.key" -out "$SSLDIR/server.crt" \
            -subj "/CN=mncos" -addext "subjectAltName=DNS:mncos"
    fi
    # worker owns the config dir (so NginxController can write listen.conf there)
    # and the ssl dir + key (so the worker-run nginx can read the cert).
    # nginx.conf and www/ stay root-owned and read-only to the service.
    chown worker:worker /opt/monutchee/msys/conf/webserver
    chown -R worker:worker "$SSLDIR"
    chmod 600 "$SSLDIR/server.key"
}
