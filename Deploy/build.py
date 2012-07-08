import os
import shutil


def ex (cmd):
	return os.popen (cmd).read()

def buildEngine ():
	print 'building engine...'
	ex('echo $(cd ../Engine; ant clean; ant build; ant jar)')
	print ' ------ done'

	print 'copying engine.jar to server...'
	ex('cp ../Engine/build/jar/engine.jar ../Server/lib')
	print ' ------ done'

def buildServer ():
	print 'building server...'
	ex('echo $(cd ../Server; ant clean; ant build; ant jar)')
	print ' ------ done'

buildEngine()
buildServer()