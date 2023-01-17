2. (3 puncte) De ce in mod obisnuit in utilizarea unui lock se prefera ca apelul lock() sa fie executat inainte de blocul try, si nu in cadrul acestuia (prima varianta de mai jos si nu a doua)? Argumentati.

```java
lock inainte de try:

someLock.lock();
try {
   .....
}
finally {
   someLock.unlock();
}

lock in cadrul try:

try {
   someLock.lock();
   .....
}
finally {
   someLock.unlock();
}
```