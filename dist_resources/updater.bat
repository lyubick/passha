@echo off

:delete
    del pasSHA.jar
    if exist "pasSHA.jar" goto :delete

:rename
    rename pasSHA.jar~ pasSHA.jar

:start
start javaw -jar pasSHA.jar

:clean
rem del updater.bat

:stop
