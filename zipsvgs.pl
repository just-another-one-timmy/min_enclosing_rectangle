#!/usr/bin/perl
# this script runs svg_zip.py on every .svg file in current dir
# original file will be replaced with it's 'zipped' version
mkdir "imgs";
foreach $file (<*.svg>) {
    if ($file =~ /(.*?).svg/g){
	$rc = system("./svg_zip.py $file tmp.svg > /dev/null");
	if ($rc != 0) {
	    print "Could not zip file $file\n";
	} else {
            rename("tmp.svg", $file);
	    print "-->" . $1 . "<-- ";
	}
    }
}
print "\n";
