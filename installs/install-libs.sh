mvn install:install-file -DgroupId=net.sf.saxon \
 -DartifactId=saxon-dom -Dversion=9.0 -Dpackaging=jar \
 -Dfile=./lib/saxon-9.0.jar

 
 mvn install:install-file -DgroupId=cfe-factura-render \
 -DartifactId=cfe-factura-render -Dversion=2.36-SNAPSHOT-ext -Dpackaging=jar \
 -Dfile=./lib/cfe-factura-render-2.36-SNAPSHOT-ext.jar

  mvn install:install-file -DgroupId=cfe-factura-render \
 -DartifactId=tidy -Dversion=1.0 -Dpackaging=jar \
 -Dfile=./lib/Tidy-1.0.jar
 
