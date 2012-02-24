#!/usr/bin/perl
# this script converts pbm file (version p1) to the list of points
# in form:
# N x1 y1 x2 y2 ... xn yn
# only black points are in resulting list
use strict;

my $line = <>;
if ($line !~ /P1/) {die "Not a P1 version of PBM format!"}
<>; # skip the comment
$line = <>; chomp($line); # read sizes
my @sizes = split(/ /, $line);
my ($width, $height) = @sizes;
my $currentRow = 0;
my $currentCol = 0;
my @resultX = ();
my @resultY = ();

# reading all characters from file
# print "Height = $height\nWidth = $width\n";
while ($line = <>) {
    chomp($line);
    foreach my $char (split //, $line) {
        if ($char == '1') {
            push(@resultX, $currentCol);
            push(@resultY, $currentRow);
        }
        $currentCol++;
        if ($currentCol == $width) {
            $currentCol = 0;
            $currentRow++;
        }
    }
}

# writing all points back
# print scalar(@resultX) . "\n";
for (my $i = 0; $i < scalar(@resultX); $i++) {
  print "$resultX[$i] $resultY[$i]\n"; 
}

