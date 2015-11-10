<img src="http://i.imgur.com/Ig3C9h8.png"/>

DynamicUpdateClient
===================

The dynamic update client is a simple, lightweight but useful update client for linux that watches local versions and remote versions and updates the local system if new updates come available automatically. It uses cron to schedule logic.

The update client is placed on all devices that run modules which could be updated. The client is responsible for selecting, downloading and triggering updates. It was designed to run on the Intel Edison in order to update systems in places without internet, but it can be adapted more widely. 

###Running the Dynamic Update Client:

If you'd like to test it's functionality, simply place the contents of the www folder on any web server.

In the root folder, build the project by running the build script

```
    ./build.sh
```

In the bin folder, run the following:
```
    ./duclient.sh --config
```

You will now be prompted with the configuration where you can enter the Dynamic update host, port, and the user that will run the updates.

After you're done, these settings will be written to a cron job and a configuration file. You can re-run this configuration at any time to add or remove modules.
 
You can edit the modules on your system by editing the bin/versions/config.json file. This makes it extremely easy to add or stop packages in your system.

###How does it work?

First, the update client retrieves the latest index file form the web repository listed in bin/versions/config.json. 

It will then locally retrieve the currently installed version of that module. We will refer to this as from_version

It will then iterate over the versions that the web repository is showing, until it finds our local version. If any other versions are found, the update client will download and execute these updates in the order they are shown in the index file.

For each version that was found in the remote index file that came after our local version, we will execute the following tasks:

Ensure this package should not be ignored (It will be ignored if it has returned 1 in the past, so that you can release a new package to update the system without it getting stuck)

Download the package

Install the package 

Execute the update.sh script with three parameters, the from_version, the version it is updating to (to_version) and the number of times the update has been retried.

When the exit code is 0, the update was successful and the from_version  value should be updated.

When the exit code is 1, the update was not successful and this package should be ignored in the future. As a security, the maximum amount of times an update can fail is 4 times.

When the exit code is 2, the update was not successful but should be attempted again in X minutes. The update client must first do that before continuing with the next update.



##Remote Package Contents
Each shell package must at least contain an update.sh shell script. This shell script will be executed by the update client after extraction and is responsible for the update process.

Packages must follow a particular naming convention. The filename is comprised of the module name (e.g. example-package), followed by a dash (-) and the version label (e.g. 1.0.0). The version label must follow the semantic version pattern int + ‘.’ + int + ‘.’ + int or int + ‘.’ + int. Some examples are: example-package-0.0.1. 

Each package has to contain an update.sh shell script
The update script will be executed as follows:

```
/bin/bash update.sh <from_version> <to_version> <retry_count>

<from_version> is the currently installed version
<to_version> is the version that the update script is expected to install
<retry_count> is the number of times the script was retried, starting at 0
```

The update script has three main responsibilities:
Only start an update if it was explicitly designed to be able to update from_version to to_version
If midway an update it cannot continue, it must revert the changes it has made to that point.
Return the appropriate exit code:
0 - if the update was successful
1 - if the update was not successful, nor can it be expected that it will in the future. An example could be that the from_version is not supported by that update.
2 - if the update was not successful, but it should be tried again later. An example could be that user activity was detected too recently and it wants to avoid interrupting an ongoing process.



##Index File
An index file has to be placed in the server repository root. It must have the exact name that was provided when configuring the module. It's a JSON array that you can add updates to as you go.

```
[
    {
            "src": "http://packages.local/example-package-1.1.1.zip",
            "version": "1.1.1"
    },
  	{
            "src": "http://packages.local/example-package.1.2.zip",
            "version": "1.2"
    }
]
```
The index file is served by a web repository using an HTTP web server. The location of the index file follows the following pattern SERVER_URL + ‘/’ + MODULE_NAME + ‘.json’. For example: http://packages.local/example-package.json

##Questions?
Email pim (at) [name of this company without inc] dot com or tweet me at @PimDeWitte