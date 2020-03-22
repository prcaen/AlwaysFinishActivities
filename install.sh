#!/usr/bin/env bash

adb install -r build/outputs/apk/release/app-release.apk

./grant_permissions.sh