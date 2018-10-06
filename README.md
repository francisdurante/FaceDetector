# faceDetection


https://azure.microsoft.com/en-us/services/cognitive-services/
Face - preview
add library in android studio (mavenCentral()) app
Module add compile 'com.microsoft.projectoxford.face:1.0.0'
add inter-permission- Manifest


Manifest merger failed : uses-sdk:minSdkVersion 19 cannot be smaller than version 22 declared in library [com.microsoft.projectoxford:face:1.4.3] C:\Users\D\.gradle\caches\transforms-1\files-1.1\face-1.4.3.aar\96306e52661df94b88a42e3bfaf876da\AndroidManifest.xml as the library might be using APIs not available in 19
	Suggestion: use a compatible library with a minSdk of at most 19,
		or increase this project's minSdk version to at least 22,
		or use tools:overrideLibrary="com.microsoft.projectoxford.face" to force usage (may lead to runtime failures)
