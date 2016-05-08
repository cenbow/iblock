#!/usr/bin/env python
import os
from BaseHTTPServer import HTTPServer
from SimpleHTTPServer import SimpleHTTPRequestHandler

ROUTES = [
    ('/list', '/')
]
EXCLUDES = [
    '/app','/bower_components','/css','/filter','/img','/includes','/service'
]

class MyHandler(SimpleHTTPRequestHandler):
    def translate_path(self, path):

        root = os.getcwd()
        # default root -> cwd
        for patt in EXCLUDES:
            if path.startswith(patt):
                return root+path

        print "orginal path:"+path
        # look up routes and get root directory
        for patt, rootDir in ROUTES:
            if path.startswith(patt):                
                path = path[len(patt)+1:]
                root = root + rootDir
                break
        # new path
        print "root"+root
        print "path"+path
        print os.path.join(root, path) 
        print root+path 
        return root+path
        #return os.path.join(root, path)    

if __name__ == '__main__':
    httpd = HTTPServer(('127.0.0.1', 8000), MyHandler)
    httpd.serve_forever()