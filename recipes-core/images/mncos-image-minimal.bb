DESCRIPTION = "Minimal image of Monutchee OS"
LICENSE = "MIT"

require include/mncos-image-common.inc

# Function to copy required files to export/tftpboot
do_copy_tftpboot() {
    set -eu

    DEST_DIR="${TOPDIR}/export/tftpboot"
    mkdir -p "${DEST_DIR}"

    echo "==> Copying TFTP boot files to ${DEST_DIR}"

    # Track success/failure
    local retVal=0

    # Helper: copy and handle missing files gracefully
    copy_or_warn() {
        local src="$1"
        local dst="$2"

        if [ -e "${src}" ]; then
            cp -v "${src}" "${dst}" || retVal=1
        else
            echo "WARN: Missing ${src}" >&2
            retVal=1
        fi
    }

    # FPGA bitstream (manual copy if needed)
    # Example: copy_or_warn "${DEPLOY_DIR_IMAGE}/fpga.bit" "${DEST_DIR}"

    # PMU firmware
    copy_or_warn "${DEPLOY_DIR_IMAGE}/pmu-firmware-${MACHINE}.elf" "${DEST_DIR}/pmufw.elf"

    # FSBL
    copy_or_warn "${DEPLOY_DIR_IMAGE}/fsbl-${MACHINE}.elf" "${DEST_DIR}/fsbl.elf"

    # System Device Tree
    copy_or_warn "${DEPLOY_DIR_IMAGE}/system.dtb" "${DEST_DIR}/system.dtb"

    # TF-A BL31 (sysroots or deploy)
    copy_or_warn "${DEPLOY_DIR_IMAGE}/arm-trusted-firmware.elf" "${DEST_DIR}/tfa.elf"

    # U-Boot
    copy_or_warn "${DEPLOY_DIR_IMAGE}/u-boot.elf" "${DEST_DIR}/uboot.elf"

    # Kernel Image
    copy_or_warn "${DEPLOY_DIR_IMAGE}/Image" "${DEST_DIR}/kernel"

    # Rootfs
    copy_or_warn "${DEPLOY_DIR_IMAGE}/${PN}-${MACHINE}.rootfs.cpio" "${DEST_DIR}/rootfs.cpio"

    # Boot script
    copy_or_warn "${DEPLOY_DIR_IMAGE}/boot.scr" "${DEST_DIR}/boot.scr"

    # Summary
    if [ ${retVal} -eq 0 ]; then
        echo "✅ All required files copied successfully to ${DEST_DIR}"
    else
        echo "⚠️  Some files were missing or failed to copy — check logs above."
    fi

    return ${retVal}
}
#Ensures the task always runs.
do_copy_tftpboot[nostamp] = "1"
addtask copy_tftpboot after do_deploy before do_build


# Function to copy the .WIC.XZ image to export/image
do_copy_wic_image() {
    DEST_DIR="${TOPDIR}/export/image"
    mkdir -p ${DEST_DIR}
    cp ${DEPLOY_DIR_IMAGE}/*.wic.xz ${DEST_DIR}/ || true
}
#Ensures the task always runs.
do_copy_wic_image[nostamp] = "1"
addtask copy_wic_image after do_image_complete before do_build

# Function to copy the SDK
do_copy_sdk() {
    DEST_DIR="${TOPDIR}/export/sdk"
    mkdir -p ${DEST_DIR}
    cp ${SDK_DEPLOY}/*.sh ${DEST_DIR}/ || true
}
#Ensures the task always runs.
do_copy_sdk[nostamp] = "1"
addtask copy_sdk after do_populate_sdk
