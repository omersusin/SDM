package grab.bit.downloader.exception

import grab.bit.downloader.part.DownloadPart

class PartTooManyErrorException(
    part: DownloadPart,
    override val cause: Throwable
) : Exception(
        "this part $part have too many errors",
    cause,
)
