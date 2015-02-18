DynamicUpdateClient
===================

This branch contains a working version of the update client. You can test it yourself by taking the following steps:

1. Place the contents of the www folder on any web server.

2. In the root folder, build the project by running the build script

```
    ./build.sh
```

3. In the bin folder, run the following:
```
    ./duclient.sh --config
```

Duclient will now be installed in the versions directory in bin. Happy updating!

You're done!