#!/bin/bash

cd $(dirname "$0") || exit

curl \
  -X POST \
  -H "Content-type: application/json" \
  http://localhost:5000/events/RHCP \
  -d '{"tickets": 15}'

curl \
  -X POST \
  -H "Content-type: application/json" \
  http://localhost:5000/events/RHCP/tickets \
  -d '{"tickets": 2}'
