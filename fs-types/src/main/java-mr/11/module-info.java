open module xyz.cofe.io.fs.typeconv {
    requires java.base;
    requires transitive java.logging;
    requires xyz.cofe.typeconv.spi;
    requires xyz.cofe.io.fs;

    exports xyz.cofe.io.fs.typeconv;
    provides xyz.cofe.typeconv.spi.GetTypeConvertor
        with
            xyz.cofe.io.fs.typeconv.CofeIOFile2String,
            xyz.cofe.io.fs.typeconv.CofeIOFile2File,
            xyz.cofe.io.fs.typeconv.CofeIOFile2Path,
            xyz.cofe.io.fs.typeconv.JavaFile2CofeIOFile,
            xyz.cofe.io.fs.typeconv.Path2CofeIOFile,
            xyz.cofe.io.fs.typeconv.String2CofeIOFile;
}