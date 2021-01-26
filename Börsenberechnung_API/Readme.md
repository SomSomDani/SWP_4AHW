
Aufgabe des zweiten Programmes ist es aus einer API einen Börsenkurs zu entnehmen und diesen mit einer Datenbank (mySQL) zuverbinden, bei diesem lese ich die Werte als JSONObject von der API ein.

Zum Installieren des Programmes braucht man die Libraries:
IOUtils : (commons-io:commons-io:2.8.0) über Maven bei IntelliJ IDE
JSONObject : (org.json:json:20151123) üver Maven bei IntelliJ IDE
MySQL Connector für Datenbanken : (mysql-connector-java-8.0.23) Download über MySQL Seite
JavaFX : (openjfx-15.0.1_windows-x64_bin-sdk) Download über die JavaFx Seite

Ausgeführt wird das Programm in dem man das Unternehmen nimmt (IBM,TESLA) mit den jeweiligen Abkürzungen, diese werden in einem JSON Objekt durch das einlesen der API abgespeichert.
In diesem JSON Object werden 20,50 oder 200 Objekte gespeichert je nachdem welche Suchregister für die Aktien man benötigt.
Diese werden in einer Datenbank mit MySQL Workbench abgespeichert. In der Datenbank werden je nach Unternehmen Tabellen angelegt und beschrieben, dass 200 Elemente wird dann nach dem Ausführen überschrieben, falls es neue Daten gibt.
Diese Daten sollen dann mit JavaFx in einem Diagramm dagestellt werden mit dem geleiteten Mittelwert.


