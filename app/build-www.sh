#!/bin/sh
# Kopiert das fertige Spiel (../index.html, ../musik.mp3) nach www/ und fügt inject.js ein.
set -e
cd "$(dirname "$0")"
rm -rf www && mkdir www
cp ../musik.mp3 www/
node -e '
const fs=require("fs");
const html=fs.readFileSync("../index.html","utf8");
const js=fs.readFileSync("inject.js","utf8");
const i=html.indexOf("<head>");
if(i<0) throw new Error("kein <head> in index.html");
fs.writeFileSync("www/index.html", html.slice(0,i+6)+"<script>"+js+"</script>"+html.slice(i+6));
'
