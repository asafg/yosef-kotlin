#!/bin/bash

#!/bin/bash

usernameOrEmail="${1:-'admin'}"
password="${2:-'password'}"

echo 'curl -X POST "http://localhost:8081/api/auth/signin" \' >&2
echo '    -H "Content-Type: application/json" \' >&2
echo "    -d '{\"usernameOrEmail\": \"${usernameOrEmail}\", \"password\": \"${password}\"'" >&2

curl -X POST "http://localhost:8081/api/auth/signin" \
     -H "Content-Type: application/json" \
     -d "{\"usernameOrEmail\": \"${usernameOrEmail}\", \"password\": \"${password}\"}"

echo -e '\n'
