#!/usr/bin/perl
# converts every .svg file in current directory
# to the .png file in imgs/
mkdir "imgs";
foreach $file (<*.svg>) {
    if ($file =~ /(.*?).svg/g){
	$rc = system("inkscape -e imgs/" . $1 . ".png $file > /dev/null");
	if ($rc != 0) {
	    print "Could not convert file $file\n";
	} else {
            unlink($file);
	    print $1 . " .. ";
	}
    }
}
print "\n";
