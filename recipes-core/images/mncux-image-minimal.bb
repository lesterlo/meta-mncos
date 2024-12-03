DESCRIPTION = "Minimal image of MNCux"
LICENSE = "MIT"

require include/mncux-image-common.inc

# Function to copy the .WIC.XZ image to export/
do_copy_wic_image() {
    DEST_DIR="${TOPDIR}/export/image"
    mkdir -p ${DEST_DIR}
    cp ${DEPLOY_DIR_IMAGE}/*.wic.xz ${DEST_DIR}/ || true
}

addtask copy_wic_image after do_image_complete before do_build

# Function to copy the SDK
do_copy_sdk() {
    DEST_DIR="${TOPDIR}/export/sdk"
    mkdir -p ${DEST_DIR}
    cp ${SDK_DEPLOY}/*.sh ${DEST_DIR}/ || true
}

addtask copy_sdk after do_populate_sdk before do_build
