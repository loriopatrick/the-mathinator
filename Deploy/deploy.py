import os

def ex (cmd):
	return os.popen (cmd).read()

LOC = 'out/'

def clean ():
	print 'cleaning...'
	ex('rm -r %s;mkdir %s' % (LOC, LOC))
	print ' ----- done'


def deploy ():
	# ex('cp ../Engine/build/jar/engine.jar %s' % LOC)
	ex('cp ../Server/build/jar/server.jar %s' % LOC)
	ex('mkdir %s/www' % LOC)
	ex('cp -r ../Website/* %swww/' % LOC)

clean()
deploy()