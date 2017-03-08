# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\DELL\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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
#    Modif.1
#    Se añade código para poder utilizar el protocolo MQTT en Android
#    1 de Febrero de 2017
#    Bibliografía:
#http://stackoverflow.com/questions/35796144/progaurd-issue-warningignoring-innerclasses-attribute-for-an-anonymous-inner-c/35798565#35798565
#http://stackoverflow.com/questions/3308010/what-is-the-ignoring-innerclasses-attribute-warning-output-during-compilation/3308059#3308059
-keepattributes EnclosingMethod #Se usa y se depura, luego se comenta y se vuelve a depurar
                                 #y se vuelve a ejecutar exitosamente desde el celular
