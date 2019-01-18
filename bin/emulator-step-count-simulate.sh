#!/usr/bin/env bash

function step {
echo "Step: $(date)"
telnet localhost 5554 &> /dev/null << EOF
auth $(cat ~/.emulator_console_auth_token)
sensor set acceleration 0:0:0
sensor set acceleration 99:99:99
EOF
}

for i in $(seq 1 60); do

step
sleep 1

done