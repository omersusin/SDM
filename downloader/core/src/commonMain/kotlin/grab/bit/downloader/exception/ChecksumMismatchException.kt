package grab.bit.downloader.exception

import java.io.IOException

class ChecksumMismatchException(
    val expected: String,
    val actual: String,
) : IOException("checksum mismatch: expected=$expected actual=$actual")
