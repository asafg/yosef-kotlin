#!/bin/bash

email="${1}"
new_password="${2}"
reset_token="${3}"

echo 'curl -X POST "http://localhost:8081/api/auth/change-password" \' >&2
echo '    -H "Content-Type: application/json" \' >&2
echo "    -d '{\"email\": \"${email}\", \"newPassword\": \"${new_password}\", \"resetToken\": \"${reset_token}\"}'" >&2
curl -X POST "http://localhost:8081/api/auth/change-password" \
     -H "Content-Type: application/json" \
     -d "{\"email\": \"${email}\", \"newPassword\": \"${new_password}\", \"resetToken\": \"${reset_token}\"}" >&2
echo -e '\n'
