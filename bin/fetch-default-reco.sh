#!/bin/bash

set -eux

token=$1
base_dir=$(dirname $0)/..

cd ${base_dir}

for tempo in $(seq 70 2 200) ; do
    let min_tempo=tempo-1
    let max_tempo=tempo+1

    seed_artists=$(curl -X "GET" "https://api.spotify.com/v1/playlists/7KMtxb9kh3TtVvJTdLtJ2K?fields=tracks(items(track.artists(id%2Cname)))" -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer $token" | jq -r '.tracks.items[].track.artists[].id' | head -n 5 | tr '\n' ',' | sed 's/,$//g')

    curl -X "GET" "https://api.spotify.com/v1/recommendations?market=US&seed_artists=$seed_artists&min_energy=0.4&min_popularity=50&min_tempo=$min_tempo&max_tempo=$max_tempo" -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer $token" -o app/src/main/res/raw/default_reco_${tempo}.json
done

