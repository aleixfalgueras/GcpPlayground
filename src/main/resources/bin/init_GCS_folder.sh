#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Use: $0 BUCKET_NAME FOLDER_NAME"
    exit 1
fi

BUCKET_NAME="$1"
FOLDER_NAME="$2"
FOLDER_PATH=gs://$BUCKET_NAME/$FOLDER_NAME

FOLDER_EXISTS=$(gsutil ls "$FOLDER_PATH")

if [[ $FOLDER_EXISTS == *"$FOLDER_PATH"* ]]; then
    echo "Folder $FOLDER_PATH aleardy exists"
else
    echo "Folder $FOLDER_PATH does not exist, I proceed to create it"

    touch ./init.txt
    gsutil cp ./init.txt "$FOLDER_PATH/"
fi