# Vulnerability Analysis

## A1:2017 Injection

### Description

Met een injectie wordt onder anderen een SQL injection bedoeld. Een SQL injection wordt gebruikt om ongewenste
commando’s uit te voeren op een database. Dit wordt gedaan door bijvoorbeeld een SQL-clause eerder af te sluiten dan
gepland en daar dus misbruik van te maken. Dit dan gebeuren doordat de invoer van een gebruiker niet escaped is of niet
op een veilige manier verwerkt wordt naar de database.

### Risk

Het risico binnen dit project is niet zo groot omdat binnen dit project gebruik wordt gemaakt van Hibernate. Hibernate
zorgt er zelf al voor dat deze SQL injecties vrijwel niet mogelijk zijn.

### Counter-measures

Binnen dit project gebruik wordt gemaakt van Hibernate. Hibernate zorgt er zelf al voor dat deze SQL injecties vrijwel
niet mogelijk zijn zolang je niet zelf sql queries gaat schrijven. Verder kun je dit voorkomen door gebruik te maken van
parameterized queries.

## A4:2017-XML External Entities (XXE)

### Description

XML external entities gaat over het gebruik van oude XML-processors. Aanvallers kunnen misbruik maken van kwetsbare
XML-processors als ze XML kunnen uploaden of vijandige inhoud kunnen opnemen in een XML-document, waarbij ze misbruik
maken van kwetsbare code, afhankelijkheden of integraties.

### Risk

Deze fouten kunnen worden gebruikt om gegevens te vergaren, het interne systemen te scannen en andere aanvallen uit te
voeren.

### Counter-measures

Gebruik waar mogelijk minder complexe gegevensformaten zoals JSON, en vermijd serialisering van gevoelige gegevens. Ook
is het erg belangrijk om alle XML-processors goed up to date te houden en invoervalidatie en filtering toe te passen.

## A9:2017 Using Components with Known Vulnerabilities

### Description

In een dependency van de applicatie kunnen veiligheids problemen zitten, zelfs binnen de dependencies van de
dependencies.

### Risk

Het risico hiervan is dat outdated depencencies een gevaar kunnen zijn voor de applicatie. Door middel van deze
dependencies kunnen er beveiligings lekken ontstaan zonder dat je dit door hebt.

### Counter-measures

Binnen dit project wordt er gebruik gemaakt van bijvoorbeeld dependabot. Deze bot kijkt automatisch naar
veiligheidsproblemen binnen de gebruikte dependencies en de dependencies van de dependencies. Ook wordt er in dit
project gebruik gemaakt van SonarCloud die in de CI pipeline ook security checks uitvoert op dit project en deze
rapporteert.
