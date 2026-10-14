$ErrorActionPreference = "Stop"

$JAVA_HOME = "C:\Users\ACER\AppData\Local\Programs\Microsoft\jdk-17.0.10.7-hotspot"
$env:JAVA_HOME = $JAVA_HOME
$env:PATH = "$JAVA_HOME\bin;$env:PATH"

$SDK_DIR = "C:\Users\ACER\AppData\Local\Android\Sdk"
$BUILD_TOOLS = "$SDK_DIR\build-tools\35.0.0"
$PLATFORM_JAR = "$SDK_DIR\platforms\android-36\android.jar"

$AAPT2 = "$BUILD_TOOLS\aapt2.exe"
$D8 = "$BUILD_TOOLS\d8.bat"
$ZIPALIGN = "$BUILD_TOOLS\zipalign.exe"
$APKSIGNER = "$BUILD_TOOLS\apksigner.bat"
$JAVAC = "$JAVA_HOME\bin\javac.exe"
$JAR = "$JAVA_HOME\bin\jar.exe"
$KEYTOOL = "$JAVA_HOME\bin\keytool.exe"

$ROOT = "C:\Users\ACER\.gemini\antigravity\scratch\HabitFlowApkBuild"
Set-Location $ROOT

Write-Host "=== Step 1: Compiling Android Resources with AAPT2 ===" -ForegroundColor Cyan
if (Test-Path "$ROOT\build") { Remove-Item -Recurse -Force "$ROOT\build" }
New-Item -ItemType Directory -Force -Path "$ROOT\build\classes" | Out-Null
New-Item -ItemType Directory -Force -Path "$ROOT\dist" | Out-Null

& $AAPT2 compile --dir "$ROOT\res" -o "$ROOT\build\resources.zip"
if ($LASTEXITCODE -ne 0) { throw "AAPT2 compile failed!" }

Write-Host "=== Step 2: Linking Resources, Manifest and Assets with AAPT2 ===" -ForegroundColor Cyan
& $AAPT2 link -I $PLATFORM_JAR --manifest "$ROOT\AndroidManifest.xml" -A "$ROOT\assets" -o "$ROOT\build\unaligned.apk" --java "$ROOT\src" "$ROOT\build\resources.zip" --auto-add-overlay
if ($LASTEXITCODE -ne 0) { throw "AAPT2 link failed!" }

Write-Host "=== Step 3: Compiling Java Sources with JAVAC ===" -ForegroundColor Cyan
$javaFiles = Get-ChildItem -Path "$ROOT\src" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
Write-Host "Compiling Java files: $($javaFiles -join ', ')"
& $JAVAC -d "$ROOT\build\classes" -cp $PLATFORM_JAR $javaFiles
if ($LASTEXITCODE -ne 0) { throw "JAVAC failed!" }

Write-Host "=== Step 4: Dexing Classes with D8 ===" -ForegroundColor Cyan
$classFiles = Get-ChildItem -Path "$ROOT\build\classes" -Filter "*.class" -Recurse | Select-Object -ExpandProperty FullName
& $D8 --output "$ROOT\build" --lib $PLATFORM_JAR $classFiles
if ($LASTEXITCODE -ne 0) { throw "D8 dexing failed!" }

Write-Host "=== Step 5: Adding classes.dex to APK ===" -ForegroundColor Cyan
Set-Location "$ROOT\build"
& $JAR -uf "$ROOT\build\unaligned.apk" "classes.dex"
if ($LASTEXITCODE -ne 0) { throw "JAR add classes.dex failed!" }
Set-Location $ROOT

Write-Host "=== Step 6: Zipalign APK ===" -ForegroundColor Cyan
& $ZIPALIGN -f -p 4 "$ROOT\build\unaligned.apk" "$ROOT\build\aligned.apk"
if ($LASTEXITCODE -ne 0) { throw "Zipalign failed!" }

Write-Host "=== Step 7: Generating Keystore (if needed) ===" -ForegroundColor Cyan
$KEYSTORE = "$ROOT\habitflow.keystore"
if (-not (Test-Path $KEYSTORE)) {
    & $KEYTOOL -genkeypair -keystore $KEYSTORE -storepass "habitflow123" -alias "habitflow" -keypass "habitflow123" -dname "CN=HabitFlow, OU=App, O=HabitFlow, L=City, S=State, C=US" -validity 10000 -keyalg RSA -keysize 2048
}

Write-Host "=== Step 8: Signing APK with APKSIGNER ===" -ForegroundColor Cyan
$FINAL_APK = "$ROOT\dist\HabitFlow.apk"
& $APKSIGNER sign --ks $KEYSTORE --ks-pass "pass:habitflow123" --ks-key-alias "habitflow" --key-pass "pass:habitflow123" --out $FINAL_APK "$ROOT\build\aligned.apk"
if ($LASTEXITCODE -ne 0) { throw "APKSIGNER sign failed!" }

Write-Host "=== Step 9: Verifying APK Signature ===" -ForegroundColor Cyan
& $APKSIGNER verify --verbose $FINAL_APK
if ($LASTEXITCODE -ne 0) { throw "APK verification failed!" }

Write-Host "=== Step 10: Copying APK to Downloads ===" -ForegroundColor Cyan
$DOWNLOADS_APK = "C:\Users\ACER\Downloads\HabitFlow.apk"
Copy-Item $FINAL_APK -Destination $DOWNLOADS_APK -Force

$apkSize = (Get-Item $FINAL_APK).Length
Write-Host "SUCCESS! APK built and signed!" -ForegroundColor Green
Write-Host "Location 1: $FINAL_APK ($([math]::Round($apkSize / 1KB, 2)) KB)" -ForegroundColor Green
Write-Host "Location 2: $DOWNLOADS_APK ($([math]::Round($apkSize / 1KB, 2)) KB)" -ForegroundColor Green
