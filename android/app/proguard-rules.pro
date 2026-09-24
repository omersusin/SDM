# Grabbit release rules. -dontobfuscate until a release build is verified
# on-device (native libs + generated serializers are obfuscation-sensitive).
# kotlinx-serialization ships its own R8 rules; no hand rules needed.
-keep class org.libtorrent4j.swig.libtorrent_jni {*;}
-keep class com.yausername.** { *; }
-keep class org.apache.commons.compress.archivers.zip.** { *; }
# compile-time-only annotation processors, absent at runtime
-dontwarn org.immutables.**
