#include <Xlib.h>
#include <stdio.h>

// compile with: g++ -o libxx.so -shared -fPIC -Wl,-soname,libxx.so -L/usr/lib/X11 -I/usr/include/X11 xdll.cpp -lX11
// GCC 4.9.2

class a
{
public:
    a()
    { 
        printf("XLIB: Initializing Multithreading...");
        fflush(stdout);
        XInitThreads();
        printf(" OK!\n");
        fflush(stdout);
    };
};

a X;

