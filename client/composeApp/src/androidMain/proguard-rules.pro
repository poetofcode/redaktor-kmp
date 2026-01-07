-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }

-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}