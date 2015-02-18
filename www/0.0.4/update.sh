#!/bin/bash
echo "Updating to version 0.0.4 (this one is supposed to exit return code 1 and keep trying)"
echo "Our parameters are (attempts should go up each time):"
echo $@
exit 1
#download 4.3.2.zip
#backup
