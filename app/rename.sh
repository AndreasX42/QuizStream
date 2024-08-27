#!/bin/bash

# Find all .ts files in the current directory and its subdirectories
find . -type f -name "*.ts" | while read -r file; do
  # Use sed to replace .css with .scss within the file
  sed -i 's/\.css/\.scss/g' "$file"
done

echo "All .css references have been renamed to .scss in .ts files"
