# 5.
(5 puncte) Consideram algoritmul lui Peterson de excludere mutuala ce ofera o generalizare pentru n thread-uri enuntat in laboratorul 3, ale carui metode sunt redate in pseudocodul de mai jos. Reamintim ideea acestuia de a trece fiecare thread printr-un filtru de n-1 nivele pana la accesul la sectiunea critica. i poate fi considerat ca identificator al unui thread iar L ca numar al nivelului. Tabloul level asociat nivelelor retine nivelul curent pentru fiecare thread, iar tabloul victim retine identificatorul fiecarui ultim thread ce a avansat la respectivul nivel.

```java
lock() {
	for (int L = 1; L < n; L++) {
		level[i] = L;
		victim[L] = i;
		while (( exists k != i with level[k] >= L ) &&
			victim [L] == i ) {};
	}
}

unlock() {
	level[i] = 0;
}
```

De ce credeti ca algoritmul lui Peterson generalizat in acest mod nu este echilibrat (fair)? Incercati sa descrieti un exemplu concurent de executie ca raspuns.

Gasiti o varianta de imbunatatire pentru a asigura garantia de fairness si implementati algoritmul in acest mod. Odata pornite n thread-uri niciunul dintre acestea nu ar trebui sa poata accesa sectiunea critica protejata de lock mai des ca celelalte.