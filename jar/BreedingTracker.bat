REM dir: \forge\mcp\src\minecraft\flamefeed\BreedingTracker\jar

cd ..\..\..\..\..
REM dir: \forge\mcp\
call recompile.bat < nul
call reobfuscate.bat < nul

cd reobf\minecraft
REM dir: \forge\mcp\reobf\minecraft
jar cvf %~dp0\BreedingTracker.jar flamefeed\BreedingTracker\src
