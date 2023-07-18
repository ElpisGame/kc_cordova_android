# kc_cordova_android
knife crash cordova-android

cordova build android

cordova build --release android

cordova build android --release -- --packageType=apk

cordova build android --prod --release --buildConfig=build.json

jarsigner -verbose -keystore gameley.keystore -signedjar app-release-signed.apk app-release-unsigned.apk gameley.keystore

./apksigner sign -ks gameley.keystore --v2-signing-enabled true --ks-key-alias gameley --out signed.apk app-release-unsigned.apk
