#!/bin/bash

username="${1:-'admin'}"
email="${2:-'admin@gmail.com'}"
password="${3:-'password'}"

echo 'curl -X POST "http://localhost:8081/api/auth/signup" \' >&2
echo '    -H "Content-Type: application/json" \' >&2
echo "    -d '{\"username\": \"${username}\",\"email\": \"admin@gmail.com\",  \"password\": \"${password}\"}'" >&2
curl -X POST "http://localhost:8081/api/auth/signup" \
     -H "Content-Type: application/json" \
     -d "{\"username\": \"${username}\",\"email\": \"${email}\",  \"password\": \"${password}\"}"

echo -e '\n'
