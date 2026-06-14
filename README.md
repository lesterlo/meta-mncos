# meta-mncos

This layer contains vendor-neutral MNCOS distribution, application and image
definitions. Machine layers provide hardware-specific boot and flashing logic.

## Production Flash Image

`mncos-production-flash-image` is the common RAM-resident base image. It
provides the bootable userspace contract but does not assume JTAG, UUU, eMMC,
TFTP, WIC, or any particular SoC vendor.

Machine layers implement the concrete behavior through:

```bitbake
MNCOS_PRODUCTION_FLASH_EXTRA_INSTALL = "product-production-flasher"
MNCOS_PRODUCTION_FLASH_IMAGE_FSTYPES = "cpio.gz product-wrapper"
```

For example, `meta-zuboard` adds the ZynqMP JTAG/TFTP export and guarded eMMC
writer. An i.MX machine layer can append the same image with the packages and
formats required by NXP UUU.

## Reference

- https://github.com/meta-homeassistant
