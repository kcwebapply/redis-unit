Redis Unit
=================

Redis Unit requires Maven 3.0 or higher and Java 8 at runtime.

If using Linux OS, you need to set kernel parameters to run Redis.
    sysctl -w vm.overcommit_memory=1
    sysctl -w net.core.somaxconn=1024

### First bootstrap and download the wrapper ###
    cd redis_unit_source_dir
    ./mvnw

### Running unit tests ###
    ./mvnw test

