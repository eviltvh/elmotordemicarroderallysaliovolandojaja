# Add project specific ProGuard rules here.
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*

# kotlinx.serialization
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keep,includedescriptorclasses class com.bynd.esp32dashboard.network.** { *; }
