@echo off
taskkill /IM javaw.exe /T /F >nul 2>nul
rem start javaw -Xmx1200M -Djava.net.preferIPv4Stack=true -Djava.encoding=UTF-8 -classpath pms.jar;plugins/*;plugins net.pms.PMS
start javaw -Xmx1290M -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 -classpath update.jar;pms.jar net.pms.PMS
