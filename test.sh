#!/bin/bash

# Check if a filename was provided
if [ $# -eq 0 ]; then
    echo "Please provide a .zip file as an argument."
    exit 1
fi

# Check if the file exists and is a .zip file
if [ ! -f "$1" ] || [[ "$1" != *.zip ]]; then
    echo "The file does not exist or is not a .zip file."
    exit 1
fi

# Get the filename without extension
filename=$(basename -- "$1")
filename="${filename%.*}"

# Create a temporary directory
temp_dir=$(mktemp -d)

# Unzip the file to the temporary directory
unzip "$1" -d "$temp_dir"

# Create a tar archive of the contents
tar -czf "$temp_dir/$filename.tar.gz" -C "$temp_dir" .

# Convert the tar archive to a base64-encoded file
base64 "$temp_dir/$filename.tar.gz" > "${filename}_encoded.txt"

# Clean up
rm -rf "$temp_dir"

echo "Conversion complete. The encoded file is ${filename}_encoded.txt"
