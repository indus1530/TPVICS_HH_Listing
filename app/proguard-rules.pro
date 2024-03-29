# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\hassan.naqvi\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Fragment
#-keep public class * extends android.support.v4.app.Fragment
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends java.lang.Exception
#
#-keep public class * extends android.view.View {
#      public <init>(android.content.Context);
#      public <init>(android.content.Context, android.util.AttributeSet);
#      public <init>(android.content.Context, android.util.AttributeSet, int);
#      public void set*(...);
#}
#
#-keepclasseswithmembers class * {
#   public <init>(android.content.Context, android.util.AttributeSet);
#}
#
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}
#
#-keepclassmembers class * extends android.content.Context {
#    public void *(android.view.View);
#    public void *(android.view.MenuItem);
#}
#
#-keepclassmembers class * implements android.os.Parcelable {
#    static ** CREATOR;
#}
#
#-keepclassmembers class **.R$* {
#    public static <fields>;
#}
#
#-keepclassmembers class * {
#    @android.webkit.JavascriptInterface <methods>;
#}
#
#-dontwarn android.support.design.**
#
#-dontwarn javax.annotation.**
#
#-keep class java.io.** { *; }
#-keep class io.reactivex.** { *; }
#-keep class com.wang.** { *; }
#-keep class android.support.** { *; }
#-keep class com.utflabs.base.** { *; }
#-keep interface android.support.** { *; }
#-keep class com.utflabs.base.models.** { *; }
#-dontwarn javax.annotation.Nullable
#-dontwarn javax.annotation.ParametersAreNonnullByDefault
#-keep class **$$ViewBinder { *; }
#-renamesourcefileattribute SourceFile
#
#-keepattributes SourceFile,LineNumberTable
#
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
#
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#
#-keepattributes *Annotation*
#-keepattributes Exceptions, Signature, InnerClasses
#-keep class * extends java.util.ListResourceBundle {
#    protected Object[][] getContents();
#}
-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }