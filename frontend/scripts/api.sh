#!/bin/sh
set -eu

if [ "$#" -gt 1 ] || { [ "$#" -eq 1 ] && [ "$1" != "--check" ]; }; then
  echo 'Usage: api.sh [--check]' >&2
  exit 2
fi

cd "$(dirname "$0")/.."
api_tmp=$(mktemp -d "${TMPDIR:-/tmp}/2idiotz-api.XXXXXX")
trap 'rm -rf "$api_tmp"' 0
trap 'exit 1' HUP INT TERM

(cd ../backend && ./gradlew --no-daemon exportOpenApi)
# Normalize JSON before formatting, preserving the committed contract's layout.
prettier --parser json-stringify < ../backend/build/openapi/openapi.json > "$api_tmp/raw.json"
prettier --stdin-filepath openapi.json < "$api_tmp/raw.json" > "$api_tmp/openapi.json"
API_INPUT="$api_tmp/openapi.json" API_OUTPUT="$api_tmp/generated" openapi-ts

if [ "${1-}" = "--check" ]; then
  if ! diff -u openapi.json "$api_tmp/openapi.json" ||
     ! diff -ru src/lib/api/generated "$api_tmp/generated"; then
    echo 'API files are stale or missing. Run pnpm api:generate.' >&2
    exit 1
  fi
  echo 'API contract and client are synchronized.'
else
  cp "$api_tmp/openapi.json" openapi.json
  rm -rf src/lib/api/generated
  mkdir -p src/lib/api
  cp -R "$api_tmp/generated" src/lib/api/generated
  echo 'API client regenerated.'
fi
