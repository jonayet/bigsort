module bigsort {
    requires java.base;
    requires java.logging;
    requires java.management;

    exports bigsort;
    exports bigsort.util;
    exports bigsort.util.api;
}