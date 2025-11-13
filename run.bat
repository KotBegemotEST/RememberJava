@echo off
chcp 65001 >nul
java -Dfile.encoding=UTF-8 -cp "target/quizgame-1.0-SNAPSHOT.jar;target/dependency/*" ee.anton.quizgame.App
