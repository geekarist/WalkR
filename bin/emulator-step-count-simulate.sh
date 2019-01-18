#!/usr/bin/env bash

function step {
sleep 0.1
echo auth $(cat ~/.emulator_console_auth_token)
sleep 0.1
echo sensor set acceleration 0:0:-1
sleep 0.1
echo sensor set acceleration 0:0:1
sleep 0.1
}

for i in $(seq 1 60); do

step | telnet localhost 5554
sleep .5

done