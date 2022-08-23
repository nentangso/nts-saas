#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 \"<version>\"" >&2
    exit 1
fi

NTS_SAAS_VERSION=$1 test-integration/scripts/11-replace-version-saas.sh
