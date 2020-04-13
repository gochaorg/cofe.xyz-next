package xyz.cofe.io.fs.typeconv;

import xyz.cofe.io.fs.File;
import xyz.cofe.typeconv.spi.GetTypeConvertor;

import java.util.function.Function;

public class CofeIOFile2Path implements GetTypeConvertor {
    @Override
    public Class getSourceType(){
        return File.class;
    }

    @Override
    public Class getTargetType(){
        return java.nio.file.Path.class;
    }

    private Function fn = from -> ((File)from).path;

    @Override
    public Function<Object, Object> getConvertor(){
        return fn;
    }
}
