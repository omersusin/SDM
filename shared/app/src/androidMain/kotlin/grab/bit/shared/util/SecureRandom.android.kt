package grab.bit.shared.util

import java.security.SecureRandom

private val secureRandom = SecureRandom()

actual fun secureRandomNextInt(bound: Int): Int {
    return secureRandom.nextInt(bound)
}
