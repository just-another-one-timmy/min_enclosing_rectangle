#!/usr/bin/python
#author bunyk
import re
from itertools import islice
from math import sqrt
import sys

class Point(object):
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __str__(self):
        return '(%s, %s)' % (self.x, self.y)

    def __sub__(self, other):
        return Point(self.x - other.x, self.y - other.y)

    def __div__(self, other):
        assert isinstance(other, float)
        return Point(self.x / other, self.y / other)

    def abssq(self):
        return self.x * self.x + self.y * self.y

    def abs(self):
        return sqrt(self.abssq())

    def norm(self):
        return self / self.abs()

    def collinear(self, other):
        return (self.norm() - other.norm()).abs() < 0.1

def file_points(fn):
    point_pattern = re.compile(
        r"^<circle cx='([\d.]+)' cy='([\d.]+)' r='1' stroke='black' />$"
    )
    with open(fn) as f:
        for line in f:
            match = point_pattern.match(line)
            if match:
                yield Point(*map(float, match.groups()))
            else:
                yield line


def write_to_file(fn, line):
    with open(fn, 'a') as f:
        if isinstance(line, Point):
            print >>f, "<circle cx='%s' cy='%s' r='1' stroke='black' />" % (line.x, line.y)
        elif isinstance(line, tuple):
            print >>f, "<line x1='%s' y1='%s' x2='%s' y2='%s' stroke='black' stroke-width='3'/>" % (
                line[0].x,
                line[0].y,
                line[1].x,
                line[1].y,
            )
        else:
            print >>f, line
        
def filter_points(sequence):
    starting = None
    current = None
    for nxt in sequence:
        if not isinstance(nxt, Point):
            yield nxt
            continue
        if not starting:
            starting = nxt
            continue
        if not current:
            current = nxt
            continue
        d = (current - starting)
        nd = (nxt - current)
        if nd.abs() < 3 and nd.collinear(d):
            current = nxt
            continue
        else:
            yield (starting, current)
            starting = nxt
            current = None

import os

points = list(file_points(sys.argv[1]))
print len(points)
#points = islice(points)

map(
    lambda x: write_to_file(sys.argv[2], x),
    filter_points(points)
)
