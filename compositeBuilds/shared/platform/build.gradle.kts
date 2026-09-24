plugins{
    kotlin("multiplatform")
}
repositories{
    mavenCentral()
}
kotlin {
    jvm("desktop")
}
version=1
// artifact coordinates stay ir.amirab (Maven identity + composite substitution)
group="ir.amirab.util"
