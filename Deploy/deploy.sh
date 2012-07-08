echo building...;
python build.py;
echo deploying...;
python deploy.py;
echo running...;
# python run.py;
java -jar $(echo $PWD)/out/server.jar 'd' 8080 $(echo $PWD)/out/www