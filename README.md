DynamicUpdateClient
===================

Running the Dynamic Update Client:

Place the contents of the www folder on any web server.

In the root folder, build the project by running the build script

```
    ./build.sh
```

In the bin folder, run the following:
```
    ./duclient.sh --config
```

Duclient will now be installed in the versions directory in bin. Happy updating!

##Update Client
The update client is placed on all devices that run modules which could be updated. The client is responsible for selecting, downloading and triggering updates.

###What does it do? 
The following steps must be taken at a specific interval:
Retrieve the latest index file for each module
curl http://packages.local/OpenMRS-module.json
Locally retrieve the currently installed version of that module. We will refer to this as from_version
Iterate through the list of packages until the from_version is found in the list
For each following item in the list, starting from the next position:
Ensure this package should not be ignored
Download the package
Install the package
Execute the update.sh script with three parameters, the from_version, the version it is updating to (to_version) and the number of times the update has been retried.
When the exit code is 0, the update was successful and the from_version  value should be updated.
When the exit code is 1, the update was not successful and this package should be ignored in the future
When the exit code is 2, the update was not successful but should be attempted again in five minutes. The update client must first do that before continuing with the next update.



##Contents
Each shell package must at least contain an update.sh shell script. This shell script will be executed by the update client after extraction and is responsible for the update process.

Packages must follow a particular naming convention. The filename is comprised of the module name (e.g. example-package), followed by a dash (-) and the version label (e.g. 1.0.0). The version label must follow the semantic version pattern int + ‘.’ + int + ‘.’ + int or int + ‘.’ + int. Some examples are: example-package-0.0.1. 

Each package has to contain an update.sh shell script
The update script will be executed as follows:
/bin/bash update.sh <from_version> <to_version> <retry_count>

<from_version> is the currently installed version
<to_version> is the version that the update script is expected to install
<retry_count> is the number of times the script was retried, starting at 0

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
