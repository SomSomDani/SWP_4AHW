Was soll dieses Programm nützliches machen?

Der Nutzen diese Programmes dient dazu aus einer API die Werte einer bestimmten Aktie aus Amerika wie IBM, Apple, Amazon, ... einzulesen via dem Firmenkürzel (AAPL, TSLA, ADM, ...). Aus diesen Aktienwerten wird ein gleitender Durchschnitt von 200 berechnet, welcher und dann die Information geben kann wie die derzeitige wirtschafliche Struktur dieses Unternehmen besitzt. 

Die API Daten werden zu allerest durch ein JSONObjekt in die IDE eingelesen, vor dort aus stellen wir durch einen MySQL-Driver eine Verbindung mit dem MySQL-Server fest. Durch die IDE werden dann je nach Aktie eine Tabelle erstellt, welche wir dann mit den sogenannten Close-Werten (Werte der Aktie) befüllen, zugleich berechnen wir den gleitenden Durchschnitt von 200, dieser wird in einer separaten Datenbank abgespeichert.

Nachdem die Daten in der Datenbank sich befinden, entnehmen wir die Daten für den Graphen. Diese Daten werden nachdem erneuten Ausführen kontrolliert, falls ein neuer Wert auftaucht wird dieser hinzugefügt.
Der Graph wird durch die Daten der Close-Werte und dem gleitenden Durchschnitt gezeichnet. Wir legen auch dort fest, falls der letzte Close-Wert höher als der gleitende Durchschnitt ist, geht die Hintergrundfarbe des Charts auf grün, wenn der Close-Wert niedriger ist, bekommt der Chart die Farbe rot.

Was braucht man um das Programm auszuführen?
1. Man muss sich bei https://www.alphavantage.co/ anmelden und dann einen Schlüssel anfordern mit diesem kann man die Aktien-Werte ansehen und entnehmen.
2. JsonObject, damit man die Daten aus der Daten entnehmen kann, im meinem Fall habe ich es über Maven gemacht mit: org.json:json:20151123
3. Wir brauchen einen MySQL-Driver der als Connector zwischen dem Programm und der Datenbank fungiert, diesen kann man sich über die MySQL-Seiter herunterladen: mysql-connector-java-8.0.23 abhängig von der Version.
4. Commons.IO über Maven: commons-io:commons-io:2.8.0
5. Als allerletztes kommt man die letzte Library, welche man benötigt nähmlich XYChart, xchart-3.8.0 welches in einem Zip-Ordner im Repository sich befindet.

Nachdem alle Libraries hinzugefügt sind und man den Key besitzt kann man das Programm ausführen. Man muss nur das MySQL-Passwort und den User ändern.
Nun können Sie das API auslesen, berechnen und ausgeben lassen. Die Graphen werden die gewünschte Aktie mit korregiertem Close Werten ausgeben.

Nachdem die Daten täglich upgedatet durch ein Artifakt, werden diese durch das Backtesting bei 200er Trading, BuyAndHold und Trading 200 + 3%, bei den jeweiligen Aktien (PTC,ATVI,TSLA,AMZN,IBM) jedoch sind auch (AAPL,ADM,FORD) noch in der Datenbank. Das Backtesting sollte zeigen wenn wir 100.000 Euro in die jeweiligen Aktien investieren, bei welcher Strategie man am meisten Gewinn macht, wenn das Startdatum der 01.01.2010 ist.
