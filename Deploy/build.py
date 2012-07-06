import os
import shutil

LOC = 'out/'

def ex (cmd):
	return os.popen (cmd).read()

def clean ():
	print 'cleaning...'
	ex('rm -r %s' % LOC)
	ex('mkdir %s' % LOC)
	print ' ----- done'

def buildEngine ():
	print 'building...'
	ex('echo $(cd ../Engine; ant clean; ant build; ant jar)')
	print ' ------ done'

	print 'copying...'
	ex('cp ../Engine/build/jar/engine.jar %s' % LOC)
	print ' ------ done'


clean()
buildEngine()