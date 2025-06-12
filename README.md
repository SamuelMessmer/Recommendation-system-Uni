Produkt-Empfehlungssystem
Dieses Repository enthält den Code für ein flexibles Produkt-Empfehlungssystem. Es wurde entwickelt, um Benutzern dabei zu helfen, schnell relevante und interessante Produkte basierend auf einem Referenzprodukt zu finden.

Kernfunktionen:
Personalisierte Empfehlungen: Benutzer können ein spezifisches Produkt angeben, und das System ermittelt daraufhin verwandte Produkte als Empfehlungen.
Anpassbare Empfehlungsstrategien: Das System bietet verschiedene Empfehlungsstrategien, die individuell ausgewählt oder miteinander kombiniert werden können. Dies ermöglicht eine maßgeschneiderte Anpassung an die spezifischen Bedürfnisse und Präferenzen des Benutzers.
Graphenbasierte Datenverwaltung: Ein besonderes Merkmal dieses Systems ist die Speicherung und Verwaltung des gesamten Produkt- und Kategoriebestandes in Form eines Graphen. Diese Struktur erlaubt es, komplexe Beziehungen zwischen Produkten und Kategorien abzubilden. Beispielsweise kann eine Beziehung definieren, dass Produkt B ein Nachfolger von Produkt A ist. Wenn ein Benutzer Interesse an Produkt A zeigt, wird Produkt B als vielversprechende Empfehlung vorgeschlagen.
Wie es funktioniert:
Das System nutzt die im Graphen hinterlegten Beziehungen, um die Relevanz von Produkten zueinander zu bewerten. Durch die Auswahl und Kombination verschiedener Strategien kann der Benutzer bestimmen, welche Arten von Beziehungen und Gewichtungen bei der Generierung der Empfehlungen berücksichtigt werden sollen. Dies führt zu dynamischen und bedarfsgerechten Produktvorschlägen, die über einfache Kategoriengleichheit hinausgehen.
