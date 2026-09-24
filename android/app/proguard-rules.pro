# Grabbit release rules. -dontobfuscate until a release build is verified
# on-device (native libs + generated serializers are obfuscation-sensitive).
-keep class org.libtorrent4j.swig.libtorrent_jni {*;}
-keep class com.yausername.** { *; }
-keep class org.apache.commons.compress.archivers.zip.** { *; }

# Keep `Companion` object fields of serializable classes.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
