A Lock implementation will impose restrictions on which thread can release a lock, meaning that only the holder can unlock it and may throw an exception if this restriction is not respected.

The difference is in what happens if the locking fails:
In the first example it can throw an exception that points to the first line, above the try block.
In the second example if it throws an exception, it will be caught in the try/catch block and in the finally block we will attempt to unlock a thread wich is in an invalid state.
