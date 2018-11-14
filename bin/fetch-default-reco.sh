#!/bin/bash

set -eux

token=$1
base_dir=$(dirname $0)/..

cd ${base_dir}

for tempo in $(seq 70 10 200) ; do
    let min_tempo=tempo-5
    let max_tempo=tempo+5

    seed_artists=$(curl -X "GET" "https://api.spotify.com/v1/playlists/37i9dQZEVXbMDoHDwVN2tF?fields=tracks(items(track.artists(id%2Cname)))" -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer $token" | jq -r '.tracks.items[].track.artists[].id' | head -n 5 | tr '\n' ',' | sed 's/,$//g')

    curl -X "GET" "https://api.spotify.com/v1/recommendations?market=US&seed_artists=$seed_artists&min_energy=0.4&min_popularity=50&min_tempo=$min_tempo&max_tempo=$max_tempo" -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer $token" -o app/src/main/res/raw/default-reco-${tempo}.json
done

