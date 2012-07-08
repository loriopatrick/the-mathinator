import os

LOC = 'out/'

def ex (cmd):
	return os.popen (cmd).read()

def run ():
	ex('java -jar %sserver.jar' % LOC)

run ()