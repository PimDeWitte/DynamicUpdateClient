DynamicUpdateClient
===================

This branch contains a working version of the update client. You can test it yourself by taking the following steps:

1. Place the contents of the www folder on any web server.

In the bin folder, run the following:
```
    ./duclient.sh --config
```

Duclient will be installed in the home directory for the user that runs the script and start running using cron.

You're done!