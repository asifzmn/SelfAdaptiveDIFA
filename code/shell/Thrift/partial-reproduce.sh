#/bin/bash 

# 1. installation-time study
echo "============= Installation-time compatibility study ========="
echo

python classify_failed_installation_effect_sdk.py installationLogs/Emulator-19/result201020.txt

# 2. run-time study
echo "============= Run-time compatibility study ========="
echo

getanysdkall.sh samples

python checkallsdk.py samples > res_run.txt

python classify_failed_running_effect_minsdk.py runtimeLogs/Emulator-21/benign-2010/






