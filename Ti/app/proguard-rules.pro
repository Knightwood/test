# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#glide图片库
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#使用glide图片库时，target API低于27
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder
#网络上还添加了两行
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{*;}
#WebChromeClient中，openFileChooser()是系统API，混淆了openFileChooser()，会导致无法回调openFileChooser()
-keepclassmembers class * extends android.webkit.WebChromeClient{
    public void openFileChooser(...);
}