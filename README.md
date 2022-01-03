# P2P File Sharing Program
## Requirements
### Apache Maven

If not already installed, it can be installed using the following steps:
1. Update the package index:
```
$ sudo apt update
```
2. Install Maven:
```
$ sudo apt install maven
```
3. Verify installation:
```
$ mvn -version
```
### LogMeIn Hamachi
Install the relevant version using your package manager, or head over to www.vpn.net to download and install the relevant version.

## Other prerequisites
1. Log into the hamachi network
```
$ sudo hamachi login
```
2. If you have not yet created a network, use the following command to create one:
```
$ sudo hamachi create <network name> <password>
```
**NOTE** This will automatically connect you to your network that you created.
3. Others can join your network, using:
```
$ sudo hamachi join <network name> <password>
```
4. Disable firewall, using:
```
$ sudo ufw disable
```
5. Ensure that its disabled, using:
```
$ sudo ufw status
```
**NOTE:** you can enable your firewall again, using:
```
$ sudo ufw enable
```

## How to run
1. Compile, using:
```
$ mvn compile
```
2. The person who is hosting the vpn on hamachi needs to start the server using:
```
$ mvn exec:java -Dexec.mainClass=server.ServerMain
```
3. Once the server has started, everyone can start their client using:
```
$ mvn exec:java -Dexec.mainClass=client.ClientMain
```
**NOTE:** Clients will be prompted to enter the server address they would like to connect to. They need to use the 
hamachi IP address of the person who is hosting the server. 
* To get the IP address of the person who is hosting the server use the following command and copy the hamachi IP 
address of that person:
```
$ sudo hamachi list
```
* The person who is hosting the server can use their own hamachi IP address, which can be obtained, using:
```
$ sudo hamachi
```
