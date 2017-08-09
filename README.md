MTP-Sync
======

## Usage

```
1.0.0
  -d, --device  <arg>   device number
      --help            Show help message
      --version         Show version of this program

Subcommand: storage
      --help   Show help message

Subcommand: storage ls
      --help   Show help message
Subcommand: sync
      --diff-out  <arg>   print diff to file instead of stdout
      --dst  <arg>        destination path
      --no-hidden         exclude hidden files
      --src  <arg>        source path
  -s, --storage  <arg>    storage id
      --help              Show help message

Subcommand: sync to-mtp
      --help   Show help message
Subcommand: sync from-mtp
      --help   Show help message
```

### Example

```
java -jar target/mtp-sync-1.0.0-SNAPSHOT.jar -d 0 sync -s 0 --src ~/Music --dst / --no-hidden to-mtp
```
