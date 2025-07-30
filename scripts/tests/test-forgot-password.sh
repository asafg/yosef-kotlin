#!/bin/bash

username="${1:-'admin'}"
email="${2:-'admin@gmail.com'}"
password="${3:-'password'}"

echo 'curl -X POST "http://localhost:8081/api/auth/forgot-password" \' >&2
echo '    -H "Content-Type: application/json" \' >&2
echo "    -d '{\"email\": \"admin@gmail.com\"}'" >&2
curl -X POST "http://localhost:8081/api/auth/forgot-password" \
     -H "Content-Type: application/json" \
     -d "${email}"

echo -e '\n'
