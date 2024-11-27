DESCRIPTION = "Minimal image of MNCux"
LICENSE = "MIT"

require include/mncux-image-common.inc

do_copy_wic_image() {
    DEST_DIR="${TOPDIR}/export"
    mkdir -p ${DEST_DIR}
    cp ${DEPLOY_DIR_IMAGE}/*.wic.xz ${DEST_DIR}/ || true
}

addtask copy_wic_image after do_image_complete before do_build
