#!/usr/bin/perl -w
use IO::Socket;
my ($host, $port, $kidpid, $handle, $line);

$host = "localhost";
$port = 13907;
if ((scalar(@ARGV) != 2) || ($ARGV[0] !~ /^[01]$/) || ($ARGV[1] !~ /^[01]$/)) {
  die "srl-client.pl <0|1 doSplit> <0|1 analysis>\n";
}
$doSplit = $ARGV[0];
$analyze = $ARGV[1];

# create a tcp connection to the specified host and port
$handle = IO::Socket::INET->new(Proto     => "tcp",
                                PeerAddr  => $host,
                                PeerPort  => $port)
       or die "can't connect to port $port on $host: $!";

STDOUT->autoflush(1);               # so output gets there right away
$handle->autoflush(1);              # so output gets there right away
#print STDERR "[Connected to $host:$port]\n";

#send the option doSplit and analysis
print $handle "$doSplit\n";
print $handle "$analyze\n";

#wait for ready signal;
$line = <$handle>; #'Ready!'

if ($doSplit eq "c") {
  shutdown($handle,2);
  close($handle);
  exit;
}

# split the program into two processes, identical twins
die "can't fork: $!" unless defined($kidpid = fork());

# the if{} block runs only in the parent process
if ($kidpid) {
    # copy the socket to standard output
    while ($line = <$handle>) {
        print STDOUT $line;
    }
    shutdown($handle,2);
    close($handle);
}
# the else{} block runs only in the child process
else {
    # copy standard input to the socket
    while ($line = <STDIN>) {
        print $handle $line;
    }
    shutdown($handle, 1);
}

