#!/bin/bash
cd submodules/Aloha-Editor/build
ant all
cp  -r out/aloha-nightly/aloha ../../../website/war/
