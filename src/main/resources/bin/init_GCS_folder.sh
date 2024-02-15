#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Use: $0 GCS_FOLDER_PATH"
    exit 1
fi

GCS_FOLDER_PATH="$1"
INIT_FILE_NAME=init.txt

touch ./"$INIT_FILE_NAME"
gsutil cp ./"$INIT_FILE_NAME" gs://"$GCS_FOLDER_PATH"/
